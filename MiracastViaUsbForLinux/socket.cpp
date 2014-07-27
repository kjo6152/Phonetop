#include <list>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include "rtsp.h"
#include "socket.h"

using namespace std;

struct sockaddr_in client_addr;
unsigned int client_addr_len = sizeof(client_addr);
unsigned char RTPBuffer[4096];
int FileDescriptor = 0;

string ReadRtspLine(int socket) {
	char buf[1024] = { '\0', };
	int cnt = 0, ret = -1;

	for (int cnt = 0; cnt < 1024; cnt++) {
		ret = read(socket, buf + cnt, 1);
		if (ret <= 0)
			break;
		if (*(buf + cnt) == '\n')
			break;
	}
	if (ret <= 0) {
		if (cnt == 0) {
			cout << "socket read error" << endl;
		}
	}
	cout << "ReadRtspLine : " << string(buf);
	return string(buf);
}
RtspPacket ReadRtspPacket(int socket) {
	RtspPacket mRtspPacket;
	RtspBody mRtspBody;
	string mstring;
	int remain_byte = 0;
	mRtspPacket.header->setHeader(ReadRtspLine(socket));
	while (true) {
		mstring = ReadRtspLine(socket);
		if (mstring.compare("\r\n") == 0) {
			if (mRtspPacket.content_length.length() > 0) {
				remain_byte = atoi(mRtspPacket.content_length.c_str());
				while (remain_byte > 0) {
					mstring = ReadRtspLine(socket);
					mRtspBody.setBody(mstring);
					mRtspPacket.addBody(mRtspBody);
					remain_byte -= mstring.length();
				}
			}
			break;
		}
		mRtspBody.setBody(mstring);
		mRtspPacket.addBody(mRtspBody);
	}
	return mRtspPacket;
}
void ReceiveRtspData(int socket, int *cur) {
	RtspPacket mRtspPacket = ReadRtspPacket(socket);

	cout << "Receive Data : " << endl;
	cout << mRtspPacket.toString();

	if (mRtspPacket.header->method.compare(RTSP_OPTIONS) == 0) {
		mRtspPacket.sendM1(socket);
		*cur = 1;
		mRtspPacket.sendM2(socket);
		*cur = 2;
	} else if (mRtspPacket.header->method.compare(RTSP_GET_PARAMETER) == 0) {
		if (*cur == 7) {
			mRtspPacket.sendConnection(socket);
		} else {
			mRtspPacket.sendM3(socket);
			*cur = 3;
		}
	} else if (mRtspPacket.header->method.compare(RTSP_SET_PARAMETER) == 0) {
		if (*cur == 3) {
			mRtspPacket.sendOKResponse(socket);
			*cur = 4;
		} else if (*cur == 4) {
			mRtspPacket.sendOKResponse(socket);
			*cur = 5;
			mRtspPacket.sendM6(socket);
			*cur = 6;
		}
	} else if (mRtspPacket.header->method.compare(RTSP_VERSION) == 0) {
		if (*cur == 6) {
			mRtspPacket.sendM7(socket);
			*cur = 7;
		}
	}
}

void SendRtspData(int socket, string data) {
	int ret = write(socket, data.c_str(), data.length());
	if (ret <= 0) {
		cout << "SendData Error" << endl;
		return;
	}
	cout << "Send Data - " << data.length() << " : " << endl;
	cout << data;
}

int ConnectRtspServer() {
	int server_fd;
	struct sockaddr_in serv_addr;

	printf("Wait I connect to server : %s\n", SERV_ADDR);
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = PF_INET;
	serv_addr.sin_addr.s_addr = inet_addr(SERV_ADDR);
	serv_addr.sin_port = htons(SERV_PORT);

	/* open a tcp socket*/
	if ((server_fd = socket(PF_INET, SOCK_STREAM, 0)) < 0) {
		printf("socket creation error\n");
		exit(1);
	}
	printf("socket opened successfully. socket num is %d\n", server_fd);

	/* connect to  the server */
	if (connect(server_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr))
			< 0) {
		printf("can't connect to the server\n");
		exit(1);
	}
	return server_fd;
}

int CreateRtpServer() {
	int server_fd;
	struct sockaddr_in serv_addr;

	cout << "CreateRtpServer..." << endl;
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_addr.sin_port = htons(CLIENT_PORT);

	/* open a tcp socket*/
	if ((server_fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		printf("socket creation error\n");
		exit(1);
	}
	printf("socket opened successfully. socket num is %d\n", server_fd);

	/* connect to  the server */
	if (bind(server_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr))
			< 0) {
		printf("can't connect to the server\n");
		exit(1);
	}
	return server_fd;
}
void ReceiveRtpData(int socket) {
	cout << "ReceiveRtpData" << endl;

	int total = 0;
	int ReadSize = 0;
	int RemainSize = 0;

	FileDescriptor = open("TSPacket.mpeg", O_WRONLY | O_CREAT,0666);
	if (FileDescriptor < 0)
		return;

	while (true) {
		ReadSize = recvfrom(socket, RTPBuffer+RemainSize, 4096 - RemainSize, 0, (struct sockaddr *) &client_addr,&client_addr_len);
		total += ReadSize;

		RemainSize = extractTSData(ReadSize+RemainSize);
		cout << "Total Rtp Receive Data : " << total << endl;
	}

}

int extractTSData(int RemainSize) {
	//If RTP Header
	int ptr = 0;
	while(1){
		if(RTPBuffer[ptr]==0x80 && RTPBuffer[ptr+1]==0x21){
			cout << "extractTSData : RTP Header" << endl;
			ptr += 12;
			RemainSize -= 12;
			continue;
		}else {
			cout << "extractTSData : TS Packet" << endl;
			if(RemainSize<188){
				memcpy(RTPBuffer,RTPBuffer+ptr,RemainSize);
				return RemainSize;
			}
			write(FileDescriptor, RTPBuffer+ptr, 188);
			ptr += 188;
			RemainSize -= 188;
		}
	}
	return 0;
}

void ReadRtpHeader(int socket, struct sockaddr_in client_addr) {
//	char data[1024];
//	unsigned int client_addr_len = sizeof(client_addr);
//	int ret = recvfrom(socket, data, 12, 0, (struct sockaddr *) &client_addr,
//			&client_addr_len);
//
//	int numSCRCs = data[0] & 0x0f;
//	size_t payloadOffset = 12 + 4 * numSCRCs;
//
//	if (data[0] & 0x10) {
//		// Header eXtension present.
//		const uint8_t *extensionData = &data[payloadOffset];
//		size_t extensionLength = 4 * (extensionData[2] << 8 | extensionData[3]);
//		payloadOffset += 4 + extensionLength;
	}


void OpenRtpServer() {
	cout << "OpenRtpServer" << endl;
	int Rtp_fd = CreateRtpServer();
	ReceiveRtpData(Rtp_fd);
}


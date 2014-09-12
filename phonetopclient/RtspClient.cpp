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
#include <thread>
#include "rtsp.h"
#include "RtspClient.h"

using namespace std;


void RtspClient::playVideo(){
//	system("/home/odroid/Desktop/[RTP]phonetopclient/phonetop rtp://192.168.42.129:24030");	
}
void RtspClient::runPlayVideo(){
//	thread RtspThread(&RtspClient::playVideo, this);
//	RtspThread.detach();	
}

string RtspClient::ReadRtspLine() {
	char buf[1024] = { '\0', };
	int cnt = 0, ret = -1;

	for (int cnt = 0; cnt < 1024; cnt++) {
		ret = read(this->RtspSocket, buf + cnt, 1);
		if (ret <= 0)
			break;
		if (*(buf + cnt) == '\n')
			break;
	}
	if (ret <= 0) {
		if (cnt == 0) {
//			cout << "socket read error" << endl;
		}
	}
//	cout << "ReadRtspLine : " << string(buf);
	return string(buf);
}
RtspPacket RtspClient::ReadRtspPacket() {
	RtspPacket mRtspPacket;
	RtspBody mRtspBody;
	string mstring;
	int remain_byte = 0;
	mRtspPacket.header->setHeader(ReadRtspLine());
	while (true) {
		mstring = ReadRtspLine();
		if (mstring.compare("\r\n") == 0) {
			if (mRtspPacket.content_length.length() > 0) {
				remain_byte = atoi(mRtspPacket.content_length.c_str());
				while (remain_byte > 0) {
					mstring = ReadRtspLine();
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
void RtspClient::ReceiveRtspData() {
	cout << "ReceiveRtspData" << endl;
	while (true) {
		try {
			RtspPacket mRtspPacket = ReadRtspPacket();

			cout << "Receive Data : " << endl;
			cout << mRtspPacket.toString();

			if (mRtspPacket.header->method.compare(RTSP_OPTIONS) == 0) {
				SendRtspData(mRtspPacket.getM1Message());
				RtspState = 1;
				SendRtspData(mRtspPacket.getM2Message());
				RtspState = 2;
			} else if (mRtspPacket.header->method.compare(RTSP_GET_PARAMETER)
					== 0) {
				if (RtspState == 8) {
					SendRtspData(mRtspPacket.getConnectionMessage());
				} else {
					SendRtspData(mRtspPacket.getM3Message());
					RtspState = 3;
				}
			} else if (mRtspPacket.header->method.compare(RTSP_SET_PARAMETER)
					== 0) {
				if (RtspState == 3) {
					SendRtspData(mRtspPacket.getOKMessage());
					RtspState = 4;
				} else if (RtspState == 4) {
					SendRtspData(mRtspPacket.getOKMessage());
					RtspState = 5;
					SendRtspData(mRtspPacket.getM6Message());
					RtspState = 6;
				}
			}
			else if (mRtspPacket.header->method.compare(RTSP_VERSION) == 0) {
				if (RtspState == 6) {
					SendRtspData(mRtspPacket.getPlayMessage());
					RtspState = 7;
				}else if(RtspState==7){
					SendRtspData(mRtspPacket.getPauseMessage());
					RtspState = 8;
				}
			}
		} catch (const char *message) {
			printf("%s\n", message);
			break;
		}
	}
	closeRtspClient();
}

void RtspClient::SendRtspData(string data) {
	int ret = write(this->RtspSocket, data.c_str(), data.length());
	if (ret <= 0) {
		cout << "SendData Error" << endl;
		throw "socket closed";
		return;
	}
	cout << "Send Data - " << data.length() << " : " << endl;
	cout << data;
}

int RtspClient::ConnectRtspServer() {
	cout << "ConnectRtspServer..." << endl;
	int server_fd;
	struct sockaddr_in serv_addr;

	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = PF_INET;
	serv_addr.sin_addr.s_addr = inet_addr(SERV_ADDR);
	serv_addr.sin_port = htons(RTSP_PORT);

	/* open a tcp socket*/
	if ((server_fd = socket(PF_INET, SOCK_STREAM, 0)) < 0) {
		printf("socket creation error\n");
		return -1;
	}

	/* connect to  the server */
	if (connect(server_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr))
			< 0) {
		printf("can't connect to the server\n");
		return -1;
	}

	this->RtspSocket = server_fd;
	return server_fd;
}



bool RtspClient::isRtspConnected() {
	if (RtspSocket > 0)
		return true;
	return false;
}
void RtspClient::runRtspClient() {
	thread RtspThread(&RtspClient::ReceiveRtspData, this);
	RtspThread.detach();
}

void RtspClient::closeRtspClient() {
	if (this->isRtspConnected()) {
		close(this->RtspSocket);
		this->RtspSocket = -1;
	}
}

void RtspClient::requestPause(){
	RtspPacket *mRtspPacket = new RtspPacket();
	SendRtspData(mRtspPacket->getPauseMessage());
}
void RtspClient::requestPlay(){
	RtspPacket *mRtspPacket = new RtspPacket();
	SendRtspData(mRtspPacket->getPlayMessage());
}

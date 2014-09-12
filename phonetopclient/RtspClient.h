#ifndef __RTSP__CLIENT__H__
#define __RTSP__CLIENT__H__

#include "rtsp.h"
#include <string>

#define SERV_ADDR "192.168.42.129"
#define RTSP_PORT 7236
#define RTP_PORT 24030
#define CLIENT_PORT2 8888

class RtspClient{
public:
	int RtspSocket;
	int RtpScoket;
	int FileDescriptor;
	int RtspState;
	bool RtspRunning;
	bool RtpRunning;
	struct sockaddr_in addr;
	unsigned int addr_len;
	unsigned char RTPBuffer[4096];

	string session;

	int cnt;
	struct sockaddr_in client_addr2;
	int connfd;
	unsigned int client_addr_len2;

	RtspClient(){
		RtspSocket = -1;
		RtpScoket = -1;
		FileDescriptor = -1;
		RtspState = 0;
		addr_len = sizeof(addr);
		RtspRunning = false;
		RtpRunning = false;

		session = string("");
		cnt=0;
		connfd=0;
		client_addr_len2=sizeof(client_addr2);
	}

	bool isRtspConnected();
	int ConnectRtspServer();
	string ReadRtspLine();
	RtspPacket ReadRtspPacket();
	void ReceiveRtspData();
	void SendRtspData(string data);
	void runRtspClient();
	void requestPause();
	void requestPlay();
	void closeRtspClient();
	void runPlayVideo();
	void playVideo();
};

#endif

#ifndef __RTSP__CLIENT__H__
#define __RTSP__CLIENT__H__

#include "rtsp.h"
#include <string>

#define SERV_ADDR "192.168.42.129"
#define RTSP_PORT 7236
#define RTP_PORT 24030

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

	RtspClient(){
		RtspSocket = -1;
		RtpScoket = -1;
		FileDescriptor = -1;
		RtspState = 0;
		addr_len = sizeof(addr);
		RtspRunning = false;
		RtpRunning = false;
	}

	bool isRtspConnected();
	bool isRtpOpened();
	int ConnectRtspServer();
	string ReadRtspLine();
	RtspPacket ReadRtspPacket();
	void ReceiveRtspData();
	void SendRtspData(string data);
	int CreateRtpServer();
	void ReceiveRtpData();
	void OpenRtpClient();
	int extractTSData(int RemainSize);
	void runRtspClient();
	void closeRtspClient();
};

#endif

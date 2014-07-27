#ifndef __SOCKET__H__
#define __SOCKET__H__

#include "rtsp.h"
#include <string>

#define SERV_ADDR "192.168.42.129"
#define SERV_PORT 7236
#define CLIENT_PORT 24030

int ConnectRtspServer();
string ReadRtspLine(int socket);
RtspPacket ReadRtspPacket(int socket);
void ReceiveRtspData(int socket,int *cur);
void SendRtspData(int socket,string data);
int CreateRtpServer();
int CreateRtcpServer();
void ReceiveRtpData(int socket);
void ReceiveRtcpData(int socket);
void OpenRtpServer();
void OpenRtcpServer();
int extractTSData(int RemainSize);
#endif

#ifndef __RTSP__H__
#define __RTSP__H__

#include <list>
#include <iostream>
#include <string>
using namespace std;

//RTSP STRING DEFINE

#define RTSP_RESPONSE_OK_NUMBER				"200"
#define RTSP_RESPONSE_OK_STR				"OK"
#define RTSP_CSEQ								"Cseq"
#define RTSP_PROVIDER						"org.wfa.wfd1.0"
#define RTSP_CONTENT_LENGTH					"content-length"
#define RTSP_CONTENT_TYPE					"content-type"
#define RTSP_CONTENT_PROTECTION				"wfd_content_protection"
#define RTSP_VIDEO_FORMATS					"wfd_video_formats"
#define RTSP_AUDIO_CODECS					"wfd_audio_codecs"
#define RTSP_RTP_PORTS						"wfd_client_rtp_ports"
#define RTSP_TRANSPORT						"transport"

//RTSP METHODS DEFINE
#define RTSP_VERSION							"RTSP/1.0"
#define RTSP_OPTIONS							"OPTIONS"
#define RTSP_GET_PARAMETER					"GET_PARAMETER"
#define RTSP_SET_PARAMETER					"SET_PARAMETER"
#define RTSP_PLAY								"PLAY"

class RtspHeader{
public:
	string method;
	string url;
	string header;
	void setHeader(string method,string url);
	void setHeader(string header);
	string getHeader();
	string getResponseOK();
	string toString();
};

class RtspBody{
public:
	string head;
	string content;
	string body;
	string getBody();
	RtspBody();
	RtspBody(string head,string content);
	void setBody(string head,string content);
	void setBody(string body);
	string toString();
};
class RtspPacket{
public:
	bool response;
	string cseq;
	string session;
	string content_length;
	RtspHeader *header;
	list<RtspBody> *body_list;

	RtspPacket();
	~RtspPacket();
	void addBody(RtspBody mRtspBody);
	string toString();

	string getM1Message();
	string getM2Message();
	string getM3Message();
	string getM6Message();
	string getM7Message();
	string getOKMessage();
	string getConnectionMessage();
};

#endif

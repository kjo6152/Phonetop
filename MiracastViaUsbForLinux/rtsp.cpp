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
#include <sstream>
#include "rtsp.h"
#include "stringutils.h"
#include "socket.h"

using namespace std;

#define RESOLUTION_640_480_P60		0x000
#define RESOLUTION_720_480_P60		0x001
#define RESOLUTION_1280_720_P30		0x02f
#define RESOLUTION_1280_720_P60		0x05f
#define RESOLUTION_1920_1080_P30	0x0ff
#define RESOLUTION_1920_1080_P60	0xfff


// RtspHeader Class Methods
void RtspHeader::setHeader(string method, string url) {
	this->method = method;
	this->url = url;
	this->header = method + " " + url + " " + string(RTSP_VERSION) + "\r\n";
}
void RtspHeader::setHeader(string header) {
	this->header = header;
	string *strResult = new string[3];
	int len = StringSplit(header, " ", strResult);
	if (len < 3) {
		cout << "header set error" << endl;
		return;
	}
	this->method = strResult[0];
	this->url = strResult[1];
}
string RtspHeader::getHeader() {
	return header;
}
string RtspHeader::getResponseOK() {
	return string(RTSP_VERSION) + " " + string(RTSP_RESPONSE_OK_NUMBER) + " "
			+ string(RTSP_RESPONSE_OK_STR) + "\r\n";
}
string RtspHeader::toString() {
	return getHeader();
}

RtspBody::RtspBody(){
	setBody("","");
}
// RtspBody Class Methods
RtspBody::RtspBody(string head,string content){
	setBody(head,content);
}
string RtspBody::getBody() {
	return body;
}

void RtspBody::setBody(string head, string content) {
	this->head = head;
	this->content = content;
	this->body = head + ": " + content + "\r\n";
}
void RtspBody::setBody(string body) {
	this->body = body;

	int index = body.find(":");
	if (index != string::npos) {
		head = string(body, 0, index);
		content = string(body, index + 2, body.length());
	} else {
		this->head = body;
		this->content = string("");
	}
}
string RtspBody::toString() {
	return getBody();
}

// RtspPacket Class Methods
RtspPacket::RtspPacket() {
	response = false;
	cseq = string("");
	session = string("");
	content_length = string("");
	header = new RtspHeader();
	body_list = new list<RtspBody>();
}
RtspPacket::~RtspPacket() {
	delete header;
	delete body_list;
}
void RtspPacket::addBody(RtspBody mRtspBody) {
	body_list->push_back(mRtspBody);
	if (mRtspBody.head.compare("CSeq") == 0) {
		this->cseq = mRtspBody.content.substr(0,mRtspBody.content.length()-2);
	} else if (mRtspBody.head.compare("Content-Length") == 0) {
		this->content_length = mRtspBody.content;
	} else if (mRtspBody.head.compare("Session") == 0) {
		int index = mRtspBody.content.find(";");
		if(index!= string::npos)this->session = string(mRtspBody.content, 0, index);
		else this->session = mRtspBody.content;
	}
}
string RtspPacket::toString() {
	string result;
	if(this->response){
		result = header->getResponseOK();
	}else  result = header->toString();

	list<RtspBody>::iterator iter = body_list->begin();
	for (; iter != body_list->end(); iter++) {
		result += ((RtspBody)(*iter)).toString();
	}
	return result + "\r\n";
}

void RtspPacket::sendM1(int socket) {
	RtspPacket *mRtspPacket = new RtspPacket();
	RtspBody mRtspBody = RtspBody("","");
	mRtspPacket->response = true;
	mRtspBody.setBody("Cseq",this->cseq);
	mRtspPacket->addBody(mRtspBody);
	mRtspBody.setBody("public","org,wfa,wfd1.0, SET_PARAMETER,GET_PARAMETER");
	mRtspPacket->addBody(mRtspBody);
	SendRtspData(socket,mRtspPacket->toString());
}
void RtspPacket::sendM2(int socket) {
	RtspPacket *mRtspPacket = new RtspPacket();
	RtspBody mRtspBody = RtspBody("","");
	mRtspPacket->response = false;
	mRtspPacket->header->setHeader("OPTIONS","*");
	mRtspBody.setBody("Cseq","309");
	mRtspPacket->addBody(mRtspBody);
	mRtspBody.setBody("require","org.wfa.wfd1.0");
	mRtspPacket->addBody(mRtspBody);
	SendRtspData(socket,mRtspPacket->toString());
}
void RtspPacket::sendM3(int socket) {
	RtspPacket *mRtspPacket = new RtspPacket();
	RtspBody mRtspBody = RtspBody("","");
	mRtspPacket->response = true;

	string content = "wfd_content_protection: none\r\n";
//	content += "wfd_video_formats: 38 01 01 08 0001deff 07ffffff 00000fff 02 0000 0000 11 0780 0438\r\n";
	content += "wfd_video_formats: 38 01 01 08 000000ff 00000000 00000000 02 0000 0000 11 0780 0438\r\n";
	content += "wfd_audio_codecs: LPCM 00000003 02, AAC 0000000f 02, AC3 00000007 02\r\n";
	content += "wfd_client_rtp_ports: RTP/AVP/UDP;unicast 24030 0 mode=play\r\n";

	ostringstream oss;
	oss << content.length();
	mRtspBody.setBody("content-length",oss.str());
	mRtspPacket->addBody(mRtspBody);
	mRtspBody.setBody("content-type","text/parameters");
	mRtspPacket->addBody(mRtspBody);
	mRtspBody.setBody("Cseq",this->cseq);
	mRtspPacket->addBody(mRtspBody);
	SendRtspData(socket,mRtspPacket->toString());
	SendRtspData(socket,content);
}
void RtspPacket::sendM6(int socket) {
	RtspPacket *mRtspPacket = new RtspPacket();
	RtspBody mRtspBody = RtspBody("","");
	mRtspPacket->response = false;
	mRtspPacket->header->setHeader("SETUP","rtsp://192.168.42.129/wfd1.0/streamid=0");
	mRtspBody.setBody("Cseq","310");
	mRtspPacket->addBody(mRtspBody);
	mRtspBody.setBody("transport","RTP/AVP/UDP;unicast;client_port=24030");
	mRtspPacket->addBody(mRtspBody);
	SendRtspData(socket,mRtspPacket->toString());
}
void RtspPacket::sendM7(int socket) {
	RtspPacket *mRtspPacket = new RtspPacket();
	RtspBody mRtspBody = RtspBody("","");
	mRtspPacket->response = false;
	mRtspPacket->header->setHeader("PLAY","rtsp://192.168.42.129/wfd1.0/streamid=0");
	mRtspBody.setBody("Cseq","311");
	mRtspPacket->addBody(mRtspBody);
	mRtspBody.setBody("session",this->session);
	mRtspPacket->addBody(mRtspBody);
	SendRtspData(socket,mRtspPacket->toString());
}
void RtspPacket::sendOKResponse(int socket) {
	RtspPacket *mRtspPacket = new RtspPacket();
	RtspBody mRtspBody = RtspBody("","");
	mRtspPacket->response = true;
	mRtspBody.setBody("Cseq",this->cseq);
	mRtspPacket->addBody(mRtspBody);
	SendRtspData(socket,mRtspPacket->toString());
}
void RtspPacket::sendConnection(int socket){
	RtspPacket *mRtspPacket = new RtspPacket();
	RtspBody mRtspBody = RtspBody("","");
	mRtspPacket->response = true;
	mRtspBody.setBody("Cseq",this->cseq);
	mRtspPacket->addBody(mRtspBody);
	SendRtspData(socket,mRtspPacket->toString());
}

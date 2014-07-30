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
#include <thread>
#include "RtspClient.h"
#include "rtsp.h"
#include "input.h"

using namespace std;

int main() {
	cout << "hello world" << endl;

	//프로세스분기 및 종료
//	pid_t pid = fork();
//	if (pid > 0)
//		return 0;


	RtspClient *mRtspClient = new RtspClient();
	InputClient *mInputClient = new InputClient();

	//미라캐스트 연결
	mRtspClient->ConnectRtspServer();
	mRtspClient->CreateRtpServer();
	if(!mRtspClient->isRtspConnected() && !mRtspClient->isRtpOpened()){
		cout << "RTSP Connect Error!" << endl;
		return 0;
	}else {
		//New thread is UDP/RTP
		mRtspClient->runRtspClient();
	}

	//Input Client 연결
	mInputClient->ConnectInputServer();
	if(!mInputClient->isInputConnected()){
		cout << "Input Connect Error!" << endl;
		return 0;
	}else {
		mInputClient->KeyboardOpen(2);
		mInputClient->MouseOpen(8);
		if(!mInputClient->isKeyboardOpened() && !mInputClient->isMouseOpened()){
			cout << "Event Open Error!" << endl;
		}else {
			mInputClient->runInputClient();
		}
	}

	cout << "close Client..." << endl;
	mInputClient->closeInputClient();
	mRtspClient->closeRtspClient();

	return 0;
}


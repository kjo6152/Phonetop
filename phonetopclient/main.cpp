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
#include <signal.h>
#include "RtspClient.h"
#include "rtsp.h"
#include "input.h"

using namespace std;

#define INPUT_MOUSE_START			1
#define INPUT_MOUSE_SLEEP			2
#define INPUT_KEYBOARD_START		3
#define INPUT_KEYBOARD_SLEEP		4
#define INPUT_MONITOR_START			5
#define INPUT_MONITOR_SLEEP			6
#define INPUT_MONITOR_PORTRAIT		7
#define INPUT_MONITOR_LANDSCAPE		8

RtspClient *mRtspClient = NULL;
InputClient *mInputClient = NULL;

void closeApp(int signum){
	if(signum==SIGRTMIN||signum==SIGRTMIN+1)return;
	cout << "close Client..." << endl;
	if(mRtspClient!=NULL)mRtspClient->closeRtspClient();
	if(mInputClient!=NULL)mInputClient->closeInputClient();
	exit(0);
}

void receiveMode(int InputSocket) {
	cout << "receiveMode"	<< endl;
	char mode;
	int ret = 0;
	while (true) {
		ret = read(InputSocket, &mode, 1);
		if (ret <= 0) {
			printf("failed to read input event from input device");
			break;
		}

		printf("mode : %d, ret : %d\n", mode, ret);

		switch (mode){
			case INPUT_MOUSE_START:
				mInputClient->isSleepMouse = false;
				break;
			case INPUT_MOUSE_SLEEP:
				mInputClient->isSleepMouse = true;
				break;
			case INPUT_KEYBOARD_START:
				mInputClient->isSleepKeyboard = false;
				break;
			case INPUT_KEYBOARD_SLEEP:
				mInputClient->isSleepKeyboard = true;
				break;
			case INPUT_MONITOR_START:
				mRtspClient->requestPlay();
				break;
			case INPUT_MONITOR_SLEEP:
				mRtspClient->requestPause();
				break;
			case INPUT_MONITOR_PORTRAIT:
				system("pkill -34 phonetop");
				break;
			case INPUT_MONITOR_LANDSCAPE:
				system("pkill -35 phonetop");
				break;
		}
	}
}

int main() {
	signal(SIGINT, closeApp);
	signal(SIGKILL, closeApp);
	signal(SIGILL, closeApp);
	signal(SIGRTMIN, closeApp);
	signal(SIGRTMIN+1, closeApp);

	//프로세스분기 및 종료
	pid_t pid = fork();
	if (pid > 0)
		return 0;


	mRtspClient = new RtspClient();
	mInputClient = new InputClient();

	//미라캐스트 연결, 서버와 연결 가능한 상태까지 계속 연결 시도
	int ret = 0;
	while(true){
		ret = mRtspClient->ConnectRtspServer();
		if(ret<0)sleep(1);
		else break;
	}
	if(!mRtspClient->isRtspConnected() ){
		cout << "RTSP Connect Error!" << endl;
		return 0;
	}else {
		//New thread is UDP/RTP
		mRtspClient->runPlayVideo();
		mRtspClient->runRtspClient();
	}

	//Input Client 연결
	int InputSocket = mInputClient->ConnectInputServer();
	if(!mInputClient->isInputConnected()){
		cout << "Input Connect Error!" << endl;
	}else {
//		mInputClient->KeyboardOpen(mInputClient->FindKeyboardEvent());
//		mInputClient->MouseOpen(mInputClient->FindMouseEvent());
		mInputClient->PipeOpen();
		mInputClient->KeyboardOpen(2);
		mInputClient->MouseOpen(8);
		if(!mInputClient->isKeyboardOpened() && !mInputClient->isPipeOpened() && !mInputClient->isMouseOpened()){
			cout << "Event Open Error!" << endl;
		}else {
			mInputClient->runInputClient();
			receiveMode(InputSocket);
		}
	}

	closeApp(0);
	return 0;
}


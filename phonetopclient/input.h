#ifndef __INPUT__CLIENT__H__
#define __INPUT__CLIENT__H__

#include <thread>

#define INPUT_PORT 6155                      // 연결할 포트
#define SERV_ADDR "192.168.42.129"              // 접속할 서버의 IP
#define NONE_DEVICE 4
#define INPUT_KEYBOARD 1
#define INPUT_MOUSE 2
#define INPUT_ALLDEVICE 3
#define EVENT_PATH "/dev/input/event"

#pragma pack(1)

struct input_event32 {
	unsigned int tv_sec;
	unsigned int tv_usec;
	unsigned short type;
	unsigned short code;
	int value;
};

class InputClient {
public:
	int InputSocket;
	int KeyboardFd;
	int MouseFd;
	bool isSleepKeyboard;
	bool isSleepMouse;

	InputClient() {
		InputSocket = -1;
		KeyboardFd = -1;
		MouseFd = -1;
		isSleepKeyboard = true;
		isSleepMouse = true;
	}

	int ConnectInputServer();
	bool isInputConnected();
	bool isKeyboardOpened();
	bool isMouseOpened();
	int KeyboardOpen(int number);
	int MouseOpen(int number);
	void runInputClient();
	void sendKeyboardEvent();
	void sendMouseEvent();
	void receiveMode();
	void closeInputClient();
};


#endif

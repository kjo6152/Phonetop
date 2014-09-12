#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <string>
#include <unistd.h>
#include <linux/input.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <signal.h>
#include <unistd.h>
#include <stdlib.h>
#include <sstream>
#include <thread>
#include <iostream>
#include "input.h"

using namespace std;


int InputClient::FindMouseEvent(){
	struct input_event event, event_end;
	char name[256] = "Unknown";
	int i=2;
	int fd;
        char inputEvent[40];
	char *ptr;
	int breakPoint=0;
	while(1){
		breakPoint=0;
		if(i>15)break;
		char buf[10]="";	
		char ev[40]="/dev/input/event";
		snprintf(buf , sizeof(buf),"%d",i);	
		
		strcat(ev,buf);
		int fd; 
	        fd = open(ev,O_RDONLY);  
	        if(fd > 0){  
		 char name[80] = "";  
		 ioctl(fd, EVIOCGNAME(sizeof(name)),name);
		 printf("name=%s \n",name); 
		 ptr=strtok(name," ");
			while(ptr!=NULL){
				printf("ptr : %s\n",ptr);		
				if(strcmp(ptr,"MOUSE")==0||strcmp(ptr,"Mouse")==0){
					//inputEvent = name;
					strcpy(inputEvent,ev);
					breakPoint=1;
					break;
				}
				ptr=strtok(NULL," ");
			}
			if(breakPoint==1) break;
		} 
		
		i++;
		printf("\n");
	}
	return i;

}

int InputClient::FindKeyboardEvent(){
	struct input_event event, event_end;
	char name[256] = "Unknown";
	int i=2;
	int fd;
	char inputEvent[40];
	char *ptr;
	int breakPoint=0;
	while(1){
		breakPoint=0;
		if(i>15)break;
		char buf[10]="";	
		char ev[40]="/dev/input/event";
		snprintf(buf , sizeof(buf),"%d",i);	
		
		strcat(ev,buf);
		int fd; 
	        fd = open(ev,O_RDONLY);  
	        if(fd > 0){  
		 char name[80] = "";  
		 ioctl(fd, EVIOCGNAME(sizeof(name)),name);
		 printf("name=%s \n",name); 
		 ptr=strtok(name," ");
			while(ptr!=NULL){
				printf("ptr : %s\n",ptr);		
				if(strcmp(ptr,"KEYBOARD")==0||strcmp(ptr,"Keyboard")==0||strcmp(ptr,"HID")==0){
					//inputEvent = name;
					strcpy(inputEvent,ev);
					breakPoint=1;
					break;
				}
				ptr=strtok(NULL," ");
			}
			if(breakPoint==1) break;
		} 
		
		i++;
		printf("\n");
	}
	return i;
}

void InputClient::runInputClient() {
	thread KeyboardThread(&InputClient::sendKeyboardEvent, this);
	KeyboardThread.detach();
	thread MouseThread(&InputClient::sendMouseEvent, this);
	MouseThread.detach();
}
bool InputClient::isInputConnected() {
	if (InputSocket > 0)
		return true;
	return false;
}
int InputClient::ConnectInputServer() {
	cout << "ConnectInputServer..." << endl;
	int server_fd;
	struct sockaddr_in serv_addr;

	printf("Wait I connect to server : %s\n", SERV_ADDR);
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = PF_INET;
	serv_addr.sin_addr.s_addr = inet_addr(SERV_ADDR);
	serv_addr.sin_port = htons(INPUT_PORT);

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
	InputSocket = server_fd;
	return server_fd;
}

int InputClient::KeyboardOpen(int number) {
	string path = EVENT_PATH + to_string(number);
	cout << "KeyboardOpen : " << path << endl;
	int fd = open(path.c_str(), O_RDWR);
	KeyboardFd = fd;
	return fd;
}

int InputClient::MouseOpen(int number) {
	string path = EVENT_PATH + to_string(number);
	cout << "MouseOpen : " << path << endl;
	int fd = open(path.c_str(), O_RDWR);
	MouseFd = fd;
	return fd;
}
bool InputClient::isKeyboardOpened() {
	if (KeyboardFd > 0)
		return true;
	return false;
}
bool InputClient::isMouseOpened() {
	if (MouseFd > 0)
		return true;
	return false;
}
void InputClient::sendKeyboardEvent() {
	cout << "sendKeyboardEvent"	<< endl;
	struct input_event event;
	struct input_event32 event32;

	while (true) {
		if (read(this->KeyboardFd, &event, sizeof(struct input_event)) < 0) {
			printf("failed to read input event from input device");
			if (errno == EINTR)
				continue;
			break;
		}
		if (isSleepKeyboard) continue;
//		printf("type : %d ,code : %d ,value : %d ,size : %d\n", event.type,event.code, event.value, (int) sizeof(struct input_event));

//64bit일 경우 32bit로 변경
		if (sizeof(struct input_event) == 24) {
			event32.type = event.type;
			event32.code = event.code;
			event32.value = event.value;

			event32.type = htons(event32.type);
			event32.code = htons(event32.code);
			event32.value = htonl(event32.value);

			write(this->InputSocket, &event32, sizeof(struct input_event32));
		} else {
			event.type = htons(event.type);
			event.code = htons(event.code);
			event.value = htonl(event.value);
			write(this->InputSocket, &event, sizeof(struct input_event));
		}
	}
	closeInputClient();
}

void InputClient::sendMouseEvent() {
	cout << "sendMouseEvent"	<< endl;
	struct input_event event;
	struct input_event32 event32;

	while (true) {
		if (read(this->MouseFd, &event, sizeof(struct input_event)) < 0) {
			printf("failed to read input event from input device");
			if (errno == EINTR)
				continue;
			break;
		}
		if (isSleepMouse) continue;
//		printf("type : %d ,code : %d ,value : %d ,size : %d\n", event.type,event.code, event.value, (int) sizeof(struct input_event));

//64bit일 경우 32bit로 변경
		if (sizeof(struct input_event) == 24) {
			event32.type = event.type;
			event32.code = event.code;
			event32.value = event.value;

			event32.type = htons(event32.type);
			event32.code = htons(event32.code);
			event32.value = htonl(event32.value);

			write(this->InputSocket, &event32, sizeof(struct input_event32));
		} else {
			event.type = htons(event.type);
			event.code = htons(event.code);
			event.value = htonl(event.value);
			write(this->InputSocket, &event, sizeof(struct input_event));
		}
	}
	closeInputClient();
}

void InputClient::closeInputClient(){
	if(this->isInputConnected()){
		close(this->InputSocket);
		this->InputSocket = -1;
	}
	if(this->isKeyboardOpened()){
		close(this->KeyboardFd);
		this->KeyboardFd = -1;
	}
	if(this->isMouseOpened()){
		close(this->MouseFd);
		this->MouseFd = -1;
	}
}

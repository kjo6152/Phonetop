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
#include "socket.h"
#include "rtsp.h"


using namespace std;

int main() {
	cout << "hello world" << endl;

	int cnt = 0;
	int socket_fd = ConnectRtspServer();
	if (socket_fd <= 0) {
		cout << "connectRtspServer error" << endl;
		return 0;
	}

	//New thread is UDP/RTP
	thread mthread(OpenRtpServer);

	//Main thread is Rtsp
	while (true) {
		ReceiveRtspData(socket_fd, &cnt);
	}

	//Wait Thread End
	mthread.join();

	return 0;
}



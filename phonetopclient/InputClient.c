#include <stdio.h> 
#include <sys/types.h> 
#include <sys/socket.h> 
#include <netinet/in.h> 
#include <arpa/inet.h> 
#include <string.h>
#include <unistd.h>
#include <linux/input.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/time.h>

#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>
#define INPUT_PORT 6155                      // 연결할 포트
#define SERV_ADDR "192.168.42.129"              // 접속할 서버의 IP
#define NONE_DEVICE 4
#define INPUT_KEYBOARD 1
#define INPUT_MOUSE 2
#define INPUT_ALLDEVICE 3

int sleep_mouse = 1;
int sleep_keyboard = 1;
int middle_server_fd = -1;

#pragma pack(1)

struct input_event32 {
	unsigned int tv_sec;
	unsigned int tv_usec;
	unsigned short type;
	unsigned short code;
	int value;
};

struct input_event32 event32;
void *keyboard_function() {

	//write input_event
	struct input_event event;
	int i;

	int fd = open("/dev/input/event2", O_RDWR);

	if (!fd) {
		printf("Errro open mouse:%s\n", strerror(errno));
		return;
	}

	while (1) {
		while (sleep_keyboard) {
			sleep(1);
		}
		if (read(fd, &event, sizeof(struct input_event)) < 0) {
			printf("failed to read input event from input device");
			if (errno == EINTR)
				continue;
//			break;
		}
		printf("sand message!!\n");
		printf("type : %d ,code : %d ,value : %d ,size : %d\n",event.type,event.code,event.value,(int)sizeof(struct input_event));

//64bit일 경우 32bit로 변경
		if (sizeof(struct input_event) == 24) {
			event32.type = event.type;
			event32.code = event.code;
			event32.value = event.value;

			event32.type = htons(event32.type);
			event32.code = htons(event32.code);
			event32.value = htonl(event32.value);

			write(middle_server_fd, &event32, sizeof(struct input_event32));
		} else {
			event.type = htons(event.type);
			event.code = htons(event.code);
			event.value = htonl(event.value);
			write(middle_server_fd, &event, sizeof(struct input_event));
		}
	}
	close(fd);
}

void *mouse_function() {

	//write input_event
	struct input_event event;
	int i;

	int fd = open("/dev/input/event8", O_RDWR);

	if (!fd) {
		printf("Errro open mouse:%s\n", strerror(errno));
		return;
	}
	while (1) {
		while (sleep_mouse) {
			sleep(1);
		}
		if (read(fd, &event, sizeof(struct input_event)) < 0) {
			printf("failed to read input event from input device");
			if (errno == EINTR)
				continue;
//			break;
		}
		printf("sand message!!\n");
		printf("type : %d ,code : %d ,value : %d ,size : %d\n",event.type,event.code,event.value,(int)sizeof(struct input_event));

//64bit일 경우 32bit로 변경
		if (sizeof(struct input_event) == 24) {
			event32.type = event.type;
			event32.code = event.code;
			event32.value = event.value;

			event32.type = htons(event32.type);
			event32.code = htons(event32.code);
			event32.value = htonl(event32.value);

			write(middle_server_fd, &event32, sizeof(struct input_event32));
		} else {
			event.type = htons(event.type);
			event.code = htons(event.code);
			event.value = htonl(event.value);
			write(middle_server_fd, &event, sizeof(struct input_event));
		}
	}
	close(fd);
}

void *mode_function() {
	char mode;
	int ret = 0;
	while (1) {
		ret = read(middle_server_fd, &mode, 1);
		if (ret <= 0) {
			printf("failed to read input event from input device");
			break;
		}

		printf("mode : %d, ret : %d\n",mode,ret);

		if (mode == NONE_DEVICE) {
			sleep_mouse = 1;
			sleep_keyboard = 1;
		} else if (mode == INPUT_ALLDEVICE) {
			sleep_mouse = 0;
			sleep_keyboard = 0;
		} else if (mode == INPUT_MOUSE) {
			sleep_mouse = 0;
			sleep_keyboard = 1;
		} else if (mode == INPUT_KEYBOARD) {
			sleep_mouse = 1;
			sleep_keyboard = 0;
		}
	}
}

main() {
	int x;
	struct sockaddr_in serv_addr;
	char buf[4];

	//프로세스분기 및 종료
//	pid_t pid = fork();
//	if (pid > 0)
//		return;


	printf("Hi, I am the client\n");
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = PF_INET;
	serv_addr.sin_addr.s_addr = inet_addr(SERV_ADDR);
    serv_addr.sin_port = htons(INPUT_PORT);
	/* open a tcp socket*/
	if ((middle_server_fd = socket(PF_INET, SOCK_STREAM, 0)) < 0) {
		printf("socket creation error\n");
		exit(1);
	}
	printf(" socket opened successfully. socket num is %d\n", middle_server_fd);

	/* connect to  the server */
	if (connect(middle_server_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
		printf("can't connect to the server\n");
		exit(1);
	}

	char mm;
	read(middle_server_fd, &mm, sizeof(mm));
	/* send input str to the server */
	printf("now i am connected to the erver. enter a string to send\n");

	pthread_t p_thread[3];
	int thr_id;
	int status;

	// 쓰레드 생성 아규먼트로 1 을 넘긴다.
	thr_id = pthread_create(&p_thread[0], NULL, keyboard_function, NULL);
	if (thr_id < 0) {
		perror("thread create error : ");
		exit(0);
	}

	// 쓰레드 생성 아규먼트로 2 를 넘긴다.
	thr_id = pthread_create(&p_thread[1], NULL, mouse_function, NULL);
	if (thr_id < 0) {
		perror("thread create error : ");
		exit(0);
	}

	// 쓰레드 생성 아규먼트로 3 를 넘긴다.
	thr_id = pthread_create(&p_thread[2], NULL, mode_function, NULL);
	if (thr_id < 0) {
		perror("thread create error : ");
		exit(0);
	}

	// 쓰레드 종료를 기다린다.
	pthread_join(p_thread[0], (void **) &status);
	pthread_join(p_thread[1], (void **) &status);
	pthread_join(p_thread[2], (void **) &status);

	close(x);
	return;
}


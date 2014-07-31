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
#include "stringutils.h"

using namespace std;

int readline(int fd, char *bufptr, size_t len) {
	/* Note that this function is very tricky.  It uses the
	 static variables bp, cnt, and b to establish a local buffer.
	 The recv call requests large chunks of data (the size of the buffer).
	 Then if the recv call reads more than one line, the overflow
	 remains in the buffer and it is made available to the next call
	 to readline.
	 Notice also that this routine reads up to '\n' and overwrites
	 it with '\0'. Thus if the line is really terminated with
	 "\r\n", the '\r' will remain unchanged.
	 */
	char *bufx = bufptr;
	static char *bp;
	static int cnt = 0;
	static char b[1024];
	char c;

	while (--len > 0) {
		if (--cnt <= 0) {
			cnt = recv(fd, b, sizeof(b), 0);
			if (cnt < 0) {
				if (errno == EINTR) {
					len++; /* the while will decrement */
					continue;
				}
				return -1;
			}
			if (cnt == 0)
				return 0;
			bp = b;
		}
		c = *bp++;
		*bufptr++ = c;
		if (c == '\n') {
			*bufptr = '\0';
			return bufptr - bufx;
		}
	}
	return -1;
}

int StringSplit(string strOrigin, string strTok, string* strResult) {
	int cutAt;                            //자르는위치
	int index = 0;                    //문자열인덱스

	//strTok을찾을때까지반복
	while ((cutAt = strOrigin.find_first_of(strTok)) != strOrigin.npos) {
		//자르는위치가0보다크면(성공시)
		if (cutAt > 0) {
			strResult[index++] = strOrigin.substr(0, cutAt);  //결과배열에추가
		}
		strOrigin = strOrigin.substr(cutAt + 1);  //원본은자른부분제외한나머지
	}
	//원본이아직남았으면
	if (strOrigin.length() > 0) {
		strResult[index++] = strOrigin;  //나머지를결과배열에추가
	}
	return index;  //결과return
}

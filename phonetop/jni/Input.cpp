#include "include/Input.h"
#include "include/suinput.h"
#include <stdlib.h>

/**
 * Input File descriptor
 */
int inputFd = -1;

bool openInput(int scrWidth, int scrHeight){
	system("su -c \"chmod 666 /dev/uinput\"");
	return openInputWithoutPermission(scrWidth, scrHeight);
}

bool openInputWithoutPermission(int scrWidth, int scrHeight){
	LOGD(LOGTAG, "Opening input device...");
	struct input_id id = {
			BUS_VIRTUAL, /* Bus type. */
			1, /* Vendor id. */
			1, /* Product id. */
			1 /* Version id. */
	};

	if((inputFd = suinput_open("qwerty", &id, scrWidth, scrHeight)) == -1){
		LOGD(LOGTAG, "Cannot open device - 'qwerty'");
		return false;
	}
	LOGI(LOGTAG, "Opened device 'qwerty'");
	return true;
}

void closeInput(){
	LOGD(LOGTAG, "Closing input device...");
	if(inputFd!=-1){
		if(suinput_close(inputFd)==-1){
			LOGD(LOGTAG, "Error closing input device..");
		}
		LOGI(LOGTAG, "Device closed.");
		system("su -c \"chmod 660 /dev/uinput\"");
		inputFd = -1;
	}else{
		LOGI(LOGTAG, "Nothing to close. (Device has not opened)");
	}
}

void closeInputWithoutRevertPermission(){
	LOGD(LOGTAG, "Closing input device...");
	if(inputFd==-1){
		if(suinput_close(inputFd)==-1){
			LOGD(LOGTAG, "Error closing input device..");
		}
		LOGI(LOGTAG, "Device closed.");
	}else{
		LOGI(LOGTAG, "Nothing to close.");
	}
}

int sendNativeEvent(int uinput_fd, uint16_t type, uint16_t code, int32_t value){
	LOGI(LOGTAG, "Input Receive!!!.");

	return suinput_write(uinput_fd, type, code, value);
}

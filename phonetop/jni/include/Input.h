#ifndef INPUT_H_
#define INPUT_H_

#include "include/LogHelper.h"
#include <linux/input.h>
#include "include/suinput.h"

#define BUS_VIRTUAL 0x06
#define LOGTAG "PassUTAG"

/**
 * Open input device using suinput.
 * It will change /dev/uinput's permission to 666 first to read/write event from app, then attempt to open device. set true, uses su -c to change /dev/uinput permission to 666.
 * @return true if open succeeds, false otherwise.
 */
bool openInput(const int scrWidth, const int scrHeight);

bool openInputWithoutPermission(const int scrWidth, const int scrHeight);

/**
 * Close input device.
 */
void closeInput();

/**
 * Close input device, without reverting back /dev/uinput's permission to 660.
 */
void closeInputWithoutRevertPermission();

//void sendNativeEvent(const char* dev, int type, int code, int value);

/**
 * A simple wrapper for suinput_write()
 */
int sendNativeEvent(int uinput_fd, uint16_t type, uint16_t code, int32_t value);

#endif

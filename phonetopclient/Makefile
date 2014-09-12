CC = g++
TARGET = phonetopclient
OBJS = main.cpp rtsp.cpp RtspClient.cpp stringutils.cpp input.cpp
CURRENT_DIR = $(shell pwd)
#turbojpeg library include and link
LIBS = -lpthread -std=c++11
UDEV_PATH = /etc/udev/rules.d/phonetop.rules
UDEV_RULES = ACTION==\"add\", SUBSYSTEM==\"usb\", ATTR{idVendor}==\"18d1\", ATTR{idProduct}==\"4ee4\", RUN+=\"$(CURRENT_DIR)/runPhonetopClient.sh\"

all : $(TARGET)

$(TARGET) : $(OBJS)
	$(CC) $(INCLUDES) -o  $@ $(OBJS) $(LIBS)
 
clean :
	rm -rf $(TARGET)

new : 
	$(MAKE) clean 
	$(MAKE)
 
udev :
	@echo $(UDEV_RULES) > $(UDEV_PATH)
	udevadm control --reload-rules
 
rmudev :
	rm -rf $(UDEV_PATH)
	udevadm control --reload-rules 
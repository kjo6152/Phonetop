CC = g++
TARGET = main
OBJS = main.cpp rtsp.cpp socket.cpp stringutils.cpp
CURRENT_DIR = $(shell pwd)

#turbojpeg library include and link
LIBS = -lpthread -std=c++11

all : $(TARGET)

$(TARGET) : $(OBJS)
	$(CC) $(INCLUDES) -o  $@ $(OBJS) $(LIBS)

clean :
	rm -rf $(TARGET)

new : 
	$(MAKE) clean 
	$(MAKE)
	
#hot plug setting
#	echo $(CURRENT_DIR)/hotplug > /proc/sys/kernel/hotplug

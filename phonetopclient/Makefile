CC = gcc
TARGET = ic
OBJS = InputClient.c
CURRENT_DIR = $(shell pwd)

#turbojpeg library include and link
LIBS = -lpthread

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

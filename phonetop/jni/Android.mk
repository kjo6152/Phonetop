LOCAL_PATH := $(call my-dir)
# APP_ABI := armeabi x86
# include $(call all-subdir-makefiles)
include $(CLEAR_VARS)

LOCAL_MODULE := phonetop
LOCAL_SRC_FILES := InputHandler.cpp \
				   Input.cpp \
				   suinput.cpp
				   
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

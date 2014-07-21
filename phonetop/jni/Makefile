JAVA_CLASSES_DIR = bin/classes
JAVA_NATIVES_DIR = $(JAVA_CLASSES_DIR)/org/secmem232/phonetop/android/natives
JNI_HEADER_DIR = jni/include
SOURCES = InputHandler.cpp

all: header lib

header : ../$(JAVA_NATIVES_DIR)/InputHandler.class
	cd ../$(JAVA_CLASSES_DIR); \
	javah org.secmem232.phonetop.android.natives.InputHandler; \
	mv org_secmem232_phonetop_android_natives_InputHandler.h InputHandler.h; \
	mv InputHandler.h ../../jni/include/InputHandler.h
	
lib : $(SOURCES)
	ndk-build APP_ABI="armeabi x86"
	
clean :
	rm -rf ../libs/armeabi/libphonetop.so; \
	rm -rf ../libs/x86/libphonetop.so; \
	rm -rf ../obj

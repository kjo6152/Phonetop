#ifndef LOGHELPER_H_
#define LOGHELPER_H_

#include <android/log.h>

#define LOGD(tag, message) __android_log_print(ANDROID_LOG_DEBUG, tag, message);
#define LOGW(tag, message) __android_log_print(ANDROID_LOG_WARN, tag, message);
#define LOGI(tag, message) __android_log_print(ANDROID_LOG_INFO, tag, message);

#endif

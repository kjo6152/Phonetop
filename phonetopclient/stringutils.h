#ifndef __STRING__UTILS__H__
#define __STRING__UTILS__H__

#include <string>

int readline(int fd, char *bufptr, size_t len);
int StringSplit(std::string strOrigin, std::string strTok, std::string *strResult);
#endif

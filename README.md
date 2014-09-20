Phonetop
========
Android for desktop project Using miracast and input sharing.
Android use miracast source, uinput, resoultion change.
Ubuntu applcation use rtp ffmpeg player, input drvier.

License
========
GPL license v3.0

How To Build
========
1. Environment : Android framework 4.4.2 (Nexus5), Ubuntu 14.04 LTS, FFMpeg 2.3
2. Build
 1) Android framework ( 4.4.2 )
   a. Download android framework 4.4.2
   b. Add binary in binaries directory and show 'https://github.com/kjo6152/Phonetop/tree/master/binaries/usage'
   c. Change source in 'frameworks/frameworks' to '<AOSP>/frameworks'
   d. Build android framework

 2) Android Application ( 4.4.2 , Need rooting to input sharing )
   a. Import project to eclipse
   b. Change ADB location in 'phonetop/systemapp/createSystemApp.sh'
   c. Check environment path about java
   d. Clean and build project then application install
   
 3) Ubuntu Application ( Ubuntu 14.04, ffmpeg 2.3 )
   a. ./configure and make in ffmpeg 2.3 directory
   b. move 'phonetop' file to phonetopclient directory
   c. make in phonetopclient directory
   d. ./runPhonetopClient.sh
 
3. Testing
 - Android application & framework : Nexus5
 - Linux application : Desktop, Odroid XU 
 - 
4. Usage

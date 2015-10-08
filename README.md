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
### Environment
 - Android framework 4.4.2 (Nexus5), Ubuntu 14.04 LTS, FFMpeg 2.3

### Build
 1. Android framework ( 4.4.2 )
   - Download android framework 4.4.2
   - Add binary in binaries directory and show 'https://github.com/kjo6152/Phonetop/tree/master/binaries/usage'
   - Change source in 'frameworks/frameworks' to '<AOSP>/frameworks'
   - Build android framework

 2. Android Application ( 4.4.2 , Need rooting to input sharing )
   - Import project to eclipse
   - Change ADB location in 'phonetop/systemapp/createSystemApp.sh'
   - Check environment path about java
   - Clean and build project then application install
   
 3. Linux Application ( Ubuntu 14.04, ffmpeg 2.3 )
   - './configure' and 'make' in 'phonetopclient/ffmpeg 2.3' directory
   - 'make new in' 'phonetopclient' directory
 
### Testing
 - Android application & framework : Nexus5
 - Linux application : Desktop, Odroid XU 

### Usage
 - Run android application and switch toogle.
 - If show message '실행중' then run linux application thought './runPhonetopClient.sh' in phonetopclient directory.
 - You can see monitor that show android display.

##Demonstration
<iframe width="560" height="315" src="https://www.youtube.com/embed/HkpBwy3axSw" frameborder="0" allowfullscreen></iframe>

##BLINK Members
- Jiwon Kim (김지원)
- Jinyoung Lee (이진영)
- Youngsik Lee (함영식)

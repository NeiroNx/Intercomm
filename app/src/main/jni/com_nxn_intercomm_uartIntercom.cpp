/*
 * Copyright (c) 2015, NeiroN.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *        * Redistributions of source code must retain the above copyright
 *            notice, this list of conditions and the following disclaimer.
 *        * Redistributions in binary form must reproduce the above copyright
 *            notice, this list of conditions and the following disclaimer in the
 *            documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT ARE DISCLAIMED.    IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include     <jni.h>
#include     <stdio.h>
#include     <stdlib.h>
#include 	 <string.h>
#include     <unistd.h>
#include     <sys/ioctl.h>
#include     <sys/types.h>
#include     <sys/stat.h>
#include     <fcntl.h>
#include     <termios.h>
#include     <errno.h>
#include	 <pthread.h>
#include     <android/log.h>

#define LOG_NDEBUG ANDROID_LOG_DEBUG
#define LOG_TAG "Serial_JNI"
#define ALOGE(...) __android_log_print(LOG_NDEBUG, LOG_TAG,__VA_ARGS__)

static pthread_rwlock_t uart_rwlock;
volatile int fd_intercom_dev = 0;
static int fd_uart_dev = 0;

static int config(int fd,int nSpeed,int nBits,char nEvent,int nStop)
{
	struct termios newtio;

	bzero( &newtio, sizeof( newtio ) );
	newtio.c_cflag |= CLOCAL | CREAD;
	newtio.c_cflag &= ~CSIZE;
	//set speed
	switch( nSpeed )
	{
		case 9600:
			cfsetispeed(&newtio,B9600);
			cfsetospeed(&newtio,B9600);
			break;
		default :
			break;
	}

	switch ( nBits )
	{
		case 7:
			newtio.c_cflag |= CS7;
			break;

		case 8:
			newtio.c_cflag |= CS8;
			break;
	}
	//set verify
	switch ( nEvent )
	{
		case 'o':
		case 'O': 	//odd
			newtio.c_cflag |= PARENB;
			newtio.c_cflag |= PARODD;
			newtio.c_iflag |= ( INPCK | ISTRIP );
			break;

		case 'e':
		case 'E':	//even
			newtio.c_iflag |= ( INPCK |ISTRIP );
			newtio.c_cflag |= PARENB;
			newtio.c_cflag &= ~PARODD;
			break;

		case 'n':
		case 'N':	//no
			newtio.c_cflag &= ~PARENB;
			newtio.c_iflag &= ~INPCK;
			break;
	}
	//set stop bit
	if( nStop ==1 ){
		newtio.c_cflag &= ~CSTOPB;
	}
	else if( nStop ==2 ){
		newtio.c_cflag |= CSTOPB;
	}


	if( ( tcsetattr(fd,TCSANOW,&newtio) )!=0 )
	{
		return -1;
	}

	else
		tcflush (fd, TCIOFLUSH);

	return 0;
}

static jint com_nxn_intercomm_uartIntercom_OpenSerial
(JNIEnv *env, jobject object,jstring dev,jint Baud)
{
	//if uart_dev is closed ,open it
	const char* Device = NULL;
    Device = env->GetStringUTFChars(dev, NULL);
	if(fd_uart_dev<=0){
		fd_uart_dev = open(Device, O_RDWR | O_NOCTTY);
		//if open uart_dev sucessful,config it
		if (fd_uart_dev>0){
			config(fd_uart_dev,Baud,8,'n',1);
			ALOGE("Open fd_uart_dev = %d !\n",fd_uart_dev);
		}
		else{
			ALOGE("Open %s fail!\n", Device);
			return -1;
		}
		ALOGE("Open %s sucessful!\n", Device);
	}
  	tcflush (fd_uart_dev, TCIOFLUSH);	//clear the buffer
  	//init rwlock.
    if(pthread_rwlock_init(&uart_rwlock,NULL) != 0)
    {
    	ALOGE("uart_rwlock initialization failed.");
    }
	return fd_uart_dev;
}

static jint com_nxn_intercomm_uartIntercom_CloseSerial
(JNIEnv *env, jobject object)
{
	//if uart_dev is open ,close it
	if(fd_uart_dev){
		close(fd_uart_dev);
	}
	fd_uart_dev = 0;
	pthread_rwlock_destroy(&uart_rwlock);//destroy rwlock.
	return 0;
}

static jstring com_nxn_intercomm_uartIntercom_SerialRead
  (JNIEnv *env, jobject object)
{
	int nread = 0;
	const int bufferLength = 4096;
    char readBuffer[bufferLength] = {'\0'};
    memset(readBuffer,'\0',bufferLength);
    pthread_rwlock_rdlock(&uart_rwlock);
    nread = read(fd_uart_dev,readBuffer,bufferLength);
    pthread_rwlock_unlock(&uart_rwlock);
    jstring mRead =  env->NewStringUTF((const char*) readBuffer);
    //free(readBuffer);
    return mRead;
}

static jint com_nxn_intercomm_uartIntercom_SerialWrite
  (JNIEnv *env, jobject object, jstring str)
{
	int nwrite = 0;
	unsigned int strLength = 0;
	const char* utfChars = NULL;
	utfChars = env->GetStringUTFChars(str, NULL);
    strLength = strlen(utfChars);
	pthread_rwlock_wrlock(&uart_rwlock);
    nwrite = write(fd_uart_dev,utfChars,strLength);
    pthread_rwlock_unlock(&uart_rwlock);
	return nwrite;
}

static jint com_nxn_intercomm_uartIntercom_SerialFlush
  (JNIEnv *evn, jclass object)
{
	tcflush (fd_uart_dev, TCIOFLUSH);
	return 0;
}

/* native interface */

static jint com_nxn_intercomm_uartIntercom_Ioctl
  (JNIEnv *env, jobject object,jstring dev, jlong num, jlong parameter )
{
	int fd_tmp=0,temp = 0;
	const char* Device = NULL;
	Device = env->GetStringUTFChars(dev, NULL);
	fd_tmp = open(Device, 0);
	if(fd_tmp<0){
		ALOGE("Open %s fail!\n",Device);
		exit(1);
	}else{
		ALOGE("fd_tmp = %d !\n",fd_tmp);
		ALOGE("Open %s sucessful!\n",Device);
	}
	//open_intercom_dev();
	temp = ioctl(fd_tmp,(unsigned int)num,(unsigned int)parameter);
	ALOGE("call ioctl return temp = %d !\n",temp);
	close(fd_tmp);
	return temp;
}


/*
 * JNI registration.
 */
static JNINativeMethod gMethods[] = {
        { "JNI_OpenSerial", "(Ljava/lang/String;I)I",
            (void*)com_nxn_intercomm_uartIntercom_OpenSerial},
        { "JNI_CloseSerial", "()I",
            (void*)com_nxn_intercomm_uartIntercom_CloseSerial},
        { "JNI_SerialRead", "()Ljava/lang/String;",
            (void*)com_nxn_intercomm_uartIntercom_SerialRead},
        { "JNI_SerialWrite", "(Ljava/lang/String;)I",
            (void*)com_nxn_intercomm_uartIntercom_SerialWrite},
        { "JNI_SerialFlush", "()I",
            (void*)com_nxn_intercomm_uartIntercom_SerialFlush},
        { "JNI_Ioctl", "(Ljava/lang/String;JJ)I",
            (void*)com_nxn_intercomm_uartIntercom_Ioctl},
};

int register_com_nxn_intercomm_uartIntercom(JNIEnv *env)
{

        jclass clazz;
        const char* className = "com/nxn/intercomm/uartIntercom";
        clazz = env->FindClass(className);
        if (clazz == NULL) {
            ALOGE("Native registration unable to find class '%s'\n", className);
            return -1;
        }
        if (env->RegisterNatives(clazz, gMethods, 6) < 0) {
            ALOGE("RegisterNatives failed for '%s'\n", className);
            return -1;
        }
        return 0;
}


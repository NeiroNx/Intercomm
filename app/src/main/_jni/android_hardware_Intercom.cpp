/*
 * Copyright (c) 2009-2010, Code Aurora Forum. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *        * Redistributions of source code must retain the above copyright
 *            notice, this list of conditions and the following disclaimer.
 *        * Redistributions in binary form must reproduce the above copyright
 *            notice, this list of conditions and the following disclaimer in the
 *            documentation and/or other materials provided with the distribution.
 *        * Neither the name of Code Aurora nor
 *            the names of its contributors may be used to endorse or promote
 *            products derived from this software without specific prior written
 *            permission.
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

#define LOG_NDEBUG 0
#define LOG_TAG "Intercom_JNI"

#include "jni.h"
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"
#include "utils/Log.h"
#include "utils/misc.h"

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
#include	<pthread.h>

#define GPIO_PULL_UP	1
#define GPIO_PULL_DOWN	0
#define INTERCOM_PULL_DOWN	0
#define INTERCOM_PULL_UP	1
//#define INTERCOM_SPEAKER_MODE	2
#define INTERCOM_HEADSET_MODE	3
#define INTERCOM_SPEAKER_MODE	4
#define INTERCOM_GET_VERSION 	5

//#define INTERCOM_AUCTUS_400M	0
//#define INTERCOM_80BK_400M		1
#define INTERCOM_81BK_480M		2
#define INTERCOM_D150_136M		3

using namespace android ;

static pthread_rwlock_t uart_rwlock;
static bool intercomMessageReady = true;
static bool intercomMessageSending = false;
static bool mMessageValid = false;
volatile int fd_intercom_dev = 0;
static int fd_uart_dev = 0;
static bool IntercomPowerState = false;

static unsigned char cmd_volume_array_81BK[19] = {'A','T','+','D','M','O','S','E','T','V',
						'O','L','U','M','E','=','9','\r','\n'};
char cmd_dmo_version[12] = {'A','T','+','D','M','O','V','E','R','Q','\r','\n'};
static int IntercomVersion = INTERCOM_81BK_480M;

//Begin laiyq,20130314;Add for support 81BK
char dmo_version_81bk[17] = {'+','D','M','O','V','E','R','Q',':','8','1','B','K','V','1','.','0'};
static unsigned char cmd_frequency_array_81BK[46] = {'A','T','+','D','M','O','S','E','T','G',
							'R','O','U','P','=','0',',','4','0','9',
							'.','7','5','0','0',',','4','0','9','.',
							'7','5','0','0',',','0','1',',','1',',',
							'0','1',',','0','\r','\n'};
//End laiyq,20130314;Add for support 81BK
char dmo_version_d150[17] = {'+','D','M','O','V','E','R','Q',':','D','1','5','0','V','1','.','0'};
static unsigned char cmd_frequency_array_D150[46] = {'A','T','+','D','M','O','S','E','T','G',
							'R','O','U','P','=','0',',','1','4','0',
							'.','0','0','0','0',',','1','4','0','.',
							'0','0','0','0',',','0','1',',','1',',',
							'0','1',',','0','\r','\n'};
static unsigned char cmd_volume_array_D150[19] = {'A','T','+','D','M','O','S','E','T','V',
						'O','L','U','M','E','=','8','\r','\n'};
//Add AT command to set Intercom MIC/SCRAMLVL/TOT
static unsigned char cmd_mic_array_81BK[20] = {'A','T','+','D','M','O','S','E','T','M',
						'I','C','=','5',',','0',',','0','\r','\n'};

static int config_serial(int fd,int nSpeed,int nBits,char nEvent,int nStop)
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

static int open_uart_dev()
{
	//if uart_dev is closed ,open it
	if(fd_uart_dev<=0){
		fd_uart_dev = open("/dev/ttyMT3", O_RDWR | O_NOCTTY);
		//if open uart_dev sucessful,config it
		if (fd_uart_dev>0){
			config_serial(fd_uart_dev,9600,8,'n',1);
			ALOGE("Open fd_uart_dev = %d !\n",fd_uart_dev);
		}
		else{
			ALOGE("Open /dev/ttyMT3 fail!\n");
			exit(1);
		}
		ALOGE("Open /dev/ttyMT3 sucessful!\n");
	}
  	tcflush (fd_uart_dev, TCIOFLUSH);	//clear the buffer
	return 0;
}

static void close_uart_dev()
{
	//if uart_dev is open ,close it
	if(fd_uart_dev){
		close(fd_uart_dev);
		ALOGE("close /dev/ttyMT3 sucessful!\n");
	}
	fd_uart_dev = 0;
}


static int set_volume_81BK_480M()
{
	int nwrite = 0;
  	//tcflush (fd_uart_dev, TCIOFLUSH);	//clear the buffer
	if(fd_uart_dev != 0)
	{
		pthread_rwlock_wrlock(&uart_rwlock);
  		nwrite = write(fd_uart_dev,cmd_volume_array_81BK,19);
		pthread_rwlock_unlock(&uart_rwlock);
	}
	return 0;
}

static int set_volume_D150_136M()
{
	int nwrite = 0;
  	//tcflush (fd_uart_dev, TCIOFLUSH);	//clear the buffer
	if(fd_uart_dev != 0)
	{
		pthread_rwlock_wrlock(&uart_rwlock);
  		nwrite = write(fd_uart_dev,cmd_volume_array_D150,19);
		pthread_rwlock_unlock(&uart_rwlock);
	}
	return 0;
}
static int set_volume()
{
	if(fd_uart_dev != 0)
	{
		if(IntercomVersion == INTERCOM_81BK_480M)
		{
			set_volume_81BK_480M();
		}
		//D150
		else if (IntercomVersion == INTERCOM_D150_136M)
		{
			set_volume_D150_136M();
		}
	}
	return 0;
}

static int set_frequency_81BK_480M()
{
	int nwrite = 0;
	if(fd_uart_dev != 0)
	{
		pthread_rwlock_wrlock(&uart_rwlock);
		nwrite = write(fd_uart_dev,cmd_frequency_array_81BK,46);
		pthread_rwlock_unlock(&uart_rwlock);
		ALOGE("set_frequency_81BK_480M()!\n");
		//Add AT command to set Intercom MIC/SCRAMLVL/TOT
		usleep(100000);
		pthread_rwlock_wrlock(&uart_rwlock);
  		nwrite = write(fd_uart_dev,cmd_mic_array_81BK,20);
		pthread_rwlock_unlock(&uart_rwlock);
	}
	return 0;
}
//D150
static int set_frequency_D150_136M()
{
	int nwrite = 0;
	if(fd_uart_dev != 0)
	{
		pthread_rwlock_wrlock(&uart_rwlock);
		nwrite = write(fd_uart_dev,cmd_frequency_array_D150,46);
		pthread_rwlock_unlock(&uart_rwlock);
		ALOGE("set_frequency_D150_136M()!\n");
	}
	return 0;
}

static int set_frequency()
{
	if(fd_uart_dev != 0)
	{
		if (IntercomVersion == INTERCOM_81BK_480M)
		{
			set_frequency_81BK_480M();
		}
		//D150
		else if (IntercomVersion == INTERCOM_D150_136M)
		{
			set_frequency_D150_136M();
		}
	}
	return 0;
}

/********************************************************************
 * Current JNI
 *******************************************************************/

/* native interface */

static jint android_hardware_intercomPowerOn
  (JNIEnv *env, jobject object)
{
	int fd_tmp=0,temp = 0;
	ALOGE("Call jni_intercomPowerOn!\n");
	IntercomPowerState = true;
		fd_tmp = open("/dev/intercom_A1840", 0);
		if(fd_tmp<0){
			ALOGE("Open /dev/intercom_A1840 fail!\n");
			exit(1);
		}
		else{
			ALOGE("fd_tmp = %d !\n",fd_tmp);
			ALOGE("Open /dev/intercom_A1840 sucessful!\n");
		}
	//open_intercom_dev();
	temp = ioctl(fd_tmp,INTERCOM_PULL_UP,0);
	ALOGE("call ioctl return temp = %d !\n",temp);
	close(fd_tmp);
	open_uart_dev();	//open uart for Intercom to set parameter or data transfer.

	//init rwlock.
	if(pthread_rwlock_init(&uart_rwlock,NULL) != 0)
	{
		ALOGE("uart_rwlock initialization failed.");
	}
	return 0;
}

static jint android_hardware_intercomPowerOff
  (JNIEnv *env, jobject object)
{
	int fd_tmp=0,temp=0;
	ALOGE("Call jni_intercomPowerOff!\n");
	IntercomPowerState = false;
	//open_intercom_dev();
		fd_tmp = open("/dev/intercom_A1840", 0);
		if(fd_tmp<0){
			ALOGE("Open /dev/intercom_A1840 fail!\n");
			exit(1);
		}
		else{
			ALOGE("fd_tmp = %d !\n",fd_tmp);
			ALOGE("Open /dev/intercom_A1840 sucessful!\n");
		}
	temp=ioctl(fd_tmp,INTERCOM_PULL_DOWN,0);
	ALOGE("call ioctl return temp = %d !\n",temp);
	close(fd_tmp);
	close_uart_dev();//close uart when Intercom PowerDown.
	pthread_rwlock_destroy(&uart_rwlock);//destroy rwlock.
	return 0;
}

static jint android_hardware_intercomSpeakerMode
  (JNIEnv *env, jobject object)
{
	int fd_tmp=0,temp=0;
	ALOGE("Call jni_intercomSpeakerMode!-0911\n");
	//open_intercom_dev();
		fd_tmp = open("/dev/intercom_A1840", 0);
		if(fd_tmp<0){
			ALOGE("Open /dev/intercom_A1840 fail!\n");
			exit(1);
		}
		else{
			ALOGE("fd_tmp = %d !\n",fd_tmp);
			ALOGE("Open /dev/intercom_A1840 sucessful!\n");
		}
	usleep(10);
	temp=ioctl(fd_tmp,INTERCOM_SPEAKER_MODE,0);//speaker mode:config on kernel driver
	ALOGE("call ioctl return temp = %d !\n",temp);
	close(fd_tmp);
	//close_intercom_dev();
	return 0;
}

static jint android_hardware_intercomHeadsetMode
  (JNIEnv *env, jobject object)
{
	int fd_tmp=0,temp=0;
	ALOGE("Call jni_intercomHeadsetMode!-0911\n");
	//open_intercom_dev();
		fd_tmp = open("/dev/intercom_A1840", 0);
		if(fd_tmp<0){
			ALOGE("Open /dev/intercom_A1840 fail!\n");
			exit(1);
		}
		else{
			ALOGE("fd_tmp = %d !\n",fd_tmp);
			ALOGE("Open /dev/intercom_A1840 sucessful!\n");
		}
	usleep(10);
	temp=ioctl(fd_tmp,INTERCOM_HEADSET_MODE,0);//headset mode:config on kernel driver
	ALOGE("call ioctl return temp = %d !\n",temp);
	close(fd_tmp);
	//close_intercom_dev();
	return 0;
}

static jint android_hardware_setRadioFrequency
  (JNIEnv *env, jobject object, jint RF)
{

	ALOGE("Call jni_setRadioFrequency! RF = %d \n",RF);
	// Null
	return 0;
}


static jint android_hardware_setVolume
  (JNIEnv *env, jobject object, jint volume)
{
	volume = volume + 1;
	ALOGE("Call jni_setVolume! volume = %d \n",volume);
	//HTK,81BK
	if (IntercomVersion == INTERCOM_81BK_480M)
	{
		switch (volume)
		{
			case 1:
				cmd_volume_array_81BK[16] = '1';
				break;
			case 2:
				cmd_volume_array_81BK[16] = '2';
				break;
			case 3:
				cmd_volume_array_81BK[16] ='3';
				break;
			case 4:
				cmd_volume_array_81BK[16] = '4';
				break;
			case 5:
				cmd_volume_array_81BK[16] = '5';
				break;
			case 6:
				cmd_volume_array_81BK[16] = '6';
				break;
			case 7:
				cmd_volume_array_81BK[16] = '7';
				break;
			case 8:
				cmd_volume_array_81BK[16] = '8';
				break;
			default:
				cmd_volume_array_81BK[16] = '8';
				break;
		}
	}
	//D150
	else if (IntercomVersion == INTERCOM_D150_136M)
	{
		switch (volume)
		{
			case 1:
				cmd_volume_array_D150[16] = '1';
				break;

			case 2:
				cmd_volume_array_D150[16] = '2';
				break;

			case 3:
				cmd_volume_array_D150[16] ='3';
				break;

			case 4:
				cmd_volume_array_D150[16] = '4';
				break;

			case 5:
				cmd_volume_array_D150[16] = '5';
				break;

			case 6:
				cmd_volume_array_D150[16] = '6';
				break;
			case 7:
				cmd_volume_array_D150[16] = '7';
				break;
			case 8:
				cmd_volume_array_D150[16] = '8';
				break;
			default:
				cmd_volume_array_D150[16] = '8';
				break;
		}
	}
	set_volume();
	return 0;
}

static jint android_hardware_setSq
  (JNIEnv *env, jobject object, jint sq_value)
{
	ALOGE("Call jni_setSq! sq_value = %d \n",sq_value);
	//HKT 81BK
	if (IntercomVersion == INTERCOM_81BK_480M)
	{
		cmd_frequency_array_81BK[38] = (unsigned char)sq_value+48;
	}
	//HTK,D150
	else if (IntercomVersion == INTERCOM_D150_136M)
	{
		cmd_frequency_array_D150[38] = (unsigned char)sq_value+48;
	}
	set_frequency();
	return 0;
}


static jint android_hardware_setCtcss
  (JNIEnv *evn, jobject object, jint ctcss_value)
{
	int decade=0,uint=0;
	ALOGE("Call jni_setCtcss! ctcss_value = %d \n",ctcss_value);
	decade = ctcss_value/10;
	uint = ctcss_value%10;
	//HTK,81BK
	if (IntercomVersion == INTERCOM_81BK_480M)
	{
		cmd_frequency_array_81BK[35] = (unsigned char)decade+48;
		cmd_frequency_array_81BK[36] = (unsigned char)uint+48;
	}
	//HTK,D150
	else if (IntercomVersion == INTERCOM_D150_136M)
	{
		cmd_frequency_array_D150[35] = (unsigned char)decade+48;
		cmd_frequency_array_D150[36] = (unsigned char)uint+48;
	}
	set_frequency();
	return 0;
}

static jint android_hardware_setTxCtcss
  (JNIEnv *evn, jobject object, jint tx_ctcss_value)
{
	int decade=0,uint=0;
	ALOGE("Call jni_setTxCtcss! tx_ctcss_value = %d \n",tx_ctcss_value);
	decade = tx_ctcss_value/10;
	uint = tx_ctcss_value%10;
	//HTK,81BK
	if (IntercomVersion == INTERCOM_81BK_480M)
	{
		ALOGE("setTxCtcss_81BK_480M.\n");
		cmd_frequency_array_81BK[40] = (unsigned char)decade+48;
		cmd_frequency_array_81BK[41] = (unsigned char)uint+48;
		set_frequency();
	}
	//HTK,D150
	if (IntercomVersion == INTERCOM_D150_136M)
	{
		ALOGE("setTxCtcss_D150_136M.\n");
		cmd_frequency_array_D150[40] = (unsigned char)decade+48;
		cmd_frequency_array_D150[41] = (unsigned char)uint+48;
		set_frequency();
	}
	//Auctus,A1840/A1852:not TxCtcss
	return 0;
}

static jint android_hardware_setTXFrequency
  (JNIEnv *evn, jclass object, jint tx_rf)
{
	int rf1=0,rf2=0,rf3=0,rf4=0,rf5=0,rf6=0,rf7=0;
	ALOGE("Call jni_setTXFrequency! tx_rf = %d \n",tx_rf);
	rf1 = tx_rf/1000000;
	rf2 = (tx_rf%1000000)/100000;
	rf3 = (tx_rf%100000)/10000;
	rf4 = (tx_rf%10000)/1000;
	rf5 = (tx_rf%1000)/100;
	rf6 = (tx_rf%100)/10;
	rf7 = (tx_rf%10)/1;
	//HTK,81BK
	if (IntercomVersion == INTERCOM_81BK_480M)
	{
		cmd_frequency_array_81BK[17] = (unsigned char)rf1+48;
		cmd_frequency_array_81BK[18] = (unsigned char)rf2+48;
		cmd_frequency_array_81BK[19] = (unsigned char)rf3+48;
		cmd_frequency_array_81BK[21] = (unsigned char)rf4+48;
		cmd_frequency_array_81BK[22] = (unsigned char)rf5+48;
		cmd_frequency_array_81BK[23] = (unsigned char)rf6+48;
		cmd_frequency_array_81BK[24] = (unsigned char)rf7+48;
	}
	//HTK,D150
	else if (IntercomVersion == INTERCOM_D150_136M)
	{
		cmd_frequency_array_D150[17] = (unsigned char)rf1+48;
		cmd_frequency_array_D150[18] = (unsigned char)rf2+48;
		cmd_frequency_array_D150[19] = (unsigned char)rf3+48;
		cmd_frequency_array_D150[21] = (unsigned char)rf4+48;
		cmd_frequency_array_D150[22] = (unsigned char)rf5+48;
		cmd_frequency_array_D150[23] = (unsigned char)rf6+48;
		cmd_frequency_array_D150[24] = (unsigned char)rf7+48;
	}
	//usleep(100000);
	set_frequency();
	return 0;
}

static jint android_hardware_setRXFrequency
  (JNIEnv *evn, jclass object, jint RX_rf)
{
  int rf1=0,rf2=0,rf3=0,rf4=0,rf5=0,rf6=0,rf7=0;
	ALOGE("Call jni_setRXFrequency! rx_rf = %d \n",RX_rf);
	rf1 = RX_rf/1000000;
	rf2 = (RX_rf%1000000)/100000;
  	rf3 = (RX_rf%100000)/10000;
	rf4 = (RX_rf%10000)/1000;
	rf5 = (RX_rf%1000)/100;
	rf6 = (RX_rf%100)/10;
	rf7 = (RX_rf%10)/1;
	//HTK,81BK
	if (IntercomVersion == INTERCOM_81BK_480M)
	{
		cmd_frequency_array_81BK[26] = (unsigned char)rf1+48;
		cmd_frequency_array_81BK[27] = (unsigned char)rf2+48;
		cmd_frequency_array_81BK[28] = (unsigned char)rf3+48;
		cmd_frequency_array_81BK[30] = (unsigned char)rf4+48;
		cmd_frequency_array_81BK[31] = (unsigned char)rf5+48;
		cmd_frequency_array_81BK[32] = (unsigned char)rf6+48;
		cmd_frequency_array_81BK[33] = (unsigned char)rf7+48;
	}
	//HTK,D150
	else if (IntercomVersion == INTERCOM_D150_136M)
	{
		cmd_frequency_array_D150[26] = (unsigned char)rf1+48;
		cmd_frequency_array_D150[27] = (unsigned char)rf2+48;
		cmd_frequency_array_D150[28] = (unsigned char)rf3+48;
		cmd_frequency_array_D150[30] = (unsigned char)rf4+48;
		cmd_frequency_array_D150[31] = (unsigned char)rf5+48;
		cmd_frequency_array_D150[32] = (unsigned char)rf6+48;
		cmd_frequency_array_D150[33] = (unsigned char)rf7+48;
	}
	//usleep(100000);
	set_frequency();

	return 0;
}

static jint android_hardware_resumeIntercomSetting
  (JNIEnv *evn, jclass object)
{
	ALOGE("Call jni_resumeIntercomSetting! \n");
	sleep(1);
	set_frequency();
	usleep(100000);
	set_volume();
	return 0;
}

static jint android_hardware_getIntercomVersion
  (JNIEnv *evn, jclass object ,jint whenBootComplete)
{
	int nread = 0,nwrite = 0,i = 0;
	unsigned char buffer[30] = {0};
	int fd_intercom=0,temp = 0;
	ALOGE("Call jni_android_hardware_getIntercomVersion !\n");

	if (whenBootComplete)
	{
		//powerOn intercom
    	fd_intercom = open("/dev/intercom_A1840", 0);
    	if(fd_intercom<0){
        	ALOGE("Open /dev/intercom_A1840 fail!\n");
        	exit(1);
    	}
    	else{
        	ALOGE("fd_intercom = %d !\n",fd_intercom);
        	ALOGE("Open /dev/intercom_A1840 sucessful!\n");
    	}
    	ioctl(fd_intercom,INTERCOM_GET_VERSION,1);
		usleep(100000);
	}
	//sleep(2);
	for(i=0;i<100;i++){
		tcflush (fd_uart_dev, TCIOFLUSH);
	pthread_rwlock_wrlock(&uart_rwlock);
		nwrite = write(fd_uart_dev,cmd_dmo_version,12);
	pthread_rwlock_unlock(&uart_rwlock);
		ALOGE("write command to get Intercom Version, nwrite = %d !\n",nwrite);
		usleep(100000);
		pthread_rwlock_rdlock(&uart_rwlock);
		nread = read(fd_uart_dev,buffer,30);
		pthread_rwlock_unlock(&uart_rwlock);
		ALOGE("Read Intercom version, nread = %d !\n",nread);
		if(nread>10)
			break;
		usleep(100000);
	}
	//nread = read(fd_uart_dev,buffer,strlen(dmo_version_80bk));
	if((buffer[9]==dmo_version_81bk[9])&&(buffer[10]==dmo_version_81bk[10])&&(buffer[11]==dmo_version_81bk[11])&&(buffer[12]==dmo_version_81bk[12])) {
		IntercomVersion = INTERCOM_81BK_480M;
		ALOGE("Runbo: This is the INTERCOM_81BK_480M module\n");
	}
	else if((buffer[9]==dmo_version_d150[9])&&(buffer[10]==dmo_version_d150[10])&&(buffer[11]==dmo_version_d150[11])&&(buffer[12]==dmo_version_d150[12])) {
		IntercomVersion = INTERCOM_D150_136M;
		ALOGE("Runbo: This is the INTERCOM_D150_136M module\n");
	}
	ALOGE("End of jni_android_hardware_getIntercomVersion !\n");

	if(whenBootComplete)
	{
		//powerOff intercom
		ioctl(fd_intercom,INTERCOM_PULL_DOWN,0);
    	close(fd_intercom);
	}

	return IntercomVersion;
}

static jint android_hardware_sendMessage(JNIEnv *env, jobject thiz, jstring message)
{
	if(IntercomPowerState)
	{
	    const char* utfChars = NULL;
		unsigned int mLength = 0;
		unsigned int mCount = 0;
		unsigned int mModulo = 0;
		unsigned int i = 0, j = 0;
		int nwrite = 0;
		char messageBuffer[100]={0};
		char sendMessageHead[10] = {'A','T','+','D','M','O','M','E','S','='};
		char sendMessageEnd[2] = {'\r','\n'};

		const int maxMessageLength = 70;
		const int lengthMessageHead = 10;
		const int lengthMessageEnd = 2;
		char lengthMessageContent = 0;
		int lengthMessageWhole = 0;
		const char *utfMessage = NULL;

	    utfChars = env->GetStringUTFChars(message, NULL);
		mLength = strlen(utfChars);
		utfMessage = utfChars;

	    ALOGE("  utfChars is '%s', \n", (const char*) utfChars );
	    ALOGE("  String length is '%d', \n",mLength );
	    //LOGE("  utfMessage is '%s', \n", (const char*) utfMessage );

	//Start sendMessage
		mCount = mLength / maxMessageLength;
		mModulo = mLength % maxMessageLength;
		if(mCount>0)
		{
			//Clear messageBuffer.
			for(i=0;i<100;i++)
			{
				messageBuffer[i] = 0;
			}
			//Write sendMessageHead to messageBuffer.
			for(i=0;i<lengthMessageHead;i++)
			{
				messageBuffer[i] = sendMessageHead[i];
			}
			lengthMessageContent = 70;
			//mark lengthMessageContent to messageBuffer
			messageBuffer[lengthMessageHead] = lengthMessageContent;
			//Write messageContent to messageBuffer.
			for(i=0;i<mCount;i++)
			{
				//The message's length is longger than maxMessageLength,must be decomposed.
				for(j=0;j<maxMessageLength;j++)
				{
					messageBuffer[lengthMessageHead+1+j] = *(utfMessage++);
				}
				messageBuffer[lengthMessageHead+1+maxMessageLength] = sendMessageEnd[0];
				messageBuffer[lengthMessageHead+1+maxMessageLength+1] = sendMessageEnd[1];

				lengthMessageWhole = lengthMessageHead + 1 + maxMessageLength + lengthMessageEnd;
				//Write messageBuffer to Intercom Module.
				pthread_rwlock_wrlock(&uart_rwlock);
				nwrite = write(fd_uart_dev,messageBuffer,lengthMessageWhole);
				pthread_rwlock_unlock(&uart_rwlock);
				//After write a message to Intercom Module,it must sleep for 2 second
				ALOGE("Write messageBuffer = %s to Intercom Module;nwrite = %d .\n",messageBuffer,nwrite);
				sleep(3);
			}
		}

		if(mModulo>0)
		{
			//Clear messageBuffer.
			for(i=0;i<100;i++)
			{
				messageBuffer[i] = 0;
			}
			//Write sendMessageHead to messageBuffer.
			for(i=0;i<lengthMessageHead;i++)
			{
				messageBuffer[i] = sendMessageHead[i];
			}
			lengthMessageContent = mModulo;
			//mark lengthMessageContent to messageBuffer
			messageBuffer[lengthMessageHead] = lengthMessageContent;
			//Write messageContent to messageBuffer.
			for(i=0;i<mModulo;i++)
			{
				messageBuffer[lengthMessageHead + 1 + i] = *(utfMessage++);
			}
			messageBuffer[lengthMessageHead+1+mModulo] = sendMessageEnd[0];
			messageBuffer[lengthMessageHead+1+mModulo+1] = sendMessageEnd[1];

			lengthMessageWhole = lengthMessageHead + 1 + mModulo + lengthMessageEnd;
			//write messageBuffer to Intercom Module.
			pthread_rwlock_wrlock(&uart_rwlock);
			nwrite = write(fd_uart_dev,messageBuffer,lengthMessageWhole);
			pthread_rwlock_unlock(&uart_rwlock);
			ALOGE("Write messageBuffer = %s to Intercom Module;nwrite = %d .\n",messageBuffer,nwrite);
			//After write a message to Intercom Module,it must sleep for 2 second
			sleep(3);
		}
	//End sendMessage

	    env->ReleaseStringUTFChars(message, utfChars);
		utfMessage = 0;
		ALOGE("Exit android_hardware_senMessage(xxx) !\n");
	}
	return 0;
}
static jint android_hardware_checkMessageBuffer(JNIEnv *env, jobject thiz)
{
	return 1;
}
static jstring android_hardware_getMessage(JNIEnv *env, jobject thiz)
{
	if(IntercomPowerState)
	{
		int nread = 0,i = 0;
		const char readMessageHead[8] = {'+','D','M','O','M','E','S','='};
		const int bufferLength = 4096;
		char readMessageBuffer[bufferLength] = {'\0'};
		int wholeMessageLength = 0;
		int subMessageLength = 0;

		char *pBuffer = NULL;
		const char *pMessageHead = NULL;
		char *pWholeMessage = NULL;
		char *pSubMessage = NULL;
		char *tempReadMessage = NULL;
		int countMessageHead = 0;

		ALOGE("Enter android_hardware_getMessage(xxx) !\n");
		if(intercomMessageReady == true)
		{
			ALOGE("intercomMessageReady = true !\n");
			intercomMessageReady = false;
			//initialize readMessageBuffer
			memset(readMessageBuffer,'\0',bufferLength);
			pWholeMessage = (char *)malloc(bufferLength+1);
			memset(pWholeMessage,'\0',bufferLength);
			pthread_rwlock_rdlock(&uart_rwlock);
			nread = read(fd_uart_dev,readMessageBuffer,bufferLength);
			pthread_rwlock_unlock(&uart_rwlock);
			ALOGE("nread = %d,readMessageBuffer = %s .\n",nread,(const char*)readMessageBuffer);

			pBuffer = readMessageBuffer;
			pMessageHead = readMessageHead;
			while(*pBuffer)
			{
				//check the first charater of readMessageHead.
				while( (*pBuffer) && (*pBuffer != *pMessageHead) )
				{
					pBuffer++;
				}
				//check the whole readMessageHead.
				while( (*pBuffer) && (*pMessageHead) && (*pBuffer == *pMessageHead) )
				{
					pBuffer++;
					pMessageHead++;
					countMessageHead++;
				}
				if(countMessageHead == 8)
				{
					mMessageValid = true;
					subMessageLength = *pBuffer;
					pBuffer++;
					wholeMessageLength += subMessageLength;
					ALOGE("subMessageLength is %d ;wholeMessageLength = %d .\n",subMessageLength,wholeMessageLength);
					pSubMessage = (char*)malloc(subMessageLength+1);
					//pWholeMessage = (char*)malloc(wholeMessageLength+1);
					tempReadMessage = pSubMessage;
					for(i=0;i<subMessageLength;i++)
					{
						*tempReadMessage++ = *pBuffer++;
					}
					*tempReadMessage = '\0';

					pWholeMessage = strcat(pWholeMessage,pSubMessage);

					ALOGE("subMessageLength is %d .\n",subMessageLength);
					ALOGE("subMessage is %s .\n",(const char*) pSubMessage);
					//LOGE("wholeMessage is %s .\n",(const char*) pWholeMessage);
					free(pSubMessage);
				}
				//reset several variables
				countMessageHead = 0;
				pSubMessage = NULL;
				pMessageHead = readMessageHead;
				tempReadMessage = NULL;
			}
			jstring mStringMessage =  env->NewStringUTF((const char*) pWholeMessage);
			free(pWholeMessage);
			pWholeMessage = NULL;
			if(mMessageValid == true)
			{
				ALOGE("mMessageValid is true ,pWholeMessage is %s .\n",(const char*) pWholeMessage);
				mMessageValid = false;
				intercomMessageReady = true;
				return mStringMessage;
			}
		}

		mMessageValid = false;
		intercomMessageReady = true;
		//return env->NewStringUTF(" ");
	}
	return 0;
}

/*
 * JNI registration.
 */
static JNINativeMethod gMethods[] = {
        { "JNI_intercomPowerOn", "()I",
            (void*)android_hardware_intercomPowerOn},
        { "JNI_intercomPowerOff", "()I",
            (void*)android_hardware_intercomPowerOff},
        { "JNI_intercomSpeakerMode", "()I",
            (void*)android_hardware_intercomSpeakerMode},
        { "JNI_intercomHeadsetMode", "()I",
            (void*)android_hardware_intercomHeadsetMode},
        { "JNI_setRadioFrequency", "(I)I",
            (void*)android_hardware_setRadioFrequency},
        { "JNI_setVolume", "(I)I",
            (void*)android_hardware_setVolume},
        { "JNI_setSq", "(I)I",
            (void*)android_hardware_setSq},
        { "JNI_setCtcss", "(I)I",
            (void*)android_hardware_setCtcss},
        { "JNI_setTxCtcss", "(I)I",
            (void*)android_hardware_setTxCtcss},
        { "JNI_setTXFrequency", "(I)I",
            (void*)android_hardware_setTXFrequency},
        { "JNI_setRXFrequency", "(I)I",
            (void*)android_hardware_setRXFrequency},
        { "JNI_resumeIntercomSetting", "()I",
            (void*)android_hardware_resumeIntercomSetting},
		{ "JNI_getIntercomVersion", "(I)I",
            (void*)android_hardware_getIntercomVersion},
		{ "JNI_sendMessage",
          "(Ljava/lang/String;)I",
          (void*)android_hardware_sendMessage },
		{ "JNI_checkMessageBuffer",
          "()I",
          (void*)android_hardware_checkMessageBuffer },
		{ "JNI_getMessage",
          "()Ljava/lang/String;",
          (void*)android_hardware_getMessage },
};

int register_android_hardware_Intercom(JNIEnv *env)
{
        return jniRegisterNativeMethods(env, "android/hardware/Intercom", gMethods, NELEM(gMethods));
}


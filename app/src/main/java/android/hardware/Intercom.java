package android.hardware;

public class Intercom
{
    private static native int JNI_checkMessageBuffer();

    private static native int JNI_closeCharDev();

    private static native int JNI_getIntercomVersion();

    private static native String JNI_getMessage();

    private static native int JNI_intercomHeadsetMode();

    private static native int JNI_intercomPowerOff();

    private static native int JNI_intercomPowerOn();

    private static native int JNI_intercomSpeakerMode();

    private static native int JNI_openCharDev();

    private static native int JNI_resumeIntercomSetting();

    private static native int JNI_sendMessage(String paramString);

    private static native int JNI_setCtcss(int paramInt);

    private static native int JNI_setRXFrequency(int paramInt);

    private static native int JNI_setRadioFrequency(int paramInt);

    private static native int JNI_setSq(int paramInt);

    private static native int JNI_setTXFrequency(int paramInt);

    private static native int JNI_setTxCtcss(int paramInt);

    private static native int JNI_setVolume(int paramInt);

    public int checkMessageBuffer()
    {
        return JNI_checkMessageBuffer();
    }

    public void closeCharDev()
    {
        JNI_closeCharDev();
    }

    public int getIntercomVersion()
    {
        return JNI_getIntercomVersion();
    }

    public String getMessage()
    {
        return JNI_getMessage();
    }

    public void intercomHeadsetMode()
    {
        JNI_intercomHeadsetMode();
    }

    public void intercomPowerOff()
    {
        JNI_intercomPowerOff();
    }

    public void intercomPowerOn()
    {
        JNI_intercomPowerOn();
    }

    public void intercomSpeakerMode()
    {
        JNI_intercomSpeakerMode();
    }

    public void openCharDev()
    {
        JNI_openCharDev();
    }

    public void resumeIntercomSetting()
    {
        JNI_resumeIntercomSetting();
    }

    public int sendMessage(String paramString)
    {
        return JNI_sendMessage(paramString);
    }

    public void setCtcss(int paramInt)
    {
        JNI_setCtcss(paramInt);
    }

    public void setRXFrequency(int paramInt)
    {
        JNI_setRXFrequency(paramInt);
    }

    public void setRadioFrequency(int paramInt)
    {
        JNI_setRadioFrequency(paramInt);
    }

    public void setSq(int paramInt)
    {
        JNI_setSq(paramInt);
    }

    public void setTXFrequency(int paramInt)
    {
        JNI_setTXFrequency(paramInt);
    }

    public void setTxCtcss(int paramInt)
    {
        JNI_setTxCtcss(paramInt);
    }

    public void setVolume(int paramInt)
    {
        JNI_setVolume(paramInt);
    }
}
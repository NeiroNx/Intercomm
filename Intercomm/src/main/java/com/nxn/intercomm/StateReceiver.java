package com.nxn.intercomm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Intercom;
import android.telephony.TelephonyManager;


public class StateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mSettings = context.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        Intercom mIintercom = new Intercom();
        Boolean Power = Boolean.parseBoolean(mSettings.getString(MainActivity.APP_PREFERENCES_POWER,"false"));
        if(!Power)return;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            mIintercom.intercomPowerOff();
        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                mIintercom.intercomPowerOff();
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                mIintercom.intercomPowerOff();
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                mIintercom.intercomPowerOn();
                mIintercom.resumeIntercomSetting();
            }
        }else if(intent.getAction().equals("com.android.deskclock.ALARM_ALERT")){
            mIintercom.intercomPowerOff();
        }else if(intent.getAction().equals("com.android.deskclock.ALARM_DONE")){
            mIintercom.intercomPowerOn();
            mIintercom.resumeIntercomSetting();
        }
    }
}

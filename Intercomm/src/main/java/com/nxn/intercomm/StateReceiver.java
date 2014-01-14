package com.nxn.intercomm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Intercom;
import android.telephony.TelephonyManager;

public class StateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        MainActivity activity = new MainActivity();
        Intercom mIintercom = activity.mIntercom;
        Boolean Power = activity.Power;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            //получаем исходящий номер
            //phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            if(Power)mIintercom.intercomPowerOff();
        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //телефон звонит, получаем входящий номер
                //phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(Power)mIintercom.intercomPowerOff();
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //телефон находится в режиме звонка (набор номера / разговор)
                if(Power)mIintercom.intercomPowerOff();
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //телефон находиться в ждущем режиме. Это событие наступает по окончанию разговора, когда мы уже знаем номер и факт звонка
                if(Power){
                    mIintercom.intercomPowerOn();
                    mIintercom.resumeIntercomSetting();
                }
            }
        }else if(intent.getAction().equals("com.android.deskclock.ALARM_ALERT")){
            if(Power)mIintercom.intercomPowerOff();
        }else if(intent.getAction().equals("com.android.deskclock.ALARM_DONE")){
            if(Power){
                mIintercom.intercomPowerOn();
                mIintercom.resumeIntercomSetting();
            }
        }
    }
}

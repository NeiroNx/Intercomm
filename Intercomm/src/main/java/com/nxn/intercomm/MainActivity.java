package com.nxn.intercomm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Intercom;

import org.xml.sax.XMLReader;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,OnClickListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SampleAdapter mPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    ActionBar mActionBar;
    IntentFilter mState;
    BroadcastReceiver mStateReceiver;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    /**
     * Settings declaration
     */
    public SharedPreferences mSettings;
    public SharedPreferences.Editor editor;
    public static final String APP_PREFERENCES = "IntercomSettings";
    public static final String APP_PREFERENCES_TAB = "tab";
    public static final String APP_PREFERENCES_NICK = "nick";
    public static final String APP_PREFERENCES_TX_FREQ = "tx_freq";
    public static final String APP_PREFERENCES_RX_FREQ = "rx_freq";
    public static final String APP_PREFERENCES_TX_CTCSS = "tx_ctcss";
    public static final String APP_PREFERENCES_RX_CTCSS = "rx_ctcss";
    public static final String APP_PREFERENCES_TX_SOS = "tx_sos";
    public static final String APP_PREFERENCES_RX_SOS = "rx_sos";
    public static final String APP_PREFERENCES_TX_CTCSS_SOS = "tx_ctcss_sos";
    public static final String APP_PREFERENCES_RX_CTCSS_SOS = "rx_ctcss_sos";
    public static final String APP_PREFERENCES_CHANNEL = "channel";
    public static final String APP_PREFERENCES_CHANNELS = "channels";
    public static final String APP_PREFERENCES_SQ = "sq";
    public static final String APP_PREFERENCES_STEP = "step";
    public static final String APP_PREFERENCES_OFFSET = "offset";
    public static final String APP_PREFERENCES_POWER = "power";
    public static final String APP_PREFERENCES_VOLUME = "volume";
    public static final String APP_PREFERENCES_SPEAKER = "speaker";
    public static final String APP_PREFERENCES_MIN_FREQ = "min_freq";
    public static final String APP_PREFERENCES_MAX_FREQ = "max_freq";
    public static final String APP_PREFERENCES_HISTORY = "history";
    public static final String APP_PREFERENCES_DELAY = "delay";
    public static final String APP_PREFERENCES_SCAN_CT = "scan_ct";
    public static final String APP_PREFERENCES_VIBRO = "vibro";
    public static final String APP_PREFERENCES_THEME = "theme";
    public static final String APP_PREFERENCES_GROUPS = "groups";
    public static final String APP_PREFERENCES_GROUP = "group";
    public static final String APP_PREFERENCES_KEY_SOS = "keySos";
    public static final String APP_PREFERENCES_KEY_BLOCK = "keyBlock";
    public static final String APP_PREFERENCES_KEY_SEARCH = "keySearch";
    public static final String FORMAT = "###.####";

    public static final Double[] steps = {0.005,0.00625,0.01,0.01250,0.015,0.02,0.025,0.03,0.05,0.1}; //Frequency step array
    public static final Double[] tones = {0.0,67.0,71.9,74.4,77.0,79.7,82.5,85.4,88.5,91.5,94.8,97.4,100.0,103.5,107.2,110.9,114.8,118.8,123.0,127.3,131.8,136.5,141.3,146.2,151.4,156.7,162.2,167.9,173.8,179.9,186.2,192.8,203.5,210.7,218.1,225.7,233.6,241.8,250.3};
    public static final Long[] delays = {100L,200L,500L,1000L,2000L,3000L,5000L,10000L,60000L,300000L};
    public Integer Ver = -1;
    public String Nick = "MyNick";
    public String History = "";
    public Double Longitude = 0.0;
    public Double Latitude = 0.0;
    public String[] ChannelList = {};
    public String[] curChannelList = {};
    public String Groups = "";
    public Integer curGroup = 0;
    public String ChannelName = "";
    public Integer curChannel = 0;
    public Double curRxFreq = 446.00625;
    public Double curTxFreq = 446.00625;
    public Double Offset = 0.0;
    public Integer curRxCt = 0;
    public Integer curTxCt = 0;
    public Double sosRxFreq = 446.00625;
    public Double sosTxFreq = 446.00625;
    public Integer sosRxCt = 0;
    public Integer sosTxCt = 0;
    public Double Step = steps[1];
    public Double minFreq = 400.0;
    public Double maxFreq = 480.0;
    public Integer Sq = 1;
    public Integer Volume = 5;
    public Boolean isSpeaker = true;
    public Boolean Power = false;
    public Boolean setSOS = false;
    public Boolean sosMode = false;
    public String Theme = "Black";
    public ManualFrequency mManual;
    public Channel mChannels;
    public Chat mChat;
    public Vibrator mVibrator;
    public Boolean isChat = true;
    public Boolean isBusy = false;
    public Boolean isBlocked = false;
    public Intercom mIntercom = new Intercom();
    public NotificationManager mNotificationManager;
    public NumberFormat Format;
    public Long ScanDelay = 3000L;
    public Boolean ScanChannel = false;
    public Boolean ScanForward = true;
    public Boolean ScanFreq = false;
    public Boolean ScanRxCt = false;
    public Boolean Vibrato = false;
    public Integer TabPos = 0;
    public String Search = "";
    public String FileName = Environment.getExternalStorageDirectory().getPath()+"/Channels.csv";
    public String ActionInput = "";
    public Integer keySos = 300;
    public Integer keyBlock = 301;
    public Integer keySearch = 84;

    //Old values to track changes
    private Double Old_curRxFreq;
    private Double Old_curTxFreq;
    private Integer Old_curRxCt;
    private Integer Old_curTxCt;
    private Integer Old_Sq;
    private Integer Old_Volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Settings get
         */
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = mSettings.edit();
        TabPos = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TAB, "0"));
        Nick = mSettings.getString(APP_PREFERENCES_NICK, Nick);
        History = mSettings.getString(APP_PREFERENCES_HISTORY, "<h1>"+getString(R.string.title_chat)+"</h1>");
        Power = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_POWER, Power.toString()));
        minFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MIN_FREQ, minFreq.toString()));
        maxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString()));
        ChannelList = mSettings.getString(APP_PREFERENCES_CHANNELS, getString(R.string.channels_std)).split("\\|");
        UpdateChannels();
        Groups = mSettings.getString(APP_PREFERENCES_GROUPS, getString(R.string.groups_list));
        curGroup = Integer.parseInt(mSettings.getString(APP_PREFERENCES_GROUP,curGroup.toString()));
        curChannelList = getChannelList(curGroup);
        curChannel = Integer.parseInt(mSettings.getString(APP_PREFERENCES_CHANNEL,curChannel.toString()));
        curRxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString()));
        curTxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString()));
        curRxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS, curRxCt.toString()));
        curTxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TX_CTCSS, curTxCt.toString()));
        sosRxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_SOS, sosRxFreq.toString()));
        sosTxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_TX_SOS,sosTxFreq.toString()));
        sosRxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS_SOS, sosRxCt.toString()));
        sosTxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TX_CTCSS_SOS, sosTxCt.toString()));
        Step = Double.parseDouble(mSettings.getString(APP_PREFERENCES_STEP, Step.toString()));
        Offset = Double.parseDouble(mSettings.getString(APP_PREFERENCES_OFFSET, Offset.toString()));
        Volume = Integer.parseInt(mSettings.getString(APP_PREFERENCES_VOLUME,Volume.toString()));
        isSpeaker = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_SPEAKER,isSpeaker.toString()));
        Vibrato = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_VIBRO, Vibrato.toString()));
        Sq = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SQ,Sq.toString()));
        ScanDelay = Long.parseLong(mSettings.getString(APP_PREFERENCES_DELAY,ScanDelay.toString()));
        ScanRxCt = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_SCAN_CT,ScanRxCt.toString()));
        Theme = mSettings.getString(APP_PREFERENCES_THEME,Theme);
        keySos = Integer.parseInt(mSettings.getString(APP_PREFERENCES_KEY_SOS,keySos.toString()));
        keyBlock = Integer.parseInt(mSettings.getString(APP_PREFERENCES_KEY_BLOCK,keyBlock.toString()));
        keySearch = Integer.parseInt(mSettings.getString(APP_PREFERENCES_KEY_SEARCH,keySearch.toString()));
        //Set old Values to send it with timer to module
        Old_curRxFreq = curRxFreq;
        Old_curTxFreq = curTxFreq;
        Old_curRxCt = curRxCt;
        Old_curTxCt = curTxCt;
        Old_Sq = Sq;
        Old_Volume = Volume;
        getTheme().applyStyle(Theme.equals("Black")?R.style.Black:R.style.Light,true);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Create format
        Format = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)Format).applyPattern(FORMAT);
        Format.setMinimumFractionDigits(FORMAT.length() - FORMAT.indexOf(".")-1);
        Format.setMinimumIntegerDigits(FORMAT.indexOf("."));

        // Set up the action bar.
        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPagerAdapter = new SampleAdapter(this, getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mManual = ManualFrequency.newInstance(this);
        mChannels = Channel.newInstance(this);
        mChat = Chat.newInstance(this);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mViewPager.setAdapter(mPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(!isBlocked){
                    TabPos = position;
                    mActionBar.setSelectedNavigationItem(position);
                }else{
                    mActionBar.setSelectedNavigationItem(TabPos);
                }
            }
        });
        /**
         * Adding Tabs
         */
        for(int i =0;i < mPagerAdapter.getCount();i++)
            mActionBar.addTab(
                    mActionBar.newTab()
                            .setText(mPagerAdapter.getPageTitle(i).toUpperCase())
                            .setTabListener(this));
        mActionBar.getTabAt(TabPos).select();
        mViewPager.setCurrentItem(TabPos);
        try{
            mIntercom.openCharDev();
        }catch (NoSuchMethodError e){
            Log.w("Intercom","openCharDev()");
        }
        try {
            mIntercom.resumeIntercomSetting();
        }catch (NoSuchMethodError e){
            Toast.makeText(this, R.string.non_runbo, Toast.LENGTH_SHORT).show();
            mIntercom = new uartIntercom();
            //mIntercom.openCharDev();
        }
        try{
            int m = mIntercom.checkMessageBuffer();
        }catch(NoSuchMethodError e){
            isChat = false;
        }catch (NullPointerException e){
            // Just check method existence
        }
        /**
         * State listener - off intercomm when calling or alarm
         */
        mState = new IntentFilter();
        mState.addAction("android.intent.action.PHONE_STATE");
        mState.addAction("android.intent.action.NEW_OUTGOING_CALL");
        mState.addAction("com.android.deskclock.ALARM_ALERT");
        mState.addAction("com.android.deskclock.ALARM_DONE");
        mStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!Power)return;
                if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                    mIntercom.intercomPowerOff();
                    isBusy = true;
                } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
                    String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        mIntercom.intercomPowerOff();
                        isBusy = true;
                    } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                        mIntercom.intercomPowerOff();
                        isBusy = true;
                    } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                        mIntercom.intercomPowerOn();
                        mIntercom.resumeIntercomSetting();
                        isBusy = false;
                    }
                }else if(intent.getAction().equals("com.android.deskclock.ALARM_ALERT")){
                    mIntercom.intercomPowerOff();
                    isBusy = true;
                }else if(intent.getAction().equals("com.android.deskclock.ALARM_DONE")){
                    mIntercom.intercomPowerOn();
                    mIntercom.resumeIntercomSetting();
                    isBusy = false;
                }
            }
        };
        registerReceiver(mStateReceiver, mState);


        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);


        if(Power){
            Toast.makeText(this, R.string.power_enabling, Toast.LENGTH_SHORT).show();
            mIntercom.intercomPowerOn();
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setRxFreq();
            setTxFreq();
            mIntercom.setCtcss(curRxCt);
            try{
                mIntercom.setTxCtcss(curTxCt);
                Ver = mIntercom.getIntercomVersion();
            }catch(NoSuchMethodError e){
                Log.w("Hardware","is too old hardware lib version");
            }
            mIntercom.setSq(Sq);
            mIntercom.resumeIntercomSetting();
            mIntercom.setVolume(Volume);
            if(isSpeaker)mIntercom.intercomSpeakerMode();else mIntercom.intercomHeadsetMode();
            mIntercom.resumeIntercomSetting();
            Toast.makeText(this, getString(R.string.power_enabled)+"\n"+getString(R.string.ver_label)+" "+Ver.toString(), Toast.LENGTH_SHORT).show();

        }
        /**
         * Start timed events
         */
        ChatHandler.sendEmptyMessageDelayed(0,5000L);
        Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            /**
             * Apply Settings and INIT
             */
            @Override
            public void run() {
                if(Power){
                    Boolean up = false;
                    if(!curRxFreq.equals(Old_curRxFreq)){
                        setRxFreq();
                        editor.putString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString());
                        Old_curRxFreq = curRxFreq;
                        up = true;
                    }
                    if(!curTxFreq.equals(Old_curTxFreq)){
                        setTxFreq();
                        editor.putString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString());
                        Old_curTxFreq = curTxFreq;
                        up = true;
                    }
                    if(!curRxCt.equals(Old_curRxCt)){
                        mIntercom.setCtcss(curRxCt);
                        editor.putString(APP_PREFERENCES_RX_CTCSS,curRxCt.toString());
                        Old_curRxCt = curRxCt;
                        up = true;
                    }
                    if(!curTxCt.equals(Old_curTxCt)){
                        try{
                            mIntercom.setTxCtcss(curTxCt);
                        }catch(NoSuchMethodError e){
                            Log.w("TXCTCSS","You version not allowed to set txctcss");
                        }
                        editor.putString(APP_PREFERENCES_TX_CTCSS,curTxCt.toString());
                        Old_curTxCt = curTxCt;
                        up = true;
                    }
                    if(!Sq.equals(Old_Sq)){
                        mIntercom.setSq(Sq);
                        editor.putString(APP_PREFERENCES_SQ,Sq.toString());
                        Old_Sq = Sq;
                        up = true;
                    }
                    if(!Volume.equals(Old_Volume)){
                        mIntercom.setVolume(Volume);
                        editor.putString(APP_PREFERENCES_VOLUME,Volume.toString());
                        Old_Volume = Volume;
                        up = true;
                    }
                    if(up){
                        //mIntercom.resumeIntercomSetting();//Commit setting
                        editor.putString(APP_PREFERENCES_CHANNEL,curChannel.toString());
                        editor.commit();
                        Notify();
                    }
                }
            }
        }, 0, 3000);
        Notify();
        if(Vibrato)mVibrator.vibrate(75L);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("Intent",intent.getAction());
        if(intent.getAction().equals("com.nxn.intercomm.CHAT")){
            mViewPager.setCurrentItem(2,true);
            mActionBar.setSelectedNavigationItem(2);
        }else if(intent.getAction().equals("com.nxn.intercomm.CHANNEL")){
            mViewPager.setCurrentItem(1,true);
            mActionBar.setSelectedNavigationItem(1);
        }else if(intent.getAction().equals("com.nxn.intercomm.FREQ")){
                mViewPager.setCurrentItem(0,true);
                mActionBar.setSelectedNavigationItem(0);
        }else if(intent.getAction().equals("com.nxn.intercomm.BLOCK")){
            setIsBlocked(true);
        }else if(intent.getAction().equals("com.nxn.intercomm.UNBLOCK")){
            setIsBlocked(false);
        }
    }

    public void Notify(){
        mNotificationManager.notify(R.id.pager, new NotificationCompat.Builder(MainActivity.this)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(String.format("%s %s [%s]", getString(R.string.app_name), ChannelName, ((Power) ? getString(R.string.on) : getString(R.string.off))))
                .setContentText(String.format("RX: %s[%s]  TX: %s[%s]", Format.format(curRxFreq), Integer.toString(curRxCt), Format.format(curTxFreq), Integer.toString(curTxCt)))
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class)
                        .setAction(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_LAUNCHER), 0))
                .build());
    }

    public void AppendMessage(String msg){
        History += "<div>"+msg+"</div>";
        TextView chat = (TextView)mViewPager.findViewById(R.id.chat);
        ScrollView scroll = (ScrollView)mViewPager.findViewById(R.id.scrollView);
        if(chat != null)chat.setText(Html.fromHtml(History + "<br/>", htmlImageGetter, htmlTagHandler));
        if(scroll != null)scroll.fullScroll(View.FOCUS_DOWN);
    }

    Html.ImageGetter htmlImageGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            int resId = getResources().getIdentifier(source, "drawable", getPackageName());
            Drawable ret = MainActivity.this.getResources().getDrawable(resId);
            ret.setBounds(0, 0, ret.getIntrinsicWidth(), ret.getIntrinsicHeight());
            return ret;
        }
    };
    Html.TagHandler htmlTagHandler = new Html.TagHandler() {
        public void handleTag(boolean opening, String tag, Editable output,	XMLReader xmlReader) {

        }
    };

    //Get list with groups
    public ArrayAdapter getGroups(){
        return new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, Groups.split(","));
    }

    //Get channels list in group
    public String[] getChannelList(int group){
        if(group == 0)return ChannelList;
        ArrayList<String> result = new ArrayList<String>();
        for (String ch : ChannelList) if(Integer.parseInt(ch.split(",")[7]) == group)result.add(ch);
        String[] mStringArray = new String[result.size()];
        mStringArray = result.toArray(mStringArray);
        return mStringArray;
    }

    //Find absolute position of channel by current data - possible for update channel params on edit in main list.
    public int getPosList(int ch,int group){
        if(group == 0)return ch;
        int p = 0;
        for(int i=0;i<ChannelList.length;i++)if(Integer.parseInt(ChannelList[i].split(",")[7]) == group)if(p==ch)return i;else p++;
        return -1;
    }

    public void setScanEnable(int group){
        for(int i=0;i<ChannelList.length;i++){
            String[] ch = ChannelList[i].split(",");
            if(group == Integer.parseInt(ch[7])|| group == 0){
                ch[6] = "true";
                ChannelList[i] = join(ch,",");
            }
        }
    }

    public void setScanInvert(int group){
        for(int i=0;i<ChannelList.length;i++){
            String[] ch = ChannelList[i].split(",");
            if(group == Integer.parseInt(ch[7])|| group == 0){
                ch[6] = (ch[6].toUpperCase().equals("TRUE"))?"false":"true";
                ChannelList[i] = join(ch,",");
            }
        }
    }

    public void UpdateChannels(){
        if(ChannelList.length > 0)
        if(ChannelList[0].split(",").length == 7){
            for(int i=0;i<ChannelList.length;i++)ChannelList[i] += (ChannelList[i].contains("PMR"))?",2":((ChannelList[i].contains("LPD"))?",3":",0");
            editor.putString(APP_PREFERENCES_CHANNELS,join(ChannelList,"|"));
            editor.commit();
        }
    }

    public static Double pow(Double base, int up){
        Double result = base;
        for(int i=1;i<up;i++)result*=base;
        return result;
    }

    public String setCh(Boolean set){
        String str = getString(R.string.no_data);
        if(curChannelList.length>0&&curChannel > -1){
            if(curChannel > curChannelList.length-1)curChannel = 0;
            String[] array = curChannelList[curChannel].split(",");
            if(array.length >= 5){
                curRxFreq = Double.parseDouble(array[1]);
                curTxFreq = Double.parseDouble(array[2]);
                curRxCt = Integer.parseInt(array[3]);
                curTxCt = Integer.parseInt(array[4]);
                Sq = Integer.parseInt(array[5]);
                ChannelName = array[0];
            }
            setTitle(ChannelName);
            Notify();
            str = //"<h1>"+ChannelName+"</h1>"+
                    getString(R.string.rxfreq_label)+": "+Format.format(curRxFreq)+"<br/>"+
                            getString(R.string.txfreq_label)+": "+Format.format(curTxFreq)+"<br/>"+
                            getString(R.string.rxctcss_label)+": "+curRxCt+"       "+
                            getString(R.string.txctcss_label)+": "+curTxCt;
        }else{
            curChannel = -1;
            setTitle(R.string.app_name);
        }
        if(set){
            try{
                TextView info = (TextView)mViewPager.findViewById(R.id.ch_info);
                info.setText(Html.fromHtml(str));
                ListView list = (ListView)mViewPager.findViewById(R.id.listView);
                list.smoothScrollToPosition(curChannel);
                EditText freq = (EditText)mViewPager.findViewById(R.id.freq);
                freq.setText(Format.format(curRxFreq));
                Spinner rxct = (Spinner)mViewPager.findViewById(R.id.rxctcss);
                rxct.setSelection(curRxCt);
                Spinner txct = (Spinner)mViewPager.findViewById(R.id.txctcss);
                txct.setSelection(curTxCt);
                Spinner sq = (Spinner)mViewPager.findViewById(R.id.sq);
                sq.setSelection(Sq-1);
            }catch (Exception e){
                //
            }
        }
        return str;
    }

    public int findCh(String str){
        int result=-1;
        for(int i=curChannel+1;i<curChannelList.length;i++){//find in upper part list
            if(curChannelList[i].toUpperCase().contains(str.toUpperCase())){
                result = i;
                break;
            }
        }
        if(result == -1)for(int i=0;i<curChannel-1;i++){//resume from lower part if NO result
            if(curChannelList[i].toUpperCase().contains(str.toUpperCase())){
                result = i;
                break;
            }
        }
        return result;
    }

    public ListAdapter ChannelsAdapter(){

        if(curChannelList.length == 0 || curChannelList[0].equals("")){
            return new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, new String[]{});
        }
        return new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_2, curChannelList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if(convertView == null){
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = (View)inflater.inflate(R.layout.list_row, null);
                }else{
                    row = (View)convertView;
                }
                TextView text1 = (TextView)row.findViewById(R.id.textView);
                TextView text2 = (TextView)row.findViewById(R.id.textView2);
                CheckBox checkBox = (CheckBox)row.findViewById(R.id.checkBox);
                String[] ch = curChannelList[position].split(",");
                checkBox.setChecked(Boolean.parseBoolean(ch[6]));
                checkBox.setContentDescription(Integer.toString(position));
                checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isBlocked)return;
                        if(Vibrato)mVibrator.vibrate(75L);
                        int pos = Integer.parseInt(view.getContentDescription().toString());
                        String[] chh = curChannelList[pos].split(",");
                        CheckBox nView = (CheckBox) view;
                        chh[6] = Boolean.toString(nView.isChecked());
                        ChannelList[getPosList(pos,curGroup)] = join(chh, ",");
                        curChannelList = getChannelList(curGroup);
                        editor.putString(APP_PREFERENCES_CHANNELS, join(ChannelList, "|"));
                        editor.commit();
                    }
                });
                View txt_layout = row.findViewById(R.id.txt_layout);
                txt_layout.setContentDescription(Integer.toString(position));
                txt_layout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isBlocked)return;
                        if(Vibrato)mVibrator.vibrate(75L);
                        curChannel = Integer.parseInt(view.getContentDescription().toString());
                        setCh(true);
                    }
                });
                // SOME do not work in themes - set text color manually
                int color = (Theme.equals("Black")?getResources().getColor(android.R.color.white):getResources().getColor(android.R.color.black));
                text1.setText(ch[0]+(curGroup == 0?" ["+Groups.split(",")[Integer.parseInt(ch[7])]+"]":""));
                text1.setTextColor(color);
                text2.setText("RX: "+ch[1]+" ["+ch[3]+"] TX: "+ch[2]+" ["+ch[4]+"]");
                text2.setTextColor(color);
                return row;
            }
        };
    }

    public void setTxFreq(){
        if(curTxFreq > maxFreq || curTxFreq < minFreq)return;
        Double f = curTxFreq * pow(10.0,Format.getMinimumFractionDigits());
        Log.w("setTxFreq",Integer.toString(f.intValue()));
        mIntercom.setTXFrequency(f.intValue());
    }

    public void setRxFreq(){
        if(curRxFreq > maxFreq || curRxFreq < minFreq)return;
        Double f = curRxFreq * pow(10.0,Format.getMinimumFractionDigits());
        Log.w("setRxFreq",Integer.toString(f.intValue()));
        mIntercom.setRXFrequency(f.intValue());
    }

    public String setFreq(Double freq , Double delta){
        Double num = curRxFreq;
        if(freq != 0.0)
            num = freq;
        num += delta;
        if(num < minFreq+Offset) num = minFreq;
        if(num > maxFreq-Offset) num = maxFreq;
        curRxFreq = num;
        curTxFreq = num+Offset;
        Notify();
        setTitle(R.string.app_name);
        return Format.format(num);
    }

    public String join(String[] array, String delimiter){
        if(array.length<1)return "";
        String str = array[0];
        for (int i=1;i<array.length;i++) str += delimiter+array[i];
        return str;
    }

    public String[] del(String[] array, int id){
        if(array.length <= 1)return new String[]{};
        int rid = (id == 0)?1:0;
        String str = array[rid];
        for (int i=1;i<id;i++) str += "|"+array[i];
        for (int i=id+rid+1;i<array.length;i++) str += "|"+array[i];
        return str.split("\\|");
    }

    public int findCt(Double[] array, Double tar){
        int out= 0;
        for(int i=0;i<array.length;i++)if(array[i].equals(tar))out = i;
        return out;
    }

    public String FromStream(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        String get_array[] = {"","","","0","0","5","true","0"};
        String tar_array = "";
        while ((line = reader.readLine())!= null) {
            String[] array = line.split(",");
            if(array.length>5 && !array[0].equals("Location")){
                if(!array[1].equals(""))get_array[0]=array[1];else get_array[0]=getString(R.string.title_chan);
                Double rx = Double.parseDouble(array[2]);
                Double offset = Double.parseDouble(array[4]);
                if(array[3].equals("") &&rx>minFreq && rx<maxFreq){
                    get_array[1]=get_array[2]=array[2];
                }else if(array[3].equals("-") &&rx-offset>minFreq&&rx>minFreq&&rx<maxFreq&&offset<maxFreq-minFreq){
                    get_array[1]=array[2];
                    get_array[2]=Double.toString(rx-offset);
                }else if(array[3].equals("+") &&rx+offset<maxFreq&&rx>minFreq&&rx<maxFreq){
                    get_array[1]=array[2];
                    get_array[2]=Double.toString(rx+offset);
                }else if(array[3].equals("split") &&rx>minFreq&&rx<maxFreq&&offset>minFreq&&offset<maxFreq){
                    get_array[1]=array[2];
                    get_array[2]=array[4];
                }
                if(array[5].equals("Tone")){
                    get_array[3]=get_array[4]=Integer.toString(findCt(tones,Double.parseDouble(array[6])));
                }else if(array[5].equals("TSQL")){
                    get_array[3]="0";
                    get_array[4]=Integer.toString(findCt(tones,Double.parseDouble(array[7])));
                }else if(array[5].equals("TSQL-R")){
                    get_array[3]=Integer.toString(findCt(tones,Double.parseDouble(array[6])));
                    get_array[4]="0";
                }else if(array[5].equals("Cross")){
                    get_array[3]=Integer.toString(findCt(tones,Double.parseDouble(array[6])));
                    get_array[4]=Integer.toString(findCt(tones,Double.parseDouble(array[7])));
                }
                if(!get_array[1].equals(""))if(tar_array.length() == 0)tar_array = join(get_array, ",");else tar_array += "|"+join(get_array, ",");
            }
        }
        reader.close();
        return tar_array;
    }

    public String importChannelListFromCSV (String filePath) throws Exception {
        FileInputStream fin = new FileInputStream(new File(filePath));
        String ret = FromStream(fin);
        fin.close();
        return ret;
    }

    public void exportChannelListToCSV (String filePath) throws Exception {
        FileWriter writer = new FileWriter(new File(filePath));
        writer.write("Location,Name,Frequency,Duplex,Offset,Tone,rToneFreq,cToneFreq,DtcsCode,DtcsPolarity,Mode,TStep,Skip,Comment,URCALL,RPT1CALL,RPT2CALL\n");
        int i = 1;
        for(String line:ChannelList){
            String[] get = line.split(",");
            writer.write(i+","+get[0]+","+get[1]+",split,"+get[2]+((get[3].equals("0"))?",,":",Cross,")+Double.toString(tones[Integer.parseInt(get[3])])+","+Double.toString(tones[Integer.parseInt(get[3])])+",023,NN,FM,5.00,,,,,,\n");
            i++;
        }
        writer.close();
    }

    @Override
    public boolean onKeyUp(int KeyCode,KeyEvent event){
        if(KeyCode == keySos){
            if(!sosMode){
                sosMode = true;
                mIntercom.intercomPowerOn();
                mIntercom.setCtcss(sosRxCt);
                mIntercom.setTxCtcss(sosTxCt);
                Double f = sosRxFreq * pow(10.0,Format.getMinimumFractionDigits());
                mIntercom.setRXFrequency(f.intValue());
                f = sosTxFreq * pow(10.0,Format.getMinimumFractionDigits());
                mIntercom.setTXFrequency(f.intValue());
                mIntercom.setSq(1);
                mIntercom.resumeIntercomSetting();
                Toast.makeText(this, getString(R.string.freq)+" SOS", Toast.LENGTH_SHORT).show();
                setTitle("SOS");
                if(Vibrato)mVibrator.vibrate(250L);
            }else{
                sosMode = false;
                if(Power){
                    mIntercom.setCtcss(curRxCt);
                    mIntercom.setTxCtcss(curTxCt);
                    mIntercom.setSq(Sq);
                    setRxFreq();
                    setTxFreq();
                    mIntercom.resumeIntercomSetting();
                }else{
                    mIntercom.intercomPowerOff();
                }
                setTitle(ChannelName);
                Toast.makeText(this, getString(R.string.freq), Toast.LENGTH_SHORT).show();
                if(Vibrato)mVibrator.vibrate(75L);
            }
            return true;
        }
        if(KeyCode == keyBlock){
            setIsBlocked(!isBlocked);
            if(isBlocked)Toast.makeText(this, getString(R.string.block), Toast.LENGTH_SHORT).show();
            return true;
        }
        if(KeyCode == keySearch){
            ActionInput = "search";
            mViewPager.setCurrentItem(1);
            DialogFragment srh = new InputDialog();
            srh.show(getSupportFragmentManager(),ActionInput);
            return true;
        }
        return false;
    }

    @Override
    public void onPostResume(){
        TabPos = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TAB,"0"));
        Nick = mSettings.getString(APP_PREFERENCES_NICK, Nick);
        History = mSettings.getString(APP_PREFERENCES_HISTORY, "<h1>"+getString(R.string.title_chat)+"</h1>");
        Power = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_POWER, Power.toString()));
        minFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MIN_FREQ, minFreq.toString()));
        maxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MAX_FREQ, maxFreq.toString()));
        ChannelList = mSettings.getString(APP_PREFERENCES_CHANNELS, getString(R.string.channels_std)).split("\\|");
        Groups = mSettings.getString(APP_PREFERENCES_GROUPS, getString(R.string.groups_list));
        curGroup = Integer.parseInt(mSettings.getString(APP_PREFERENCES_GROUP,curGroup.toString()));
        curChannelList = getChannelList(curGroup);
        curChannel = Integer.parseInt(mSettings.getString(APP_PREFERENCES_CHANNEL, curChannel.toString()));
        curRxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_FREQ, curRxFreq.toString()));
        curTxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString()));
        curRxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS, curRxCt.toString()));
        curTxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TX_CTCSS, curTxCt.toString()));
        sosRxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_SOS, sosRxFreq.toString()));
        sosTxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_TX_SOS,sosTxFreq.toString()));
        sosRxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS_SOS, sosRxCt.toString()));
        sosTxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TX_CTCSS_SOS, sosTxCt.toString()));
        Step = Double.parseDouble(mSettings.getString(APP_PREFERENCES_STEP, Step.toString()));
        Offset = Double.parseDouble(mSettings.getString(APP_PREFERENCES_OFFSET, Offset.toString()));
        Volume = Integer.parseInt(mSettings.getString(APP_PREFERENCES_VOLUME, Volume.toString()));
        isSpeaker = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_SPEAKER, isSpeaker.toString()));
        Vibrato = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_VIBRO, Vibrato.toString()));
        Sq = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SQ, Sq.toString()));
        ScanDelay = Long.parseLong(mSettings.getString(APP_PREFERENCES_DELAY, ScanDelay.toString()));
        ScanRxCt = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_SCAN_CT,ScanRxCt.toString()));
        Theme = mSettings.getString(APP_PREFERENCES_THEME,Theme);
        keySos = Integer.parseInt(mSettings.getString(APP_PREFERENCES_KEY_SOS,keySos.toString()));
        keyBlock = Integer.parseInt(mSettings.getString(APP_PREFERENCES_KEY_BLOCK,keyBlock.toString()));
        keySearch = Integer.parseInt(mSettings.getString(APP_PREFERENCES_KEY_SEARCH,keySearch.toString()));
        super.onPostResume();
    }
    @Override
    public void onStop(){
        editor.putString(APP_PREFERENCES_TAB,Integer.toString(TabPos));
        editor.putString(APP_PREFERENCES_NICK,Nick);
        editor.putString(APP_PREFERENCES_HISTORY,History);
        editor.putString(APP_PREFERENCES_POWER,Power.toString());
        editor.putString(APP_PREFERENCES_MIN_FREQ,minFreq.toString());
        editor.putString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString());
        editor.putString(APP_PREFERENCES_CHANNELS,join(ChannelList, "|"));
        editor.putString(APP_PREFERENCES_GROUPS,Groups);
        editor.putString(APP_PREFERENCES_GROUP,curGroup.toString());
        editor.putString(APP_PREFERENCES_CHANNEL,curChannel.toString());
        editor.putString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString());
        editor.putString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString());
        editor.putString(APP_PREFERENCES_RX_CTCSS,curRxCt.toString());
        editor.putString(APP_PREFERENCES_TX_CTCSS,curTxCt.toString());
        editor.putString(APP_PREFERENCES_RX_SOS,sosRxFreq.toString());
        editor.putString(APP_PREFERENCES_TX_SOS,sosTxFreq.toString());
        editor.putString(APP_PREFERENCES_RX_CTCSS_SOS,sosRxCt.toString());
        editor.putString(APP_PREFERENCES_TX_CTCSS_SOS,sosTxCt.toString());
        editor.putString(APP_PREFERENCES_STEP,Step.toString());
        editor.putString(APP_PREFERENCES_OFFSET,Offset.toString());
        editor.putString(APP_PREFERENCES_VOLUME,Volume.toString());
        editor.putString(APP_PREFERENCES_SPEAKER,isSpeaker.toString());
        editor.putString(APP_PREFERENCES_VIBRO, Vibrato.toString());
        editor.putString(APP_PREFERENCES_SQ,Sq.toString());
        editor.putString(APP_PREFERENCES_DELAY,ScanDelay.toString());
        editor.putString(APP_PREFERENCES_SCAN_CT,ScanRxCt.toString());
        editor.putString(APP_PREFERENCES_THEME,Theme);
        editor.putString(APP_PREFERENCES_KEY_SOS,keySos.toString());
        editor.putString(APP_PREFERENCES_KEY_BLOCK,keyBlock.toString());
        editor.putString(APP_PREFERENCES_KEY_SEARCH,keySearch.toString());
        editor.commit();
        super.onStop();
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState){
        mManual.main = this;
        mChat.main = this;
        mChannels.main = this;
        super.onPostCreate(savedInstanceState);
    }
    /*@Override
    public boolean onKeyDown(int KeyCode,KeyEvent event){
        Toast.makeText(this, "KeyCode="+KeyCode+" Ev="+event.getCharacters(), Toast.LENGTH_SHORT).show();
        return false;
    }*/
    @Override
    public boolean onMenuOpened(int id,Menu menu){
        MenuItem block = menu.findItem(R.id.block);
        if(block != null)block.setChecked(isBlocked);
        return super.onMenuOpened(id,menu);
    }

    public void setIsBlocked(Boolean block){
        isBlocked=block;
        if(!isBlocked){
            ListView list = (ListView)mViewPager.findViewById(R.id.listView);
            if(list != null)list.setAdapter(ChannelsAdapter());
            if(list != null)list.smoothScrollToPosition(curChannel);
            EditText freq = (EditText)mViewPager.findViewById(R.id.freq);
            if(freq != null)freq.setText(Format.format(curRxFreq));
            Spinner rxct = (Spinner)mViewPager.findViewById(R.id.rxctcss);
            if(rxct != null)rxct.setSelection(curRxCt);
            Spinner txct = (Spinner)mViewPager.findViewById(R.id.txctcss);
            if(txct != null)txct.setSelection(curTxCt);
            Spinner sq = (Spinner)mViewPager.findViewById(R.id.sq);
            if(sq != null)sq.setSelection(Sq-1);
            Spinner group = (Spinner)mViewPager.findViewById(R.id.group_list);
            if(group != null)group.setSelection(curGroup);
            mViewPager.setCurrentItem(TabPos, true);
            mActionBar.setSelectedNavigationItem(TabPos);
        }
    }

    @Override
    public void onClick(View view){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
            try{
                MenuItem item = menu.findItem(R.id.power);
                assert item != null;
                if(Power) {
                    item.setIcon(android.R.drawable.ic_lock_idle_charging);
                    item.setChecked(Power);
                }
            }catch (Error e){
                Log.w("Power","can not set icon");
            }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final ListView list = (ListView)mViewPager.findViewById(R.id.listView);
        if(item.getItemId() == R.id.block){
            setIsBlocked(!isBlocked);
            item.setChecked(isBlocked);
        }
        if(!isBlocked)
        switch (item.getItemId()){
            case R.id.share_gps:
                if(isChat&&!Longitude.equals(0.0)&&!Latitude.equals(0.0)){
                    String msg = String.format(getString(R.string.gpsmsg), Nick, Latitude, Longitude, Latitude, Longitude);
                    if(Power)mIntercom.sendMessage(msg);
                    AppendMessage(msg);
                }
                return true;
            case R.id.change_theme:
                if(Theme.equals("Black")){
                    Theme = "Light";
                }else{
                    Theme = "Black";
                }
                editor.putString(APP_PREFERENCES_THEME,Theme);
                editor.commit();
                //getTheme().applyStyle(Theme.equals("Black") ? R.style.Black : R.style.Light, true);
                recreate();
                return true;
            case R.id.clear_history_action:
                History = "<h1>"+getString(R.string.title_chat)+"</h1>";
                try{
                    TextView chat = (TextView)mViewPager.findViewById(R.id.chat);
                    chat.setText(Html.fromHtml(History+"<br/>", htmlImageGetter, htmlTagHandler));
                }catch (Exception e){
                    //
                }
                return true;
            case R.id.clear_channels:
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.clear_channels)+"?")
                        .setCancelable(false)
                        .setPositiveButton(getString(android.R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mViewPager.setCurrentItem(1);
                                        ChannelList = getString(R.string.channels_std).split("\\|");
                                        curChannelList = getChannelList(curGroup);
                                        curChannel = 0;
                                        editor.putString(APP_PREFERENCES_CHANNEL,curChannel.toString());
                                        editor.putString(APP_PREFERENCES_CHANNELS,join(ChannelList, "|"));
                                        editor.commit();
                                        list.setAdapter(ChannelsAdapter());
                                        setCh(true);
                                    }
                                })
                        .setNegativeButton(getString(android.R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).show();

                return true;
            case android.R.id.home:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.imp_csv:
                ActionInput = "import";
                mViewPager.setCurrentItem(1);
                DialogFragment imp = new InputDialog();
                imp.show(getSupportFragmentManager(),ActionInput);
                return true;
            case R.id.exp_csv:
                ActionInput = "export";
                mViewPager.setCurrentItem(1);
                DialogFragment exp = new InputDialog();
                exp.show(getSupportFragmentManager(),ActionInput);
                return true;
            case R.id.delete_all_action:
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.delete_all)+"?")
                        .setCancelable(false)
                        .setPositiveButton(getString(android.R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mViewPager.setCurrentItem(1);
                                        ChannelList = new String[]{};
                                        curChannelList = new String[]{};
                                        curChannel=-1;
                                        editor.putString(APP_PREFERENCES_CHANNEL,curChannel.toString());
                                        editor.putString(APP_PREFERENCES_CHANNELS,"");
                                        editor.commit();
                                        list.setAdapter(ChannelsAdapter());
                                        setCh(true);
                                    }
                                })
                        .setNegativeButton(getString(android.R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                return true;
            case R.id.ch_del:
                mViewPager.setCurrentItem(1);
                ChannelList=del(ChannelList,getPosList(curChannel,curGroup));
                curChannelList = getChannelList(curGroup);
                if(curChannel>curChannelList.length-1)curChannel=curChannelList.length-1;
                editor.putString(APP_PREFERENCES_CHANNEL,curChannel.toString());
                editor.putString(APP_PREFERENCES_CHANNELS, join(ChannelList, "|"));
                editor.commit();
                list.setAdapter(ChannelsAdapter());
                setCh(true);
                return true;
            case R.id.ch_add:
                setSOS = false;
                mViewPager.setCurrentItem(1);
                DialogFragment edit = new ChannelAddDialog();
                edit.show(getSupportFragmentManager(),"channel_edit");
                return true;
            case R.id.sos_set:
                setSOS = true;
                DialogFragment sos = new ChannelAddDialog();
                sos.show(getSupportFragmentManager(),"sos_edit");
                return true;
            case R.id.ch_search:
                ActionInput = "search";
                mViewPager.setCurrentItem(1);
                DialogFragment srh = new InputDialog();
                srh.show(getSupportFragmentManager(),ActionInput);
                break;
            case R.id.about:
                DialogFragment about = new AboutDialog();
                about.show(getSupportFragmentManager(),"about");
                return true;
            case R.id.action_settings:
                DialogFragment settings = new SettingsDialog();
                settings.show(getSupportFragmentManager(),"settings");
                return true;
            case R.id.action_quit:
                try{
                    mIntercom.closeCharDev();
                }catch (NoSuchMethodError e){
                    Log.w("Intercom","closeCharDev()");
                }
                mNotificationManager.cancel(R.id.pager);
                ScanFreq = false;
                ScanChannel = false;
                ChatHandler.removeMessages(0);
                ScanHandler.removeMessages(0);
                unregisterReceiver(mStateReceiver);
                mLocationManager.removeUpdates(mLocationListener);
                if(Vibrato)mVibrator.vibrate(75L);
                finish();
                return true;
            case R.id.power:
                if(Vibrato)mVibrator.vibrate(75L);
                if(item.isChecked()){
                    mIntercom.intercomPowerOff();
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        //
                    }
                    try{
                        mIntercom.closeCharDev();
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","closeCharDev()");
                    }
                    Toast.makeText(this, R.string.power_disabled, Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_lock_power_off);
                    item.setChecked(false);
                }else{
                    try{
                        mIntercom.openCharDev();
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","openCharDev()");
                    }
                    mIntercom.intercomPowerOn();
                    try {
                        Thread.sleep(400L); //Boot-up wait
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setTxFreq();
                    setRxFreq();
                    //mIntercom.setRadioFrequency(getFreq());
                    mIntercom.setSq(Sq);
                    mIntercom.setCtcss(curRxCt);
                    try{
                        Ver = mIntercom.getIntercomVersion();
                        mIntercom.setTxCtcss(curTxCt);
                    }catch(NoSuchMethodError e){
                        Log.w("Intercom","getIntercomVersion()");
                    }
                    mIntercom.setVolume(Volume);
                    if(isSpeaker)mIntercom.intercomSpeakerMode();else mIntercom.intercomHeadsetMode();
                    mIntercom.resumeIntercomSetting();
                    Toast.makeText(this, getString(R.string.power_enabled)+"\n"+getString(R.string.ver_label)+" "+Ver.toString(), Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_lock_idle_charging);
                    item.setChecked(true);
                }
                Power=item.isChecked();
                editor.putString(APP_PREFERENCES_POWER,Power.toString());
                editor.commit();
                Notify();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        if(!isBlocked){
            TabPos = tab.getPosition();
            mViewPager.setCurrentItem(tab.getPosition());
        }else{
            mViewPager.setCurrentItem(TabPos);
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public class SampleAdapter extends FragmentStatePagerAdapter {
        MainActivity main = null;
        public SampleAdapter(MainActivity main, FragmentManager mgr) {
            super(mgr);
            this.main = main;
        }
        @Override
        public int getCount() {
            return (main.isChat)?3:2;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mManual;
                case 1:
                    return mChannels;
                case 2:
                    return mChat;
            }
            return null;
        }

        @Override
        public String getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_freq);
                case 1:
                    return getString(R.string.title_chan);
                case 2:
                    return getString(R.string.title_chat);
            }
            return null;
        }
    }

    public static class ManualFrequency extends Fragment implements
            OnClickListener,
            View.OnLongClickListener,
            TextWatcher,
            AdapterView.OnItemSelectedListener,
            SeekBar.OnSeekBarChangeListener
    {
        private MainActivity main;
        private Boolean isLongTouch = false;
        public ManualFrequency(){

        }
        public static ManualFrequency newInstance(MainActivity main){
            ManualFrequency fragment = new ManualFrequency();
            fragment.main = main;
            return fragment;
        }
        @Override
        public void onClick(View view){
            if(main.isBlocked)return;
            EditText e = (EditText)getView().findViewById(R.id.freq);
            ImageButton snd = (ImageButton)getView().findViewById(R.id.sound_src);
            if(main.Vibrato)main.mVibrator.vibrate(75L);
            if(!isLongTouch)switch (view.getId()){
                case R.id.freq:
                    if(main.ScanFreq)Toast.makeText(main, getString(R.string.scan_stopped), Toast.LENGTH_SHORT).show();
                    main.ScanFreq = false;
                    break;
                case R.id.freq_next:
                    //Scan = false;
                    e.setText(main.setFreq(0.0,main.Step));
                    break;
                case R.id.freq_prew:
                    //Scan = false;
                    e.setText(main.setFreq(0.0, -main.Step));
                    break;
                case R.id.sound_src:
                    if(main.isSpeaker){
                        snd.setImageResource(android.R.drawable.ic_lock_silent_mode);
                        main.isSpeaker = false;
                        if(main.Power)main.mIntercom.intercomHeadsetMode();
                    }else{
                        snd.setImageResource(android.R.drawable.ic_lock_silent_mode_off);//stat_sys_headset
                        main.isSpeaker = true;
                        if(main.Power)main.mIntercom.intercomSpeakerMode();
                    }
                    break;
            } else isLongTouch = false;
        }
        @Override
        public boolean onLongClick(View view) {
            if(main.isBlocked)return true;
            isLongTouch = true;
            if(main.Vibrato)main.mVibrator.vibrate(75L);
            switch (view.getId()){
                case R.id.freq_next:
                    main.ScanFreq = true;
                    main.ScanForward = true;
                    main.ScanChannel = false;
                    main.ScanHandler.sendEmptyMessageDelayed(0,main.ScanDelay);
                    Toast.makeText(super.getActivity(), getString(R.string.scan)+" "+getString(R.string.freq_next), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.freq_prew:
                    main.ScanFreq = true;
                    main.ScanForward = false;
                    main.ScanChannel = false;
                    main.ScanHandler.sendEmptyMessageDelayed(0,main.ScanDelay);
                    Toast.makeText(super.getActivity(), getString(R.string.scan)+" "+getString(R.string.freq_prew), Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(main.isBlocked)return;
            main.Volume = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(main.isBlocked)return;
            if(main.Vibrato)main.mVibrator.vibrate(75L);
            switch (parent.getId()){
                case R.id.rxctcss:
                    main.curRxCt = position;
                    break;
                case R.id.txctcss:
                    main.curTxCt = position;
                    break;
                case R.id.sq:
                    main.Sq = position+1;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            main = (MainActivity)super.getActivity();
            View rootView = inflater.inflate(R.layout.manual_freq, container, false);
            assert rootView != null;
            rootView.findViewById(R.id.freq_next).setOnClickListener(this);
            rootView.findViewById(R.id.freq_prew).setOnClickListener(this);
            rootView.findViewById(R.id.freq_next).setOnLongClickListener(this);
            rootView.findViewById(R.id.freq_prew).setOnLongClickListener(this);
            ImageButton snd = (ImageButton)rootView.findViewById(R.id.sound_src);
            snd.setOnClickListener(this);
            EditText freq = (EditText)rootView.findViewById(R.id.freq);
            freq.setOnClickListener(this);
            freq.addTextChangedListener(this);
            Spinner sq = (Spinner)rootView.findViewById(R.id.sq);
            Spinner rxct = (Spinner)rootView.findViewById(R.id.rxctcss);
            Spinner txct = (Spinner)rootView.findViewById(R.id.txctcss);
            SeekBar vol = (SeekBar)rootView.findViewById(R.id.volume);
            vol.setOnSeekBarChangeListener(this);
            sq.setOnItemSelectedListener(this);
            rxct.setOnItemSelectedListener(this);
            txct.setOnItemSelectedListener(this);
            /**
             * TODO: Check weather of views before set him
             */
            //if(savedInstanceState.isEmpty())
            try{
                freq.setText(main.Format.format(main.curRxFreq));
                sq.setSelection(main.Sq - 1);
                rxct.setSelection(main.curRxCt);
                txct.setSelection(main.curTxCt);
                vol.setProgress(main.Volume);
                if(main.isSpeaker){
                    snd.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                }else{
                    snd.setImageResource(android.R.drawable.ic_lock_silent_mode);//stat_sys_headset
                }
            }catch (NullPointerException e){
                //
            }

            return rootView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(main.isBlocked)return;
            Double freq;
            if(s.length()>=4&&before != count){
                freq = Double.parseDouble(s.toString());
                main.setFreq(freq ,0.0);
            }

        }

        @Override
        public void afterTextChanged(Editable string) {
            if(main.isBlocked)return;
            Double freq;
            if(main.Vibrato)main.mVibrator.vibrate(75L);
            if(string.length()>0){
                freq = Double.parseDouble(string.toString());
                //If valid number
                if(freq != 0.0){
                    //If bigger than maximum
                    if(freq > main.maxFreq && string.length() >= 3){
                        Toast.makeText(super.getActivity(), getString(R.string.set_max)+" "+main.maxFreq.toString(), Toast.LENGTH_SHORT).show();
                        if(string.length()>2)string.delete(2,string.length());
                        return;
                    }
                    //If lower than minimum
                    if(freq < main.minFreq && string.length() >=3){
                        Toast.makeText(super.getActivity(), getString(R.string.set_min)+" "+main.minFreq.toString(), Toast.LENGTH_SHORT).show();
                        if(string.length()>2)string.delete(2,string.length());
                        return;
                    }
                    //Add point after 3 valid sym entered
                    if(string.length() == 3)string.append(".");
                }
            }
        }
    }

    public static class Channel extends Fragment implements
            OnClickListener,
            View.OnLongClickListener,
            AdapterView.OnItemSelectedListener
    {
        private MainActivity main;
        private Boolean isLongTouch = false;
        private String ch_name = "";
        public Channel(){

        }
        public static Channel newInstance(MainActivity main){
            Channel fragment = new Channel();
            fragment.main = main;
            return fragment;
        }
        @Override
        public void onClick(View view){
            if(main.isBlocked)return;
            if(main.Vibrato)main.mVibrator.vibrate(75L);
            if(main.curChannel == -1)return;
            if(!isLongTouch)
            switch (view.getId()){
                case R.id.ch_next:
                    if(main.curChannel<main.curChannelList.length-1)main.curChannel++;
                    if(main.ScanChannel)Toast.makeText(main, getString(R.string.scan_stopped), Toast.LENGTH_SHORT).show();
                    main.setCh(true);
                    main.ScanChannel = false;
                    break;
                case R.id.ch_prew:
                    if(main.curChannel>0)main.curChannel--;
                    if(main.ScanChannel)Toast.makeText(main, getString(R.string.scan_stopped), Toast.LENGTH_SHORT).show();
                    main.setCh(true);
                    main.ScanChannel = false;
                    break;
                case R.id.ch_info:
                    DialogFragment editor = new ChannelEditDialog();
                    if(!main.ScanChannel)editor.show(super.getFragmentManager(),"channel_edit");
                    if(main.ScanChannel)Toast.makeText(main, getString(R.string.scan_stopped), Toast.LENGTH_SHORT).show();
                    main.ScanChannel = false;
                    break;
                case R.id.invert:
                    main.setScanInvert(main.curGroup);
                    main.curChannelList = main.getChannelList(main.curGroup);
                    ListView list = (ListView)getView().findViewById(R.id.listView);
                    list.setAdapter(main.ChannelsAdapter());
                    list.setSelection(main.curChannel);
                    Toast.makeText(main, getString(R.string.invert), Toast.LENGTH_SHORT).show();
                    main.editor.putString(APP_PREFERENCES_CHANNELS,main.join(main.ChannelList,"|"));
                    main.editor.commit();
                    break;
                case R.id.check_all:
                    main.setScanEnable(main.curGroup);
                    main.curChannelList = main.getChannelList(main.curGroup);
                    ListView list1 = (ListView)getView().findViewById(R.id.listView);
                    list1.setAdapter(main.ChannelsAdapter());
                    list1.setSelection(main.curChannel);
                    Toast.makeText(main, getString(R.string.check_all), Toast.LENGTH_SHORT).show();
                    main.editor.putString(APP_PREFERENCES_CHANNELS,main.join(main.ChannelList,"|"));
                    main.editor.commit();
                    break;
            }else isLongTouch=false;

        }
        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            rootView = inflater.inflate(R.layout.channels, container, false);
            assert rootView != null;
            main = (MainActivity)super.getActivity();
            rootView.findViewById(R.id.ch_next).setOnClickListener(this);
            rootView.findViewById(R.id.ch_prew).setOnClickListener(this);
            rootView.findViewById(R.id.check_all).setOnClickListener(this);
            rootView.findViewById(R.id.invert).setOnClickListener(this);
            rootView.findViewById(R.id.ch_next).setOnLongClickListener(this);
            rootView.findViewById(R.id.ch_prew).setOnLongClickListener(this);
            TextView ch_info = (TextView)rootView.findViewById(R.id.ch_info);
            ListView list = (ListView)rootView.findViewById(R.id.listView);
            list.setAdapter(main.ChannelsAdapter());
            ch_info.setText(Html.fromHtml(main.setCh(false)));
            ch_info.setOnClickListener(this);
            ch_info.setOnLongClickListener(this);
            Spinner group = (Spinner)rootView.findViewById(R.id.group_list);
            group.setOnItemSelectedListener(this);
            group.setAdapter(main.getGroups());
            group.setSelection(main.curGroup);
            return rootView;
        }

        @Override
        public boolean onLongClick(View view) {
            if(main.isBlocked)return true;
            if(main.curChannel == -1)return true;
            isLongTouch = true;
            if(main.Vibrato)main.mVibrator.vibrate(75L);
            switch (view.getId()){
                case R.id.ch_next:
                    main.ScanChannel = true;
                    main.ScanForward = true;
                    main.ScanFreq = false;
                    main.ScanHandler.sendEmptyMessageDelayed(0,main.ScanDelay);
                    Toast.makeText(super.getActivity(), getString(R.string.scan)+" "+getString(R.string.ch_next_help), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.ch_prew:
                    main.ScanChannel = true;
                    main.ScanForward = false;
                    main.ScanFreq = false;
                    main.ScanHandler.sendEmptyMessageDelayed(0,main.ScanDelay);
                    Toast.makeText(super.getActivity(), getString(R.string.scan)+" "+getString(R.string.ch_prew_help), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.ch_info:
                    Toast.makeText(super.getActivity(), getString(R.string.group_label), Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            if(main.isBlocked)return;
            main.curGroup = pos;
            main.curChannelList = main.getChannelList(pos);
            main.curChannel = 0;
            ListView list = (ListView)getView().findViewById(R.id.listView);
            list.setAdapter(main.ChannelsAdapter());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

        public class ChannelEditDialog extends DialogFragment{
            @Override
            public Dialog onCreateDialog(Bundle savedInstanseState){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View Settings = inflater.inflate(R.layout.channel, null);
                assert Settings != null;
                String[] ch = main.curChannelList[main.curChannel].split(",");
                Log.e("Channel",main.curChannelList[main.curChannel]);
                final EditText name = (EditText)Settings.findViewById(R.id.ch_name);
                name.setText(ch[0]);
                final EditText rx = (EditText)Settings.findViewById(R.id.ch_rxfreq);
                rx.setText(ch[1]);
                final EditText tx = (EditText)Settings.findViewById(R.id.ch_txfreq);
                tx.setText(ch[2]);
                final Spinner rxct = (Spinner)Settings.findViewById(R.id.ch_rxctcss);
                rxct.setSelection(Integer.parseInt(ch[3]));
                final Spinner txct = (Spinner)Settings.findViewById(R.id.ch_txctcss);
                txct.setSelection(Integer.parseInt(ch[4]));
                final Spinner sq = (Spinner)Settings.findViewById(R.id.ch_sq);
                sq.setSelection(Integer.parseInt(ch[5])-1);
                final CheckBox scan = (CheckBox)Settings.findViewById(R.id.ch_scan);
                scan.setChecked(Boolean.parseBoolean(ch[6]));
                final Spinner group = (Spinner)Settings.findViewById(R.id.ch_group);
                group.setAdapter(main.getGroups());
                group.setSelection(Integer.parseInt(ch[7]));
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(Settings)
                        // Add action buttons
                        .setPositiveButton(R.string.ch_apply, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //add ch to list
                                String result =
                                        name.getText().toString().replace(",",".").replace("|","/")+","+
                                        rx.getText().toString()+","+
                                        tx.getText().toString()+","+
                                        Integer.toString(rxct.getSelectedItemPosition())+","+
                                        Integer.toString(txct.getSelectedItemPosition())+","+
                                        Integer.toString(sq.getSelectedItemPosition()+1)+","+
                                        Boolean.toString(scan.isChecked())+ "," +
                                        Integer.toString(group.getSelectedItemPosition());
                                Log.e("OnEdit",Integer.toString(main.getPosList(main.curChannel,main.curGroup)));
                                main.ChannelList[main.getPosList(main.curChannel,main.curGroup)]=result;
                                main.curGroup = (main.curGroup == 0)?main.curGroup:group.getSelectedItemPosition();
                                main.curChannelList = main.getChannelList(main.curGroup);
                                if(main.curChannel>main.curChannelList.length-1)main.curChannel=main.curChannelList.length-1;
                                ListView list = (ListView)main.mViewPager.findViewById(R.id.listView);
                                list.setAdapter(main.ChannelsAdapter());
                                Spinner group_list = (Spinner)main.mViewPager.findViewById(R.id.group_list);
                                group_list.setSelection(main.curGroup);
                                main.setCh(true);
                                main.editor.putString(APP_PREFERENCES_CHANNELS,main.join(main.ChannelList, "|"));
                                main.editor.putString(APP_PREFERENCES_CHANNEL,main.curChannel.toString());
                                main.editor.commit();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ChannelEditDialog.this.getDialog().cancel();
                            }
                        });
                return builder.create();

            }
        }
    }

    /**
     * TODO Scan Frequency with service
     */
    private Handler ScanHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Boolean set = true;
            if(ScanFreq){
                EditText freq = (EditText)mViewPager.findViewById(R.id.freq);
                Spinner rx = (Spinner)mViewPager.findViewById(R.id.rxctcss);
                if(ScanForward){
                    if(ScanRxCt&&curRxCt<tones.length-1){
                        curRxCt++;
                        if(rx != null)rx.setSelection(curRxCt);
                    } else {
                        if(curRxFreq+Step > maxFreq-Offset) curRxFreq = minFreq-Step;
                        if(curRxCt == tones.length-1)curRxCt=-1;
                        if(freq != null)freq.setText(setFreq(curRxFreq,Step));
                    }
                }else{
                    if(ScanRxCt&&curRxCt>0){
                        curRxCt--;
                        if(rx != null)rx.setSelection(curRxCt);
                    } else {
                        if(curRxFreq-Step < minFreq+Offset) curRxFreq = maxFreq+Step;
                        if(curRxCt == 0)curRxCt=tones.length;
                        if(freq != null)freq.setText(setFreq(curRxFreq,-Step));
                    }
                }
                if(Power&&!isBusy){
                    if(ScanRxCt&&curRxCt>-1&&curRxCt<tones.length){
                        Old_curRxCt=curRxCt;
                        mIntercom.setCtcss(curRxCt);
                        Toast.makeText(MainActivity.this, Format.format(curRxFreq)+" MHz "+getString(R.string.rxctcss_label) + "  "+tones[curRxCt] + " Hz ["+curRxCt+"]", Toast.LENGTH_SHORT).show();
                        editor.putString(APP_PREFERENCES_RX_CTCSS,curRxCt.toString());
                        editor.commit();
                    }else{
                        Old_curRxFreq = curRxFreq;
                        Old_curTxFreq = curTxFreq = curRxFreq + Offset;
                        setRxFreq();
                        setTxFreq();
                        Toast.makeText(MainActivity.this, getString(R.string.title_freq) + "  "+Format.format(curRxFreq)+" MHz", Toast.LENGTH_SHORT).show();
                        editor.putString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString());
                        editor.putString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString());
                        editor.commit();
                    }
                }
                ScanHandler.sendMessageDelayed(new Message(),ScanDelay);
            }
            if(ScanChannel){
                if(ScanForward){
                    if(curChannel<curChannelList.length-1)curChannel++;else curChannel=0;
                    if(!Boolean.parseBoolean(curChannelList[curChannel].split(",")[6])){
                        set = false;
                        ScanHandler.sendEmptyMessageDelayed(0,0);
                    }else{
                        setCh(true);
                        ScanHandler.sendEmptyMessageDelayed(0,ScanDelay);
                    }
                }else{
                    if(curChannel>0)curChannel--;else curChannel=curChannelList.length-1;
                    if(!Boolean.parseBoolean(curChannelList[curChannel].split(",")[6])){
                        set = false;
                        ScanHandler.sendEmptyMessageDelayed(0,0);
                    }else{
                        setCh(true);
                        ScanHandler.sendEmptyMessageDelayed(0,ScanDelay);
                    }
                }
            }
            if(set&&Power&&ScanChannel&&!isBusy){
                Old_curRxFreq = curRxFreq;
                Old_curTxFreq = curTxFreq;
                Old_curRxCt = curRxCt;
                Old_curTxCt = curTxCt;
                Old_Sq = Sq;
                setRxFreq();
                setTxFreq();
                mIntercom.setCtcss(curRxCt);
                try{
                    mIntercom.setTxCtcss(curTxCt);
                }catch(NoSuchMethodError e){
                    Log.e("Scanner","Can not set TxCTCSS - no such method");
                }
                mIntercom.setSq(Sq);
                mIntercom.resumeIntercomSetting();
                Toast.makeText(MainActivity.this, getString(R.string.title_chan) + "  "+ChannelName, Toast.LENGTH_SHORT).show();
                editor.putString(APP_PREFERENCES_CHANNEL,curChannel.toString());
                editor.putString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString());
                editor.putString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString());
                editor.putString(APP_PREFERENCES_RX_CTCSS,curRxCt.toString());
                editor.putString(APP_PREFERENCES_TX_CTCSS,curTxCt.toString());
                editor.putString(APP_PREFERENCES_SQ,Sq.toString());
                editor.commit();
            }

        }
    };

    public static class Chat extends Fragment implements
            OnClickListener,
            View.OnKeyListener
    {
        private MainActivity main;
        public Chat(){

        }
        public static Chat newInstance(MainActivity main){
            Chat fragment = new Chat();
            fragment.main = main;
            return fragment;
        }
        @Override
        public void onClick(View view){

        }
        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            main = (MainActivity)super.getActivity();
            rootView = inflater.inflate(R.layout.chat, container, false);
            assert rootView != null;
            TextView nick = (TextView)rootView.findViewById(R.id.nick);
            TextView chat = (TextView)rootView.findViewById(R.id.chat);
            EditText msg = (EditText)rootView.findViewById(R.id.message);
            ScrollView scroll = (ScrollView)rootView.findViewById(R.id.scrollView);
            msg.setOnKeyListener(this);
            nick.setText(main.Nick + " >");
            chat.setText(Html.fromHtml(main.History+"<br/>", main.htmlImageGetter, main.htmlTagHandler));
            chat.setClickable(true);
            chat.setMovementMethod(LinkMovementMethod.getInstance());
            scroll.fullScroll(View.FOCUS_DOWN);
            return rootView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(main.isBlocked)return true;
            TextView nick = (TextView)getView().findViewById(R.id.nick);
            nick.setText(main.Nick+" >");
            if(main.Vibrato)main.mVibrator.vibrate(75L);
            switch (keyCode){
                case KeyEvent.KEYCODE_ENTER :
                    EditText message = (EditText)getView().findViewById(R.id.message);
                    String msg = message.getText().toString();
                    if(!msg.equals("")){
                        /**
                         * TODO: Set Nick with "/name MyNick"
                         */
                        msg = "<div>"+main.Nick+"&nbsp;&gt;"+msg+"</div>";
                        try{
                            if(main.Power)main.mIntercom.sendMessage(msg);
                        }catch (NoSuchMethodError e){
                            Log.w("Message", "can not be send");
                        }
                        main.AppendMessage(msg);
                        message.setText("");
                        message.requestFocus();
                    }
                    return true;
            }
            return false;
        }
    }

    private Handler ChatHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            try{
                if(Power&&!isBusy){
                    String msg;
                    if( mIntercom.checkMessageBuffer() > 0&&(msg = mIntercom.getMessage())!= null ){//Get any text
                        AppendMessage(msg);
                        Toast.makeText(MainActivity.this, getString(R.string.message) + ":\n  "+msg, Toast.LENGTH_LONG).show();
                        mNotificationManager.notify(R.id.chat, new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(android.R.drawable.ic_dialog_email)
                                .setContentTitle(String.format("%s %s", getString(R.string.message), ChannelName))
                                .setContentText(msg)
                                .setContentIntent(PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, MainActivity.class)
                                        .setAction("com.nxn.intercomm.CHAT")
                                        .addCategory(Intent.CATEGORY_LAUNCHER), 0))
                                .build());
                        mViewPager.setCurrentItem(2);
                        mActionBar.setSelectedNavigationItem(2);
                    }
                }
                ChatHandler.sendEmptyMessageDelayed(0,5000L);//5 sec update
            }catch(NoSuchMethodError e){
                Log.w("Message","can not found function");
            }

        }
    };

    public class SettingsDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanseState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View Settings = inflater.inflate(R.layout.settings, null);
            assert Settings != null;
            final EditText nick = (EditText)Settings.findViewById(R.id.set_nick);
            nick.setText(Nick);
            final EditText min = (EditText)Settings.findViewById(R.id.set_min);
            min.setText(minFreq.toString());
            final EditText max = (EditText)Settings.findViewById(R.id.set_max);
            max.setText(maxFreq.toString());
            final EditText offset = (EditText)Settings.findViewById(R.id.offset);
            offset.setText(Offset.toString());
            final Spinner st = (Spinner)Settings.findViewById(R.id.set_step);
            for(int i=0;i<steps.length;i++)if(Step.equals(steps[i]))st.setSelection(i);
            final Spinner delay = (Spinner)Settings.findViewById(R.id.set_delay);
            for(int i=0;i<delays.length;i++)if(ScanDelay.equals(delays[i]))delay.setSelection(i);
            final CheckBox scan_ct = (CheckBox)Settings.findViewById(R.id.scan_ct);
            scan_ct.setChecked(ScanRxCt);
            final CheckBox vibro = (CheckBox)Settings.findViewById(R.id.vibro);
            vibro.setChecked(Vibrato);
            final EditText groups = (EditText)Settings.findViewById(R.id.set_groups);
            groups.setText(Groups);
            View.OnKeyListener keyListener = new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    if(keyCode != 66&&keyCode != 67){
                    EditText ed = (EditText)view;
                    ed.setText(Integer.toString(keyCode));
                    return true;
                    }else return false;
                }
            };
            final EditText set_sos = (EditText)Settings.findViewById(R.id.set_sos);
            set_sos.setOnKeyListener(keyListener);
            set_sos.setText(keySos.toString());
            final EditText set_block = (EditText)Settings.findViewById(R.id.set_block);
            set_block.setOnKeyListener(keyListener);
            set_block.setText(keyBlock.toString());
            final EditText set_search = (EditText)Settings.findViewById(R.id.set_search);
            set_search.setOnKeyListener(keyListener);
            set_search.setText(keySearch.toString());
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(Settings)
                    // Add action buttons
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            minFreq = Double.parseDouble(min.getText().toString());
                            maxFreq = Double.parseDouble(max.getText().toString());
                            Step = steps[st.getSelectedItemPosition()];
                            Offset = Double.parseDouble(offset.getText().toString());
                            Nick = nick.getText().toString();
                            ScanDelay = delays[delay.getSelectedItemPosition()];
                            ScanRxCt = scan_ct.isChecked();
                            Vibrato = vibro.isChecked();
                            Groups = groups.getText().toString();
                            keySos = Integer.parseInt(set_sos.getText().toString());
                            keyBlock = Integer.parseInt(set_block.getText().toString());
                            keySearch = Integer.parseInt(set_search.getText().toString());
                            Spinner groups_list = (Spinner)mViewPager.findViewById(R.id.group_list);
                            groups_list.setAdapter(getGroups());
                            editor.putString(APP_PREFERENCES_NICK, Nick);
                            editor.putString(APP_PREFERENCES_MIN_FREQ,minFreq.toString());
                            editor.putString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString());
                            editor.putString(APP_PREFERENCES_STEP,Step.toString());
                            editor.putString(APP_PREFERENCES_OFFSET,Offset.toString());
                            editor.putString(APP_PREFERENCES_DELAY,ScanDelay.toString());
                            editor.putString(APP_PREFERENCES_SCAN_CT,ScanRxCt.toString());
                            editor.putString(APP_PREFERENCES_VIBRO, Vibrato.toString());
                            editor.putString(APP_PREFERENCES_GROUPS,Groups);
                            editor.putString(APP_PREFERENCES_KEY_SOS,keySos.toString());
                            editor.putString(APP_PREFERENCES_KEY_BLOCK,keyBlock.toString());
                            editor.putString(APP_PREFERENCES_KEY_SEARCH,keySearch.toString());
                            editor.commit();
                            TextView nick = (TextView)mViewPager.findViewById(R.id.nick);
                            if(nick != null)nick.setText(Nick+" >");
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SettingsDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();

        }
    } 
    public class InputDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanseState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View About = inflater.inflate(R.layout.input, null);
            final EditText input = (EditText)About.findViewById(R.id.input);
            if(ActionInput.equals("import")||ActionInput.equals("export")){
                input.setText(FileName);
                input.setHint(R.string.file_label);
            }else if(ActionInput.equals("search")){
                input.setText(Search);
                input.setHint(R.string.search);
            }
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(About)
                    // Add action buttons
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String str = input.getText().toString();
                            if (ActionInput.equals("import")) {
                                String imported = "";

                                try {
                                    imported = importChannelListFromCSV(str);
                                    FileName = str;
                                } catch (Exception e) {
                                    Log.e("Error", e.toString() + Arrays.toString(e.getStackTrace()));
                                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(MainActivity.this, String.format(getString(R.string.ch_imported), imported.split("\\|").length, FileName), Toast.LENGTH_LONG).show();
                                if(curChannel == -1)ChannelList = imported.split("\\|"); else ChannelList = (join(ChannelList, "|") + "|" + imported).split("\\|");
                                curChannelList = getChannelList(curGroup);
                            } else if (ActionInput.equals("export")) {

                                try {
                                    exportChannelListToCSV(str);
                                    FileName = str;
                                } catch (Exception e) {
                                    Log.e("Error", e.toString() + Arrays.toString(e.getStackTrace()));
                                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else if (ActionInput.equals("search")) {
                                Search = str;
                                int f = findCh(str);
                                if (f != -1) {
                                    curChannel = f;
                                    setCh(true);
                                }
                            }

                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            InputDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();

        }
    }

    public class AboutDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanseState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View About = inflater.inflate(R.layout.about, null);
            TextView text = (TextView)About.findViewById(R.id.about_txt);
            text.setText(Html.fromHtml(getString(R.string.about_text)));
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(About)
                    // Add action buttons
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            AboutDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();

        }
    }

    public class ChannelAddDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanseState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View Settings = inflater.inflate(R.layout.channel, null);
            assert Settings != null;
            final EditText name = (EditText)Settings.findViewById(R.id.ch_name);
            final EditText rx = (EditText)Settings.findViewById(R.id.ch_rxfreq);
            final EditText tx = (EditText)Settings.findViewById(R.id.ch_txfreq);
            final Spinner rxct = (Spinner)Settings.findViewById(R.id.ch_rxctcss);
            final Spinner txct = (Spinner)Settings.findViewById(R.id.ch_txctcss);
            final Spinner sq = (Spinner)Settings.findViewById(R.id.ch_sq);
            final CheckBox scan = (CheckBox)Settings.findViewById(R.id.ch_scan);
            final Spinner group = (Spinner)Settings.findViewById(R.id.ch_group);
            if(!setSOS){
                name.setText(getString(R.string.title_chan)+" "+Integer.toString(ChannelList.length));
                rx.setText(curRxFreq.toString());
                tx.setText(curTxFreq.toString());
                rxct.setSelection(curRxCt);
                txct.setSelection(curTxCt);
                sq.setSelection(Sq-1);
                group.setAdapter(getGroups());
                group.setSelection(curGroup);
            }else{
                name.setText("SOS");
                name.setEnabled(false);
                rx.setText(sosRxFreq.toString());
                tx.setText(sosTxFreq.toString());
                rxct.setSelection(sosRxCt);
                txct.setSelection(sosTxCt);
                sq.setEnabled(false);
            }
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(Settings)
                    // Add action buttons
                    .setPositiveButton(((!setSOS)?R.string.ch_add:R.string.ch_apply), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if(!setSOS){
                                //add ch to list
                                String result =
                                        name.getText().toString().replace(",", ".").replace("|", "/") + "," +
                                                rx.getText().toString() + "," +
                                                tx.getText().toString() + "," +
                                                Integer.toString(rxct.getSelectedItemPosition()) + "," +
                                                Integer.toString(txct.getSelectedItemPosition()) + "," +
                                                Integer.toString(sq.getSelectedItemPosition()+1) + "," +
                                                Boolean.toString(scan.isChecked())+ "," +
                                                Integer.toString(group.getSelectedItemPosition());
                                if(curChannel == -1)ChannelList = new String[]{result};else
                                    ChannelList = (join(ChannelList, "|") + "|" + result).split("\\|");
                                curGroup = group.getSelectedItemPosition();
                                curChannelList = getChannelList(curGroup);
                                curChannel = curChannelList.length - 1;
                                ListView list = (ListView)mViewPager.findViewById(R.id.listView);
                                list.setAdapter(ChannelsAdapter());
                                Spinner group_list = (Spinner)mViewPager.findViewById(R.id.group_list);
                                group_list.setSelection(curGroup);
                                editor.putString(APP_PREFERENCES_CHANNELS,join(ChannelList, "|"));
                                editor.commit();
                            }else{
                                sosRxFreq = Double.parseDouble(rx.getText().toString());
                                sosTxFreq = Double.parseDouble(tx.getText().toString());
                                sosRxCt = rxct.getSelectedItemPosition();
                                sosTxCt = txct.getSelectedItemPosition();
                                editor.putString(APP_PREFERENCES_RX_SOS,sosRxFreq.toString());
                                editor.putString(APP_PREFERENCES_TX_SOS,sosTxFreq.toString());
                                editor.putString(APP_PREFERENCES_RX_CTCSS_SOS,sosRxCt.toString());
                                editor.putString(APP_PREFERENCES_TX_CTCSS_SOS,sosTxCt.toString());
                                editor.commit();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ChannelAddDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();

        }
    }
}

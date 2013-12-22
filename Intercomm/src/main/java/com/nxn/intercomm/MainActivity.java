package com.nxn.intercomm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ActionMode;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Intercom;



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
    public static final String APP_PREFERENCES_CHANNEL = "channel";
    public static final String APP_PREFERENCES_CHANNELS = "channels";
    public static final String APP_PREFERENCES_SQ = "sq";
    public static final String APP_PREFERENCES_STEP = "step";
    public static final String APP_PREFERENCES_POWER = "power";
    public static final String APP_PREFERENCES_VOLUME = "volume";
    public static final String APP_PREFERENCES_SPEAKER = "speaker";
    public static final String APP_PREFERENCES_MIN_FREQ = "min_freq";
    public static final String APP_PREFERENCES_MAX_FREQ = "max_freq";
    public static final String APP_PREFERENCES_HISTORY = "history";
    public static final String FORMAT = "###.####";

    public static final Double[] steps = {0.005,0.00625,0.01,0.01250,0.015,0.02,0.025,0.03,0.05,0.1}; //Frequency step array
    public static final Double[] tones = {0.0,67.0,71.9,74.4,77.0,79.7,82.5,85.4,88.5,91.5,94.8,97.4,100.0,103.5,107.2,110.9,114.8,118.8,123.0,127.3,131.8,136.5,141.3,146.2,151.4,156.7,162.2,167.9,173.8,179.9,186.2,192.8,203.5,210.7,218.1,225.7,233.6,241.8,250.3};
    public Integer Ver = -1;
    public String Nick = "MyNick";
    public String History = "";
    public String[] ChannelList = {};
    public Integer curChannel = 0;
    public Double curRxFreq = 446.00625;
    public Double curTxFreq = 446.00625;
    public Integer curRxCt = 0;
    public Integer curTxCt = 0;
    public Double Step = steps[1];
    public Double minFreq = 400.0;
    public Double maxFreq = 480.0;
    public Integer Sq = 1;
    public Integer Volume = 5;
    public Boolean isSpeaker = true;
    public Boolean Power = false;
    public ManualFrequency mManual;
    public Channel mChannels;
    public Chat mChat;
    public Intercom mIntercom = new Intercom();
    public NumberFormat Format;
    public Long ScanDelay = 3000L;
    public Integer TabPos = 0;

    //Old values to track changes
    private Double Old_curRxFreq;
    private Double Old_curTxFreq;
    private Integer Old_curRxCt;
    private Integer Old_curTxCt;
    private Integer Old_Sq;
    private Integer Old_Volume;

    public static Double pow(Double base, int up){
        Double result = base;
        for(int i=1;i<up;i++)result*=base;
        return result;
    }
    public String setCh(){
        String[] array = ChannelList[curChannel].split(",");
        if(array.length<5)return getString(R.string.no_data);
        curRxFreq = Double.parseDouble(array[1]);
        curTxFreq = Double.parseDouble(array[2]);
        curRxCt = Integer.parseInt(array[3]);
        curTxCt = Integer.parseInt(array[4]);
        Sq = Integer.parseInt(array[5]);
        return array[0];
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
        if(num < minFreq) num = minFreq;
        if(num > maxFreq) num = maxFreq;
        curRxFreq = num;
        curTxFreq = num;
        return Format.format(num);
    }
    public String join(String[] array, String delimiter){
        String str = array[0];
        for (int i=1;i<array.length;i++) str += delimiter+array[i];
        return str;
    }
    public int findCt(Double[] array, Double tar){
        int out= 0;
        for(int i=0;i<array.length;i++)if(array[i].equals(tar))out = i;
        return out;
    }

    public String[] convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        String get_array[] = {"","","","0","0","5","true"};
        String tar_array[] = {};
        while ((line = reader.readLine()) != null) {
            String[] array = line.split(",");
            if(array.length>10){
                if(!array[1].equals(""))get_array[0]=array[1];else get_array[0]=Integer.toString(tar_array.length);
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
                if(array[5].equals("Tone") || array[5].equals("Cross")){
                    get_array[3]=get_array[4]=Integer.toString(findCt(tones,Double.parseDouble(array[6])));
                }else if(array[5].equals("TSQL")){
                    get_array[3]="0";
                    get_array[4]=Integer.toString(findCt(tones,Double.parseDouble(array[7])));
                }else if(array[5].equals("TSQL-R")){
                    get_array[3]=Integer.toString(findCt(tones,Double.parseDouble(array[6])));
                    get_array[4]="0";
                }
                if(!get_array[1].equals(""))tar_array[tar_array.length]=join(get_array,",");
            }
        }
        reader.close();
        return tar_array;
    }

    public String[] getChannelListFromCSV (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String[] ret = convertStreamToString(fin);
        fin.close();
        return ret;
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
        curChannel = Integer.parseInt(mSettings.getString(APP_PREFERENCES_CHANNEL,curChannel.toString()));
        curRxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString()));
        curTxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString()));
        curRxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS, curRxCt.toString()));
        curTxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TX_CTCSS, curTxCt.toString()));
        Step = Double.parseDouble(mSettings.getString(APP_PREFERENCES_STEP, Step.toString()));
        Volume = Integer.parseInt(mSettings.getString(APP_PREFERENCES_VOLUME,Volume.toString()));
        isSpeaker = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_SPEAKER,isSpeaker.toString()));
        Sq = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SQ,Sq.toString()));
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
        editor.putString(APP_PREFERENCES_CHANNELS,join(ChannelList,"|"));
        editor.putString(APP_PREFERENCES_CHANNEL,curChannel.toString());
        editor.putString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString());
        editor.putString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString());
        editor.putString(APP_PREFERENCES_RX_CTCSS,curRxCt.toString());
        editor.putString(APP_PREFERENCES_TX_CTCSS,curTxCt.toString());
        editor.putString(APP_PREFERENCES_STEP,Step.toString());
        editor.putString(APP_PREFERENCES_VOLUME,Volume.toString());
        editor.putString(APP_PREFERENCES_SPEAKER,isSpeaker.toString());
        editor.putString(APP_PREFERENCES_SQ,Sq.toString());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        /**
         * Settings get
         */
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = mSettings.edit();
        TabPos = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TAB,"0"));
        Nick = mSettings.getString(APP_PREFERENCES_NICK, Nick);
        History = mSettings.getString(APP_PREFERENCES_HISTORY, "<h1>"+getString(R.string.title_chat)+"</h1>");
        Power = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_POWER,Power.toString()));
        minFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MIN_FREQ, minFreq.toString()));
        maxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString()));
        ChannelList = mSettings.getString(APP_PREFERENCES_CHANNELS, getString(R.string.channels_std)).split("\\|");
        curChannel = Integer.parseInt(mSettings.getString(APP_PREFERENCES_CHANNEL,curChannel.toString()));
        curRxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_FREQ,curRxFreq.toString()));
        curTxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_TX_FREQ,curTxFreq.toString()));
        curRxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS, curRxCt.toString()));
        curTxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TX_CTCSS, curTxCt.toString()));
        Step = Double.parseDouble(mSettings.getString(APP_PREFERENCES_STEP, Step.toString()));
        Volume = Integer.parseInt(mSettings.getString(APP_PREFERENCES_VOLUME,Volume.toString()));
        isSpeaker = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_SPEAKER,isSpeaker.toString()));
        Sq = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SQ,Sq.toString()));

        //Set old Values to send it with timer to module
        Old_curRxFreq = curRxFreq;
        Old_curTxFreq = curTxFreq;
        Old_curRxCt = curRxCt;
        Old_curTxCt = curTxCt;
        Old_Sq = Sq;
        Old_Volume = Volume;

        try {
            mIntercom.openCharDev();
        }catch (NoSuchMethodError e){
            Toast.makeText(this, R.string.non_runbo, Toast.LENGTH_SHORT).show();
            mIntercom = new uartIntercom();
        }


        //Create format
        Format = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)Format).applyPattern(FORMAT);
        Format.setMinimumFractionDigits(FORMAT.length() - FORMAT.indexOf(".")-1);
        Format.setMinimumIntegerDigits(FORMAT.indexOf("."));

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPagerAdapter = new SampleAdapter(this, getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mManual = ManualFrequency.newInstance(this);
        mChannels = Channel.newInstance(this);
        mChat = Chat.newInstance(this);

        mViewPager.setAdapter(mPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                TabPos = position;
                actionBar.setSelectedNavigationItem(position);
                if(position==1)setCh();
            }
        });
        /**
         * Adding Tabs
         */
        for(int i =0;i < mPagerAdapter.getCount();i++)
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mPagerAdapter.getPageTitle(i).toUpperCase())
                            .setTabListener(this));
        actionBar.getTabAt(TabPos).select();
        mViewPager.setCurrentItem(TabPos);

        if(Power){
            Toast.makeText(this, R.string.power_enabling, Toast.LENGTH_SHORT).show();
            mIntercom.intercomPowerOn();
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Ver = mIntercom.getIntercomVersion();
            setRxFreq();
            setTxFreq();
            mIntercom.setCtcss(curRxCt);
            mIntercom.setTxCtcss(curTxCt);
            mIntercom.setSq(Sq);
            mIntercom.resumeIntercomSetting();
            mIntercom.setVolume(Volume);
            if(isSpeaker)mIntercom.intercomSpeakerMode();else mIntercom.intercomHeadsetMode();
            mIntercom.resumeIntercomSetting();
            Toast.makeText(this, R.string.power_enabled + "Ver: "+Ver.toString(), Toast.LENGTH_SHORT).show();

        }
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
                        Old_curRxFreq = curRxFreq;
                        up = true;
                    }
                    if(!curTxFreq.equals(Old_curTxFreq)){
                        setTxFreq();
                        Old_curTxFreq = curTxFreq;
                        up = true;
                    }
                    if(!curRxCt.equals(Old_curRxCt)){
                        mIntercom.setCtcss(curRxCt);
                        Old_curRxCt = curRxCt;
                        up = true;
                    }
                    if(!curTxCt.equals(Old_curTxCt)){
                        mIntercom.setTxCtcss(curTxCt);
                        Old_curTxCt = curTxCt;
                        up = true;
                    }
                    if(!Sq.equals(Old_Sq)){
                        mIntercom.setSq(Sq);
                        Old_Sq = Sq;
                        up = true;
                    }
                    if(!Volume.equals(Old_Volume)){
                        mIntercom.setVolume(Volume);
                        Old_Volume = Volume;
                        up = true;
                    }
                    if(up){
                        mIntercom.resumeIntercomSetting();//Commit setting
                    }
                }
            }
        }, 0, 3000);
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
        switch (item.getItemId()){
            case R.id.clear_history_action:
                History = "<h1>"+getString(R.string.title_chat)+"</h1>";
                return true;
            case R.id.clear_channels:
                ChannelList = getString(R.string.channels_std).split("\\|");
                curChannel = 0;
                setCh();
                return true;
            case android.R.id.home:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.ch_add:
                Log.w("ADD","Channel");
                DialogFragment editor = new ChannelAddDialog();
                editor.show(getSupportFragmentManager(),"channel_edit");
                return true;
            case R.id.about:
                DialogFragment about = new AboutDialog();
                about.show(getSupportFragmentManager(),"about");
                return true;
            case R.id.action_settings:
                DialogFragment settings = new SettingsDialog();
                settings.show(getSupportFragmentManager(),"settings");
                return true;
            case R.id.action_quit:
                mIntercom.closeCharDev();
                finish();
                return true;
            case R.id.power:
                if(item.isChecked()){
                    mIntercom.intercomPowerOff();
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        //
                    }
                    mIntercom.closeCharDev();
                    Toast.makeText(this, R.string.power_disabled, Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_lock_power_off);
                    item.setChecked(false);
                }else{
                    mIntercom.openCharDev();
                    mIntercom.intercomPowerOn();
                    try {
                        Thread.sleep(400L); //Boot-up wait
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try{
                        Ver = mIntercom.getIntercomVersion();
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","getIntercomVersion()");
                    }
                    setTxFreq();
                    setRxFreq();
                    //mIntercom.setRadioFrequency(getFreq());
                    mIntercom.setSq(Sq);
                    mIntercom.setCtcss(curRxCt);
                    mIntercom.setTxCtcss(curTxCt);
                    mIntercom.setVolume(Volume);
                    if(isSpeaker)mIntercom.intercomSpeakerMode();else mIntercom.intercomHeadsetMode();
                    mIntercom.resumeIntercomSetting();
                    Toast.makeText(this, R.string.power_enabled, Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_lock_idle_charging);
                    item.setChecked(true);
                }
                Power=item.isChecked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        TabPos = tab.getPosition();
        mViewPager.setCurrentItem(tab.getPosition());
        if(tab.getPosition()==1)setCh();

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
            return 3;
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
        private Boolean Scan = false;
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
            EditText e = (EditText)getView().findViewById(R.id.freq);
            ImageButton snd = (ImageButton)getView().findViewById(R.id.sound_src);
            if(!isLongTouch)switch (view.getId()){
                case R.id.freq:
                    Scan = false;
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
            isLongTouch = true;
            switch (view.getId()){
                case R.id.freq_next:
                    Message msg1 = new Message();
                    msg1.arg1 = 1;
                    Scan = true;
                    mHandler.sendMessageDelayed(msg1,main.ScanDelay);
                    break;
                case R.id.freq_prew:
                    Message msg2 = new Message();
                    msg2.arg1 = 2;
                    Scan = true;
                    mHandler.sendMessageDelayed(msg2,main.ScanDelay);
                    break;
            }
            return false;
        }
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if(Scan){
                    EditText freq = (EditText)getView().findViewById(R.id.freq);
                    switch (message.arg1){
                        case 1:
                            if(main.curRxFreq+main.Step > main.maxFreq) main.curRxFreq = main.minFreq-main.Step;
                            freq.setText(main.setFreq(main.curRxFreq,main.Step));
                            Message msg1 = new Message();
                            msg1.arg1 = 1;
                            mHandler.sendMessageDelayed(msg1,main.ScanDelay);
                            break;
                        case 2:
                            if(main.curRxFreq-main.Step < main.minFreq) main.curRxFreq = main.maxFreq+main.Step;
                            freq.setText(main.setFreq(main.curRxFreq,-main.Step));
                            Message msg2 = new Message();
                            msg2.arg1 = 2;
                            mHandler.sendMessageDelayed(msg2,main.ScanDelay);
                            break;
                    }
                    if(main.Power){
                        main.Old_curRxFreq = main.Old_curTxFreq = main.curTxFreq = main.curRxFreq;
                        main.setRxFreq();
                        main.setTxFreq();
                    }
                }
            }
        };


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            main.Volume = progress - 1;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
                freq.setText(main.setFreq(0.0,0.0));
                sq.setSelection(main.Sq - 1);
                rxct.setSelection(main.curRxCt);
                txct.setSelection(main.curTxCt);
                vol.setProgress(main.Volume + 1);
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

        }

        @Override
        public void afterTextChanged(Editable string) {
            Double freq;
            if(string.length()>0){

                    freq = Double.parseDouble(string.toString());


                //If valid number
                if(freq != 0.0){
                    //If bigger than maximum
                    if(freq > main.maxFreq && string.length() >= 3){
                        Toast.makeText(getView().getContext(), getString(R.string.set_max)+" "+main.maxFreq.toString(), Toast.LENGTH_SHORT).show();
                        if(string.length()>2)string.delete(2,string.length());
                        return;
                    }
                    //If lower than minimum
                    if(freq < main.minFreq && string.length() >=3){
                        Toast.makeText(getView().getContext(), getString(R.string.set_min)+" "+main.minFreq.toString(), Toast.LENGTH_SHORT).show();
                        if(string.length()>2)string.delete(2,string.length());
                        return;
                    }
                    //Add point after 3 valid sym entered
                    if(string.length() == 3)string.append(".");
                    if(string.length()>= 4)main.curRxFreq = freq;
                    main.curTxFreq = main.curRxFreq;
                }
            }
        }
    }
    public static class Channel extends Fragment implements
            OnClickListener,
            View.OnLongClickListener,
            TextWatcher,
            SeekBar.OnSeekBarChangeListener
    {
        private MainActivity main;
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
            switch (view.getId()){
                case R.id.ch_search:
                    EditText search = (EditText)getView().findViewById(R.id.searchText);
                    Log.w("Search",search.getText().toString());
                    int f = findCh(search.getText().toString());
                    if(f != -1){
                        main.curChannel=f;
                        ch_name = main.setCh();
                        getInfo(true);
                    }
                    break;
                case R.id.ch_next:
                    if(main.curChannel<main.ChannelList.length-1)main.curChannel++;
                    ch_name = main.setCh();
                    getInfo(true);
                    break;
                case R.id.ch_prew:
                    if(main.curChannel>0)main.curChannel--;
                    ch_name = main.setCh();
                    getInfo(true);
                    break;
                case R.id.ch_info:
                    DialogFragment editor = new ChannelEditDialog();
                    editor.show(super.getFragmentManager(),"channel_edit");
                    break;
                case R.id.ch_src:
                    ImageButton snd = (ImageButton)getView().findViewById(R.id.ch_src);
                    if(main.isSpeaker){
                        snd.setImageResource(android.R.drawable.ic_lock_silent_mode);
                        main.mIntercom.intercomHeadsetMode();
                        main.isSpeaker=false;
                    }else{
                        snd.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                        main.mIntercom.intercomSpeakerMode();
                        main.isSpeaker=true;//stat_sys_headset
                    }
                    break;

            }

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
            ch_name = main.setCh();
            rootView.findViewById(R.id.ch_search).setOnClickListener(this);
            rootView.findViewById(R.id.ch_next).setOnClickListener(this);
            rootView.findViewById(R.id.ch_prew).setOnClickListener(this);
            rootView.findViewById(R.id.ch_vol).setOnClickListener(this);
            TextView ch_info = (TextView)rootView.findViewById(R.id.ch_info);
            ImageButton snd = (ImageButton)rootView.findViewById(R.id.ch_src);
            snd.setOnClickListener(this);
            SeekBar vol = (SeekBar)rootView.findViewById(R.id.ch_vol);

            vol.setOnSeekBarChangeListener(this);
            vol.setProgress(main.Volume);
            ch_info.setText(Html.fromHtml(getInfo(false)));
            ch_info.setOnClickListener(this);
            if(main.isSpeaker){
                snd.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
            }else{
                snd.setImageResource(android.R.drawable.ic_lock_silent_mode);//stat_sys_headset
            }
            /**
             * TODO: Channel List and manipulation
             */
            return rootView;
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            main.Volume = progress - 1;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
        public String getInfo(Boolean set){
            String str = "<h1>"+ch_name+"</h1>"+
            "<p>"+getString(R.string.rxfreq_label)+": "+main.Format.format(main.curRxFreq)+"<br/>"+
            getString(R.string.txfreq_label)+": "+main.Format.format(main.curTxFreq)+"<br/>"+
                    getString(R.string.rxctcss_label)+": "+main.curRxCt+"  "+
                    getString(R.string.txctcss_label)+": "+main.curTxCt+"</p>";
            if(set){
                TextView info = (TextView)getView().findViewById(R.id.ch_info);
                info.setText(Html.fromHtml(str));
            }
            return str;
        }
        public int findCh(String str){
            int result=-1;
            for(int i=main.curChannel+1;i<main.ChannelList.length;i++){//find in upper part list
                if(main.ChannelList[i].toUpperCase().contains(str.toUpperCase())){
                    result = i;
                    break;
                }else{
                    for(String f:str.split(" ")){//substring find
                        if(main.ChannelList[i].toUpperCase().contains(f.toUpperCase())){
                            result=i;
                            break;
                        }
                    }
                }
            }
            if(result == -1)for(int i=0;i<main.curChannel-1;i++){//resume from lower part if NO result
                if(main.ChannelList[i].toUpperCase().contains(str.toUpperCase())){
                    result = i;
                    break;
                }else{
                    for(String f:str.split(" ")){//Substring find
                        if(main.ChannelList[i].toUpperCase().contains(f.toUpperCase())){
                            result=i;
                            break;
                        }
                    }
                }
            }
            return result;
        }

        public class ChannelEditDialog extends DialogFragment{
            @Override
            public Dialog onCreateDialog(Bundle savedInstanseState){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View Settings = inflater.inflate(R.layout.channel, null);
                assert Settings != null;
                final EditText name = (EditText)Settings.findViewById(R.id.ch_name);
                name.setText(ch_name);
                final EditText rx = (EditText)Settings.findViewById(R.id.ch_rxfreq);
                rx.setText(main.curRxFreq.toString());
                final EditText tx = (EditText)Settings.findViewById(R.id.ch_txfreq);
                tx.setText(main.curTxFreq.toString());
                final Spinner rxct = (Spinner)Settings.findViewById(R.id.ch_rxctcss);
                rxct.setSelection(main.curRxCt);
                final Spinner txct = (Spinner)Settings.findViewById(R.id.ch_txctcss);
                txct.setSelection(main.curTxCt);
                final Spinner sq = (Spinner)Settings.findViewById(R.id.ch_sq);
                sq.setSelection(main.Sq-1);
                final CheckBox scan = (CheckBox)Settings.findViewById(R.id.ch_scan);
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
                                        Boolean.toString(scan.isChecked());
                                main.ChannelList[main.curChannel]=result;
                                ch_name = main.setCh();
                                getInfo(true);
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
        private Spanned getHtml(){
            return Html.fromHtml(main.History+"<br/>");
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
            rootView = inflater.inflate(R.layout.chat, container, false);
            assert rootView != null;
            TextView nick = (TextView)rootView.findViewById(R.id.nick);
            TextView chat = (TextView)rootView.findViewById(R.id.chat);
            EditText msg = (EditText)rootView.findViewById(R.id.message);
            ScrollView scroll = (ScrollView)rootView.findViewById(R.id.scrollView);
            main = (MainActivity)super.getActivity();
            msg.setOnKeyListener(this);
            nick.setText(main.Nick+" >");
            chat.setText(getHtml());
            scroll.fullScroll(View.FOCUS_DOWN);
            return rootView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            TextView nick = (TextView)getView().findViewById(R.id.nick);
            nick.setText(main.Nick+" >");
            switch (keyCode){
                case KeyEvent.KEYCODE_ENTER :
                    EditText message = (EditText)getView().findViewById(R.id.message);
                    String msg = message.getText().toString();
                    if(!msg.equals("")){
                        /**
                         * TODO: Set Nick with "/name MyNick"
                         */
                        TextView chat = (TextView)getView().findViewById(R.id.chat);
                        ScrollView scroll = (ScrollView)getView().findViewById(R.id.scrollView);
                        msg = "<div>"+main.Nick+"&nbsp;&gt;"+msg+"</div>";
                        try{
                            if(main.Power)main.mIntercom.sendMessage(msg);
                        }catch (NoSuchMethodError e){
                            Log.w("Message","can not be send");
                            msg += "<i>"+getString(R.string.not_send)+"</i>";
                        }
                        main.History += msg;
                        chat.setText(getHtml());
                        scroll.fullScroll(View.FOCUS_DOWN);
                        message.setText("");
                        message.requestFocus();
                    }
                    return true;
                    //break;
            }
            return false;
        }
    }
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
            final Spinner st = (Spinner)Settings.findViewById(R.id.set_step);
            for(int i=0;i<steps.length;i++)if(Step.equals(steps[i]))st.setSelection(i);
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
                            Nick = nick.getText().toString();
                            editor.putString(APP_PREFERENCES_NICK,Nick);
                            editor.putString(APP_PREFERENCES_MIN_FREQ,minFreq.toString());
                            editor.putString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString());
                            editor.putString(APP_PREFERENCES_STEP,Step.toString());
                            editor.commit();
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
                            //
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
            name.setText(getString(R.string.title_chan)+" "+Integer.toString(ChannelList.length));
            final EditText rx = (EditText)Settings.findViewById(R.id.ch_rxfreq);
            rx.setText(curRxFreq.toString());
            final EditText tx = (EditText)Settings.findViewById(R.id.ch_txfreq);
            tx.setText(curTxFreq.toString());
            final Spinner rxct = (Spinner)Settings.findViewById(R.id.ch_rxctcss);
            rxct.setSelection(curRxCt);
            final Spinner txct = (Spinner)Settings.findViewById(R.id.ch_txctcss);
            txct.setSelection(curTxCt);
            final Spinner sq = (Spinner)Settings.findViewById(R.id.ch_sq);
            sq.setSelection(Sq-1);
            final CheckBox scan = (CheckBox)Settings.findViewById(R.id.ch_scan);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(Settings)
                    // Add action buttons
                    .setPositiveButton(R.string.ch_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //add ch to list
                            String result =
                                    name.getText().toString().replace(",", ".").replace("|", "/") + "," +
                                            rx.getText().toString() + "," +
                                            tx.getText().toString() + "," +
                                            Integer.toString(rxct.getSelectedItemPosition()) + "," +
                                            Integer.toString(txct.getSelectedItemPosition()) + "," +
                                            Integer.toString(sq.getSelectedItemPosition()+1) + "," +
                                            Boolean.toString(scan.isChecked());
                            ChannelList = (join(ChannelList, "|") + "|" + result).split("\\|");
                            curChannel = ChannelList.length - 1;
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

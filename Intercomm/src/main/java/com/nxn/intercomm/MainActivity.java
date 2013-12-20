package com.nxn.intercomm;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
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
    public static final String APP_PREFERENCES_NICK = "nick";
    public static final String APP_PREFERENCES_TX_FREQ = "tx_freq";
    public static final String APP_PREFERENCES_RX_FREQ = "rx_freq";
    public static final String APP_PREFERENCES_TX_CTCSS = "tx_ctcss";
    public static final String APP_PREFERENCES_RX_CTCSS = "rx_ctcss";
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
    public Integer Ver = -1;
    public String Nick = "MyNick";
    public String History = "";
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

    @Override
    public void onPostResume(){
        Nick = mSettings.getString(APP_PREFERENCES_NICK, Nick);
        History = mSettings.getString(APP_PREFERENCES_HISTORY, "<h1>"+getString(R.string.title_chat)+"</h1>");
        Power = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_POWER,Power.toString()));
        minFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MIN_FREQ, minFreq.toString()));
        maxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString()));
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
        editor.putString(APP_PREFERENCES_NICK,Nick);
        editor.putString(APP_PREFERENCES_HISTORY,History);
        editor.putString(APP_PREFERENCES_POWER,Power.toString());
        editor.putString(APP_PREFERENCES_MIN_FREQ,minFreq.toString());
        editor.putString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString());
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
        Nick = mSettings.getString(APP_PREFERENCES_NICK, Nick);
        History = mSettings.getString(APP_PREFERENCES_HISTORY, "<h1>"+getString(R.string.title_chat)+"</h1>");
        Power = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_POWER,Power.toString()));
        minFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MIN_FREQ, minFreq.toString()));
        maxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString()));
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
                actionBar.setSelectedNavigationItem(position);
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
                if(Power)item.setIcon(android.R.drawable.ic_lock_idle_charging);
                item.setChecked(Power);
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
            case android.R.id.home:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.action_settings:
                DialogFragment newFragment = new SettingsDialog();
                newFragment.show(getSupportFragmentManager(),"settings");
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
                editor.putString(APP_PREFERENCES_POWER,Power.toString());
                editor.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

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
            switch (view.getId()){
                case R.id.freq_next:
                    e.setText(main.setFreq(0.0,main.Step));
                    break;
                case R.id.freq_prew:
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
            }
        }
        @Override
        public boolean onLongClick(View view) {
            /**
             * TODO Autoincrease Freq (scan mode) 3000 delay
             */
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
    public static class Channel extends Fragment implements OnClickListener  {
        private MainActivity main;
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
                case R.id.add:
                    DialogFragment newFragment = new ChannelDialog();
                    newFragment.show(super.getFragmentManager(),"channel");
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
            rootView.findViewById(R.id.add).setOnClickListener(this);
            rootView.findViewById(R.id.search).setOnClickListener(this);
            /**
             * TODO: Channel List and manipulation
             */
            return rootView;
        }
        public class ChannelDialog extends DialogFragment{
            @Override
            public Dialog onCreateDialog(Bundle savedInstanseState){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View Settings = inflater.inflate(R.layout.channel, null);
                assert Settings != null;
                final EditText rx = (EditText)Settings.findViewById(R.id.rxfreq);
                rx.setText(main.curRxFreq.toString());
                final EditText tx = (EditText)Settings.findViewById(R.id.txfreq);
                tx.setText(main.curTxFreq.toString());
                final EditText rxct = (EditText)Settings.findViewById(R.id.rxctcss);
                rxct.setText(main.curRxCt.toString());
                final EditText txct = (EditText)Settings.findViewById(R.id.txctcss);
                txct.setText(main.curTxCt.toString());
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(Settings)
                        // Add action buttons
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //add ch to list
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ChannelDialog.this.getDialog().cancel();
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

}

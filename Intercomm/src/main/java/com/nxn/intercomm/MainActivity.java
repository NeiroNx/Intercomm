package com.nxn.intercomm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.EditText;
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
    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;
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
    public static final String APP_PREFERENCES_MIN_FREQ = "min_freq";
    public static final String APP_PREFERENCES_MAX_FREQ = "max_freq";
    public static final String APP_PREFERENCES_HISTORY = "history";
    public static final String FORMAT = "###.####";

    public static final Double[] steps = {0.00625,0.01250,0.025}; //Frequency step array
    public Integer Ver = -1;
    public String Nick = "MyNick";
    public String History = "";
    public Double curFreq = 446.00625;
    public Double curTxFreq = 0.0;  //Set 0.0 to use curFreq
    public Integer curCt = 0;
    public Integer curTxCt = 0;
    public Double Step = steps[1];
    public Double minFreq = 400.0;
    public Double maxFreq = 480.0;
    public Integer Sq = 1;
    public Integer Volume = 5;
    public Boolean Power = false;
    public ManualFrequency mManual;
    public Channel mChannels;
    public Chat mChat;
    private Intercom mIntercom = new Intercom();
    private Boolean InitOK = false;

    Integer txFreq(){
        Double tx = (curTxFreq.equals(0.0))?curFreq:curTxFreq;
        Double f = Double.parseDouble(Float.toString(tx.floatValue() * 10000F));
        return f.intValue();
    }
    Integer rxFreq(){
        Double f = Double.parseDouble(Float.toString(curFreq.floatValue() * 10000F));
        return f.intValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        curFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_FREQ,curFreq.toString()));
        curTxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_FREQ,curTxFreq.toString()));
        curCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS, curCt.toString()));
        curTxCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS, curTxCt.toString()));
        Step = Double.parseDouble(mSettings.getString(APP_PREFERENCES_STEP, Step.toString()));
        Volume = Integer.parseInt(mSettings.getString(APP_PREFERENCES_VOLUME,Volume.toString()));
        Sq = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SQ,Sq.toString()));



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
        /**
         * Manual Frequency set settings
         */
        mManual = new ManualFrequency(mSettings);
        mManual.setMaxFreq(maxFreq);
        mManual.setMinFreq(minFreq);
        mManual.setCurFreq(curFreq);
        mManual.setStep(Step);
        mManual.setSq(Sq);
        mManual.setCt(curCt);
        mManual.setVolume(Volume);

        mChannels = new Channel(mSettings);
        mChat = new Chat(mSettings);
        mChat.setNick(Nick);
        mChat.setHistory(History);
        mViewPager.setAdapter(mPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                switch (position){
                    case 2:
                        //
                        break;
                }
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

        Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            /**
             * Apply Settings and INIT
             */
            @Override
            public void run() {
                if(!curFreq.equals(mManual.getCurFreq())){
                    curFreq = mManual.getCurFreq();
                    editor.putString(APP_PREFERENCES_TX_FREQ,curFreq.toString());
                    editor.commit();
                    if(Power)
                        try{
                            //mIntercom.setRadioFrequency(getFreq());
                            mIntercom.setTXFrequency(txFreq());
                            mIntercom.setRXFrequency(rxFreq());
                            mIntercom.resumeIntercomSetting();
                        }catch (NoSuchMethodError e){
                            Log.w("Intercom","setRadioFrequency(getFreq())");
                        }
                }
                if(!Sq.equals(mManual.getSq())){
                    Sq = mManual.getSq();
                    editor.putString(APP_PREFERENCES_SQ, Sq.toString());
                    editor.commit();
                    if(Power)
                        try{
                            mIntercom.setSq(Sq);
                            mIntercom.resumeIntercomSetting();
                        }catch (NoSuchMethodError e){
                            Log.w("Intercom","setSq(Sq)");
                        }
                }
                if(!curCt.equals(mManual.getCt())){
                    curCt = mManual.getCt();
                    curTxCt = mManual.getCt();
                    editor.putString(APP_PREFERENCES_RX_CTCSS, curCt.toString());
                    editor.commit();
                    if(Power)
                        try{
                            mIntercom.setCtcss(curCt);
                            mIntercom.setTxCtcss(curTxCt);
                            mIntercom.resumeIntercomSetting();
                        }catch (NoSuchMethodError e){
                            Log.w("Intercom","setCtcss(curCt)");
                        }
                }
                if(!Volume.equals(mManual.getVolume())){
                    Volume = mManual.getVolume();
                    editor.putString(APP_PREFERENCES_VOLUME,Volume.toString());
                    editor.commit();
                    if(Power)
                        try{
                            mIntercom.setVolume(Volume);
                            mIntercom.resumeIntercomSetting();
                        }catch (NoSuchMethodError e){
                            Log.w("Intercom","setVolume(Volume)");
                        }
                }
                if(!History.equals(mChat.getHistory())){
                    History = mChat.getHistory();
                    editor.putString(APP_PREFERENCES_HISTORY,History);
                    editor.commit();
                }
                mChat.setNick(Nick);
                mChat.setHistory(History);
                mChat.setPower(Power);
                mManual.setVolume(Volume);
                mManual.setMaxFreq(maxFreq);
                mManual.setMinFreq(minFreq);
                mManual.setSq(Sq);
                mManual.setCt(curCt);
                mManual.setStep(Step);
                mManual.setCurFreq(curFreq);
                if(Power && !InitOK){
                    curCt = mManual.getCt();
                    curFreq = mManual.getCurFreq();
                    Sq = mManual.getSq();
                    Volume = mManual.getVolume();
                    try{
                        mIntercom.openCharDev();
                        mIntercom.intercomPowerOn();
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","intercomPowerOn() and init");
                    }
                    try{
                        Thread.sleep(200L);
                        Ver = mIntercom.getIntercomVersion();
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","getIntercomVersion()");
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    try{
                        mIntercom.setTXFrequency(txFreq());
                        mIntercom.setRXFrequency(rxFreq());
                        //mIntercom.setRadioFrequency(getFreq());
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","setRadioFrequency(getFreq())");
                    }
                    try{
                        mIntercom.setSq(Sq);
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","setSq(Sq)");
                    }
                    try{
                        mIntercom.setCtcss(curCt);
                        mIntercom.setTxCtcss(curTxCt);
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","setCtcss(curCt)");
                    }
                    try{
                        mIntercom.setVolume(Volume);
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","setVolume(Volume)");
                    }
                    try{
                        mIntercom.intercomSpeakerMode();
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","intercomSpeakerMode()");
                    }
                    try{
                        mIntercom.resumeIntercomSetting();
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","resumeIntercomSetting()");
                    }
                    InitOK = true;

                }
            }
        }, 0, 1000);
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
                mChat.setHistory(History);
                return true;
            case android.R.id.home:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.action_settings:
                DialogFragment newFragment = new SettingsDialog();
                newFragment.show(getSupportFragmentManager(),"settings");
                return true;
            case R.id.action_quit:
                finish();
                return true;
            case R.id.power:
                if(item.isChecked()){
                    try{
                        mIntercom.intercomPowerOff();
                        Thread.sleep(200L);
                        mIntercom.closeCharDev();
                        Toast.makeText(this, R.string.power_disabled, Toast.LENGTH_SHORT).show();
                        item.setIcon(android.R.drawable.ic_lock_power_off);
                        item.setChecked(false);
                    }catch (NoSuchMethodError e){
                        Log.w("Intercom","intercomPowerOff() failed");
                    } catch (InterruptedException e) {
                        //
                    }
                }else{
                    try{
                        mIntercom.openCharDev();
                        mIntercom.intercomPowerOn();
                        Thread.sleep(200L); //Boot-up wait
                        try{
                            Ver = mIntercom.getIntercomVersion();
                        }catch (NoSuchMethodError e){
                            Log.w("Intercom","getIntercomVersion()");
                        }
                        mIntercom.setTXFrequency(txFreq());
                        mIntercom.setRXFrequency(rxFreq());
                        //mIntercom.setRadioFrequency(getFreq());
                        mIntercom.setSq(Sq);
                        mIntercom.setCtcss(curCt);
                        mIntercom.setTxCtcss(curCt);
                        mIntercom.setVolume(Volume);
                        mIntercom.intercomSpeakerMode();
                        mIntercom.resumeIntercomSetting();
                        Toast.makeText(this, R.string.power_enabled, Toast.LENGTH_SHORT).show();
                        item.setIcon(android.R.drawable.ic_lock_idle_charging);
                        item.setChecked(true);
                        InitOK = true;
                    }catch (Error e){
                        Log.w("Intercom","PowerOn init failed");
                    } catch (InterruptedException e) {
                        //
                    }
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
        Context ctxt = null;
        public SampleAdapter(Context ctxt, FragmentManager mgr) {
            super(mgr);
            this.ctxt = ctxt;
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
            TextWatcher,
            AdapterView.OnItemSelectedListener,
            SeekBar.OnSeekBarChangeListener
    {
        private Double curFreq;
        private Double minFreq;
        private Double maxFreq;
        private Double Step;
        private Integer Sq;
        private Integer Ct;
        private Integer Volume;
        public ManualFrequency(SharedPreferences mSettings){
            minFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MIN_FREQ, "400.0"));
            maxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MAX_FREQ,"480.0"));
            curFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_RX_FREQ,"446.0062"));
            Ct = Integer.parseInt(mSettings.getString(APP_PREFERENCES_RX_CTCSS, "0"));
            Step = Double.parseDouble(mSettings.getString(APP_PREFERENCES_STEP, "0.0125"));
            Volume = Integer.parseInt(mSettings.getString(APP_PREFERENCES_VOLUME,"5"));
            Sq = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SQ,"5"));
        }
        private Double getMinFreq(){
            return minFreq;
        }
        private Double getMaxFreq(){
            return maxFreq;
        }
        private Double getCurFreq(){
            return curFreq;
        }
        private void setMinFreq(Double f){
            minFreq = f;
        }
        private void setMaxFreq(Double f){
            maxFreq = f;
        }
        private void setCurFreq(Double f){
            curFreq = f;
        }
        private void setStep(Double s){
            Step = s;
        }
        private Integer getCt(){
            return Ct;
        }
        private void setCt(Integer i){
            Ct = i;
        }
        private Integer getSq(){
            return Sq;
        }
        private void setSq(Integer i){
            Sq = i;
        }
        private Integer getVolume(){
            return Volume;
        }
        private void setVolume(Integer i){
            Volume = i;
        }
        String setFreq(Double freq, Double delta){
            NumberFormat Format = NumberFormat.getInstance(Locale.ENGLISH);
            ((DecimalFormat)Format).applyPattern(FORMAT);
            Format.setMinimumFractionDigits(4);
            Format.setMinimumIntegerDigits(3);
            Double num = curFreq;
            if(freq != 0.0)
                num = freq;
            num += delta;
            if(num < minFreq) num = minFreq;
            if(num > maxFreq) num = maxFreq;
            curFreq = num;
            return Format.format(num);
        }
        Integer getFreq(){
            Double f = Double.parseDouble(Float.toString(curFreq.floatValue() * 10000F));
            return f.intValue();
        }

        @Override
        public void onClick(View view){
            EditText e = (EditText)getView().findViewById(R.id.freq);
            switch (view.getId()){
                case R.id.freq_next:
                    e.setText(setFreq(0.0,Step));
                    break;
                case R.id.freq_prew:
                    e.setText(setFreq(0.0, -Step));
                    break;
                case R.id.sound_src:
                    /**
                     * TODO: Create sound source select
                     */
                    break;
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Volume = progress;
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
                case R.id.ctcss:
                    Ct = position;
                    break;
                case R.id.sq:
                    Sq = position+1;
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
            View rootView = inflater.inflate(R.layout.manual_freq, container, false);
            assert rootView != null;
            rootView.findViewById(R.id.freq_next).setOnClickListener(this);
            rootView.findViewById(R.id.freq_prew).setOnClickListener(this);
            rootView.findViewById(R.id.sound_src).setOnClickListener(this);
            EditText freq = (EditText)rootView.findViewById(R.id.freq);
            freq.addTextChangedListener(this);
            Spinner sq = (Spinner)rootView.findViewById(R.id.sq);
            Spinner ct = (Spinner)rootView.findViewById(R.id.ctcss);
            SeekBar vol = (SeekBar)rootView.findViewById(R.id.volume);
            vol.setOnSeekBarChangeListener(this);
            sq.setOnItemSelectedListener(this);
            ct.setOnItemSelectedListener(this);
            /**
             * TODO: Check weather of views before set him
             */
            //if(savedInstanceState.isEmpty())
            try{
                freq.setText(setFreq(0.0,0.0));
                sq.setSelection(Sq - 1);
                ct.setSelection(Ct);
                vol.setProgress(Volume);
            }catch (Error e){
                    Log.i("onCreateView","Failed to set state");
            }

            return rootView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Log.i("BeforeTextChanged","s = "+s.toString()+" count="+count+" after="+after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Log.i("TextChanged","s = "+s.toString()+" count="+count+" before="+before);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Double freq;
            if(s.length()>0){
                freq = Double.parseDouble(s.toString());
                if(freq == 0.0){
                    s.replace(1,s.length(),setFreq(0.0,0.0));
                }
                if(freq > maxFreq && s.length() >= 3){
                    Toast.makeText(getView().getContext(), getString(R.string.set_max)+" "+maxFreq.toString(), Toast.LENGTH_SHORT).show();
                    if(s.length()>2)s.delete(s.length()-1,s.length());
                    return;
                }
                if(freq < minFreq && s.length() >=3){
                    Toast.makeText(getView().getContext(), getString(R.string.set_min)+" "+minFreq.toString(), Toast.LENGTH_SHORT).show();
                    if(s.length()>2)s.delete(s.length()-1,s.length());
                    return;
                }
                //Log.i("Editor",s.toString());
                if(s.length() == 3)s.append(".");
                if(s.length()>= 4)curFreq = freq;
            }
        }
    }
    public static class Channel extends Fragment implements OnClickListener  {
        private Double curRxFreq = 400.0;
        private Double curTxFreq = 400.0;
        private Integer curTxCt = 0;
        private Integer curRxCt = 0;
        private Integer Sq = 8;
        public Channel(SharedPreferences mSettings){

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
                //final EditText nick = (EditText)Settings.findViewById(R.id.set_nick);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(Settings)
                        // Add action buttons
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //
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
        private String Nick;
        private String History;
        private Boolean Power;
        private Intercom cIntrecom = new Intercom();
        public Chat(SharedPreferences mSettings){
            Nick = mSettings.getString(APP_PREFERENCES_NICK, "MyNick");
            History = mSettings.getString(APP_PREFERENCES_HISTORY, "<h1>"+getString(R.string.title_chat)+"</h1>");
            Power = Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_POWER,"false"));
        }
        private void setPower(Boolean power){
            Power = power;
        }
        private String getNick(){
            return Nick;
        }
        private void setNick(String nick){
            Nick = nick;
        }
        private String getHistory(){
            return History;
        }
        private void setHistory(String history){
            History = history;
        }
        private Spanned getHtml(){
            return Html.fromHtml(History+"<br/>");
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
            msg.setOnKeyListener(this);
            nick.setText(Nick+" >");
            chat.setText(getHtml());
            scroll.fullScroll(View.FOCUS_DOWN);
            return rootView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            TextView nick = (TextView)getView().findViewById(R.id.nick);
            nick.setText(Nick+" >");
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
                        msg = "<div>"+Nick+"&nbsp;&gt;"+msg+"</div>";
                        try{
                            if(Power)cIntrecom.sendMessage(msg);
                        }catch (NoSuchMethodError e){
                            Log.w("Message","can not be send");
                            msg += "<i>"+getString(R.string.not_send)+"</i>";
                        }
                        History += msg;
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
                            mManual.setMinFreq(minFreq);
                            mManual.setMaxFreq(maxFreq);
                            mManual.setStep(Step);
                            mChat.setNick(Nick);
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

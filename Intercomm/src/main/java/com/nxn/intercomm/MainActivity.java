package com.nxn.intercomm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
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
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.text.TextWatcher;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


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
    SharedPreferences mSettings;
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
    public static final String FORMAT = "###.####";

    public static final Double[] steps = {0.00625,0.01250,0.025}; //Frequency step array
    public String Nick = "MyNick";
    public Double curFreq = 446.00625;
    public Integer curCt = 0;
    public Double Step = steps[1];
    public Double minFreq = 400.0;
    public Double maxFreq = 480.0;
    public Integer Sq = 1;
    public Integer Volume = 5;
    public Boolean Power;
    public SharedPreferences.Editor editor;
    public ManualFrequency mManual;
    public Channel mChannels;
    public Chat mChat;

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
        minFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MIN_FREQ, minFreq.toString()));
        maxFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_MAX_FREQ,maxFreq.toString()));
        curFreq = Double.parseDouble(mSettings.getString(APP_PREFERENCES_TX_FREQ,curFreq.toString()));
        curCt = Integer.parseInt(mSettings.getString(APP_PREFERENCES_TX_CTCSS, curCt.toString()));
        Step = Double.parseDouble(mSettings.getString(APP_PREFERENCES_STEP, Step.toString()));
        Volume = Integer.parseInt(mSettings.getString(APP_PREFERENCES_VOLUME,Volume.toString()));
        Sq = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SQ,Sq.toString()));
        Log.i("OnCREATE","CurFREQ ="+curFreq.toString());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setIcon(R.drawable.ic_launcher);
        //actionBar.setCustomView(R.layout.actionbar);
        //mEnable = (ToggleButton)actionBar.getCustomView().findViewById(R.id.enable);
        //mEnable.setOnClickListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPagerAdapter = new SampleAdapter(this, getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        /**
         * Manual Frequency set settings
         */
        mManual = new ManualFrequency();
        mManual.setMaxFreq(maxFreq);
        mManual.setMinFreq(minFreq);
        mManual.setCurFreq(curFreq);
        mManual.setStep(Step);
        mManual.setSq(Sq);
        mManual.setCt(curCt);
        mManual.setVolume(Volume);

        mChannels = new Channel();
        mChat = new Chat();
        mChat.setNick(Nick);
        mViewPager.setAdapter(mPagerAdapter);
        //mManual.getView().addOnAttachStateChangeListener(this);
        //mPagerAdapter.getItem(0).getView().findViewById(R.id.freq_next).toString();//.setOnClickListener(this);


        //mManual.getArguments().putDouble(APP_PREFERENCES_MIN_FREQ,minFreq);

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

        Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                //runOnUiThread(new Runnable() {
                //    public void run() {
                        if(!curFreq.equals(mManual.getCurFreq())){
                            curFreq = mManual.getCurFreq();
                            editor.putString(APP_PREFERENCES_TX_FREQ,curFreq.toString());
                            editor.commit();
                        }
                        if(!Sq.equals(mManual.getSq())){
                            Sq = mManual.getSq();
                            editor.putString(APP_PREFERENCES_SQ, Sq.toString());
                            editor.commit();
                        }
                        if(!curCt.equals(mManual.getCt())){
                            curCt = mManual.getCt();
                            editor.putString(APP_PREFERENCES_TX_CTCSS, curCt.toString());
                            editor.commit();
                        }
                        if(!Volume.equals(mManual.getVolume())){
                            Volume = mManual.getVolume();
                            editor.putString(APP_PREFERENCES_VOLUME,Volume.toString());
                            editor.commit();
                        }

                //    }
                //});
            }
        }, 0, 5000);
    }

    @Override
    public void onClick(View view){


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //if(Boolean.parseBoolean(mSettings.getString(APP_PREFERENCES_POWER,"false")))
        //menu.findItem(R.id.power).setIcon(android.R.drawable.ic_lock_idle_charging);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
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
                /**
                 * TODO: Power Control
                 */
                if(item.isChecked()){
                    item.setIcon(android.R.drawable.ic_lock_power_off);
                    Toast.makeText(this, R.string.power_disabled, Toast.LENGTH_SHORT).show();
                    Power=false;
                }else{
                    item.setIcon(android.R.drawable.ic_lock_idle_charging);
                    Toast.makeText(this, R.string.power_enabled, Toast.LENGTH_SHORT).show();
                    Power=true;
                }
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
        private Double minFreq = 400.0;
        private Double maxFreq = 480.0;
        private Double Step = 0.00625;
        private Integer Sq = 1;
        private Integer Ct = 0;
        private Integer Volume = 5;
        public ManualFrequency(){

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
            /**
             * TODO: Set volume of Sound
             */
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
                    /**
                     * TODO: Set tone code
                     */
                    break;
                case R.id.sq:
                    Sq = position+1;
                    /**
                     * TODO: Set SQ parameter
                     */
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
            }catch (Exception e){
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
                if(s.length()>= 6)curFreq = freq;
                /**
                 * TODO: Set FREQUENCY immedeatly here
                 */
            }
        }
    }
    public static class Channel extends Fragment implements OnClickListener  {
        public Channel(){

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
            rootView = inflater.inflate(R.layout.channels, container, false);
            /**
             * TODO: Channel List and manipulation
             */
            return rootView;
        }
    }
    public static class Chat extends Fragment implements
            OnClickListener,
            View.OnKeyListener
    {
        private String Nick;
        private String History;
        public Chat(){

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
            return Html.fromHtml(History);
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
            History = "<h1>"+getString(R.string.title_chat)+"</h1>\n";
            View rootView;
            rootView = inflater.inflate(R.layout.chat, container, false);
            assert rootView != null;
            TextView nick = (TextView)rootView.findViewById(R.id.nick);
            TextView chat = (TextView)rootView.findViewById(R.id.chat);
            EditText msg = (EditText)rootView.findViewById(R.id.message);
            chat.setText(getHtml());
            msg.setOnKeyListener(this);
            chat.scrollTo(0,100000);
            nick.setText(Nick+" >");
            /**
             * TODO: Chat
             */
            return rootView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            switch (keyCode){
                case KeyEvent.KEYCODE_ENTER :
                    EditText e = (EditText)getView().findViewById(R.id.message);
                    String msg = e.getText().toString();
                    if(!msg.equals("")){
                        TextView w = (TextView)getView().findViewById(R.id.chat);
                        History += "<div>"+Nick+"&nbsp;&gt;"+msg+"</div>";
                        w.setText(getHtml());
                        w.scrollBy(0,1000);
                        e.setText("");
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
            final EditText nic = (EditText)Settings.findViewById(R.id.set_nick);
            nic.setText(Nick);
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
                            Nick = nic.getText().toString();
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

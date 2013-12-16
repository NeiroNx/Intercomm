package com.nxn.intercomm;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    public static final String APP_PREFERENCES_TX_FREQ = "tx_freq";
    public static final String APP_PREFERENCES_RX_FREQ = "rx_freq";
    public static final String APP_PREFERENCES_TX_CTCSS = "tx_ctcss";
    public static final String APP_PREFERENCES_RX_CTCSS = "rx_ctcss";
    public static final String APP_PREFERENCES_SQ = "sq";
    public static final String APP_PREFERENCES_POWER = "power";
    public static final String APP_PREFERENCES_MIN_FREQ = "min_freq";
    public static final String APP_PREFERENCES_MAX_FREQ = "tx_freq";
    public static final String FORMAT = "###.#####";


    public Double curFreq = 446.00625;
    public Double minFreq = 400.0;
    public Double maxFreq = 480.0;
    ImageButton next;
    ImageButton prew;
    EditText freq;

    ToggleButton mEnable;
    public ManualFrequency mManual;
    public Channel mChannels;
    public Chat mChat;

    @Override
    protected void onPause() {
        super.onPause();
        //SharedPreferences.Editor editor = mSettings.edit();
        //editor.putInt(APP_PREFERENCES_COUNTER, counter);
        //editor.apply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        mManual = new ManualFrequency();
        mManual.setMaxFreq(maxFreq);
        mManual.setMinFreq(minFreq);
        mManual.setCurFreq(curFreq);
        mChannels = new Channel();
        mChat = new Chat();
        mViewPager.setAdapter(mPagerAdapter);
        //mManual.getView().addOnAttachStateChangeListener(this);
        //mPagerAdapter.getItem(0).getView().findViewById(R.id.freq_next).toString();//.setOnClickListener(this);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //minFreq = 0.0;

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

        //next = (ImageButton) mViewPager.findViewById(R.id.freq_next);

        //next.setOnClickListener(this);
        //prew = (ImageButton) mViewPager.findViewById(R.id.freq_perw);
        //prew.setOnClickListener(this);
        //freq = (EditText) mSectionsPagerAdapter.getItem(0).getTargetFragment().getView().findViewById(R.id.freq);
       // freq.setText("446.00625");


        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(
                actionBar.newTab()
                        .setText(getString(R.string.title_section1).toUpperCase())
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(getString(R.string.title_section2).toUpperCase())
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(getString(R.string.title_section3).toUpperCase())
                        .setTabListener(this));


    }
    public String setFreq(String str, Double delta){
        NumberFormat Format = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)Format).applyPattern(FORMAT);
        Format.setMinimumFractionDigits(5);
        Format.setMinimumIntegerDigits(3);
        Double num = curFreq;
        if(str != null)
            num = Double.parseDouble(str);
        num += delta;
        if(num < minFreq) num = minFreq;
        if(num > maxFreq) num = maxFreq;
        curFreq = num;
        return Format.format(num);
    }
    @Override
    public void onClick(View view){


        /*Double num;
        try {
            num = Double.parseDouble(freq.getText().toString());
        }catch (Exception error){
            num = 0.0;
        }
        switch (view.getId()){
            case R.id.freq_perw:
                freq.setText(verify(Format.format(num - 0.00025)));
                freq.selectAll();
                break;
            case R.id.freq_next:
                freq.setText(verify(Format.format(num + 0.00025)));
                freq.selectAll();
                break;
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
            case R.id.action_settings:
                return true;
            case R.id.action_quit:
                EditText t = (EditText)mViewPager.getChildAt(0).findViewById(R.id.freq);
                if(t != null)t.setText("222");
                //finish();
                return true;
            case R.id.power:
                if(item.isChecked()){
                    item.setIcon(android.R.drawable.checkbox_off_background);
                    Toast.makeText(this, R.string.power_disabled, Toast.LENGTH_SHORT).show();
                    item.setChecked(false);
                }else{
                    item.setIcon(android.R.drawable.checkbox_on_background);
                    Toast.makeText(this, R.string.power_enabled, Toast.LENGTH_SHORT).show();
                    item.setChecked(true);
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
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3);
            }
            return null;
        }
    }

    public static class ManualFrequency extends Fragment implements OnClickListener {
        private Double curFreq;
        private Double minFreq = 400.0;
        private Double maxFreq = 480.0;
        private Double step = 0.00025;
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
        String setFreq(String str, Double delta){
            NumberFormat Format = NumberFormat.getInstance(Locale.ENGLISH);
            ((DecimalFormat)Format).applyPattern(FORMAT);
            Format.setMinimumFractionDigits(5);
            Format.setMinimumIntegerDigits(3);
            Double num = curFreq;
            if(str != null)
                num = Double.parseDouble(str);
            num += delta;
            if(num < minFreq) num = minFreq;
            if(num > maxFreq) num = maxFreq;
            curFreq = num;
            return Format.format(num);
        }
        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.freq_next:
                    EditText e = (EditText)getView().findViewById(R.id.freq);
                    e.setText(setFreq(e.getText().toString(),step));
                    break;
                case R.id.freq_prew:
                    EditText s = (EditText)getView().findViewById(R.id.freq);
                    s.setText(setFreq(s.getText().toString(),-step));
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
            View rootView = inflater.inflate(R.layout.manual_freq, container, false);
            rootView.findViewById(R.id.freq_next).setOnClickListener(this);
            rootView.findViewById(R.id.freq_prew).setOnClickListener(this);
            //minFreq = getArguments().getDouble(APP_PREFERENCES_MIN_FREQ);
            //maxFreq = getArguments().getDouble(APP_PREFERENCES_MAX_FREQ);
            return rootView;
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
            return rootView;
        }
    }
    public static class Chat extends Fragment implements OnClickListener  {
        public Chat(){

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
            return rootView;
        }
    }

}

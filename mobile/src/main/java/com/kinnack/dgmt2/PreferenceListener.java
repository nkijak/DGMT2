package com.kinnack.dgmt2;

import android.content.SharedPreferences;
import android.util.Log;

import com.kinnack.dgmt2.event.ServerChanged;
import com.kinnack.dgmt2.event.UserNameChanged;
import com.kinnack.dgmt2.option.Option;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

/**
 * Created by nicolas on 4/25/15.
 */
public class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
    //TODO how do you get these to match the file.  THis is error prone already.
    private static final String HOST_KEY = "pref_key_dagny_server_host";
    private static final String PORT_KEY = "pref_key_dagny_server_port";
    private static final String USERNAME_KEY = "pref_key_username";
    private Bus mBus;
    private SharedPreferences sharedPreferences;
    public PreferenceListener(Bus bus, SharedPreferences preferences) {
        mBus = bus;
        this.sharedPreferences = preferences;
        bus.register(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       this.sharedPreferences = sharedPreferences;
       switch (key) {
           case HOST_KEY:
                mBus.post(produceServiceChangedEvent());
           case PORT_KEY:
               mBus.post(produceServiceChangedEvent());
           case USERNAME_KEY:
               mBus.post(produceUsernameChangedEvent());
       }
    }

    @Produce
    public ServerChanged produceServiceChangedEvent() {
        String host = sharedPreferences.getString(HOST_KEY, "");
        int key = Integer.parseInt(sharedPreferences.getString(PORT_KEY, "80"));
        Log.d("PreferenceListener", "ServerChanged(" + host + ", " + key + ")");
        return new ServerChanged(host, key);
    }

    @Produce
    public UserNameChanged produceUsernameChangedEvent() {
       return new UserNameChanged(Option.apply(sharedPreferences.getString(USERNAME_KEY, null)));
    }
}

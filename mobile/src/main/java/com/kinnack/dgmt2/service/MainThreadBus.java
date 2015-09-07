package com.kinnack.dgmt2.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;

public class MainThreadBus extends Bus {
        private final Handler mainThread = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Log.d("MainThreadLooper", "POSTING " + event);
                super.post(event);
            } else {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        MainThreadBus.this.post(event);
                    }
                });
            }
        }

        public void anyPost(final Object event) {
            super.post(event);
        }
}

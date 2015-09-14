package com.kinnack.dgmt2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.kinnack.dgmt2.model.BoundedPingCalculator;
import com.kinnack.dgmt2.option.Option;
import com.kinnack.dgmt2.service.MainThreadBus;
import com.kinnack.dgmt2.service.SampleHttpService;
import com.kinnack.dgmt2.service.SampleRepository;
import com.kinnack.dgmt2.service.SampleStorage;
import com.kinnack.dgmt2.service.SnappyRepo;
import com.kinnack.dgmt2.service.StatisticsService;
import com.kinnack.dgmt2.service.sampling.HttpSampleStorage;
import com.kinnack.dgmt2.service.sampling.LocalSampleServiceDecorator;
import com.kinnack.dgmt2.service.sampling.PingBroadcast;
import com.kinnack.dgmt2.service.sampling.SampleStorageDbHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.squareup.otto.Bus;

import static com.kinnack.dgmt2.option.Option.None;
import static com.kinnack.dgmt2.option.Option.Some;

/**
 * Created by prodrive555 on 4/18/15.
 */
public class DGMT2 extends MultiDexApplication {
    final static int PING_MIN_MIN = 5;
    final static int PING_MAX_MIN = 65;
    private SampleRepository mSampleRepo;
    final private Bus bus = new MainThreadBus();
    final private SampleHttpService sampleHttpService = new SampleHttpService();
    final private PingGenerator pingGenerator = new PingGenerator(new BoundedPingCalculator(PING_MIN_MIN,PING_MAX_MIN));
    final private Gson gson = new GsonBuilder().create();
    private PreferenceListener preferenceListener;
    private LocalSampleServiceDecorator localSampleServiceDecorator;
    private PingBroadcast pingBroadcast;

    synchronized
    public SampleRepository getSampleRepository() {
        if (mSampleRepo == null) {
            mSampleRepo = new SampleRepository(
                bus,
                getLocalSampleServiceDecorator(getStorage())
            );
        }
        return mSampleRepo;
    }

    private SampleStorage getStorage() {
        return new HttpSampleStorage(sampleHttpService, bus);
    }


    public Bus getBus() { return bus; }
    public PingGenerator getPingGenerator() {
        return pingGenerator;
    }

    public SampleHttpService getSampleHttpService() { return sampleHttpService; }

    public LocalSampleServiceDecorator getLocalSampleServiceDecorator(SampleStorage storage) {
        if (localSampleServiceDecorator == null)  {
            localSampleServiceDecorator = new LocalSampleServiceDecorator(this, storage, bus);
        }
        return localSampleServiceDecorator;
    }

    private SampleStorageDbHelper sampleStorageDbHelper;
    public SampleStorageDbHelper getSampleStorageDbHelper() {
        if (sampleStorageDbHelper == null) {
            sampleStorageDbHelper = new SampleStorageDbHelper(this);
        }

        return sampleStorageDbHelper;
    }

    private SnappyRepo snappyRepo;
    public Option<SnappyRepo> getRecordRepo() {
        if (snappyRepo == null) {
            try {
                snappyRepo = new SnappyRepo(DBFactory.open(this));
            } catch (SnappydbException sde) {
                Log.e("DGMT2#getRecordRepo", "Couldn't open Snappy DB", sde);
                return None();
            }
        }
        return Some(snappyRepo);
    }

    private StatisticsService statsService;
    public StatisticsService getStatsService() {
        if (statsService == null) {
            statsService = new StatisticsService(getRecordRepo().get());
        }
        return statsService;
    }


    @Override
    public void onCreate() {
        Log.d("DGMT2", "DGMT2 application created");

        super.onCreate();

        bus.register(sampleHttpService);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        preferenceListener = new PreferenceListener(bus, preferences);

        self = this;

    }

    private static DGMT2 self;

    public static Option<DGMT2> instance() {
       return Option.apply(self);
    }
}

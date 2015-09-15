package com.kinnack.dgmt2.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.R;
import com.kinnack.dgmt2.event.UserNameChanged;
import com.kinnack.dgmt2.model.Record;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;
import com.kinnack.dgmt2.service.RecordRepository;
import com.kinnack.dgmt2.service.SnappyRepo;
import com.kinnack.dgmt2.service.StatisticsService;
import com.kinnack.dgmt2.widget.Overview;
import com.squareup.otto.Subscribe;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.kinnack.dgmt2.option.Option.None;
import static com.kinnack.dgmt2.option.Option.Some;
import static com.kinnack.dgmt2.service.StatisticsService.FOURTYEIGHT_HOURS_IN_MS;

public class SelectExerciseActivity extends AppCompatActivity {
    private static final String PUSHUPS="pushups";
    private static final String NEGPULLUPS="-pullups";
    private static final String SQUATS="squats";
    private final int FAB_ENTRY=101;
    private StatisticsService statsService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        DGMT2 dgmt = (DGMT2)getApplication();
        dgmt.getBus().register(this);

        statsService = dgmt.getStatsService();

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle("DGMT");

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectExerciseActivity.this, ManualCountEntry.class), FAB_ENTRY);
            }
        });
        findViewById(R.id.pushupsByDayChart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(InboxActivity.showHistory(SelectExerciseActivity.this, PUSHUPS));
            }
        });
        findViewById(R.id.negPushupsByDayChart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(InboxActivity.showHistory(SelectExerciseActivity.this, NEGPULLUPS));
            }
        });
        findViewById(R.id.squatsByDayChart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(InboxActivity.showHistory(SelectExerciseActivity.this, SQUATS));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCounts();
    }

    protected void updateCounts() {
        int pushupsRemaining = 100 - statsService.typeCountForPeriod(PUSHUPS, FOURTYEIGHT_HOURS_IN_MS);
        int negPullupsRemaining = 30 - statsService.typeCountForPeriod(NEGPULLUPS, FOURTYEIGHT_HOURS_IN_MS);
        int squatsRemaining = 100 - statsService.typeCountForPeriod(SQUATS, FOURTYEIGHT_HOURS_IN_MS);

        ((Button)findViewById(R.id.pushups)).setText(pushupsRemaining + " PUSHUPS");
        ((Button)findViewById(R.id.negPullups)).setText(negPullupsRemaining + " -PULLUPS");
        ((Button)findViewById(R.id.squats)).setText(squatsRemaining + " SQUATS");


        updateCard(PUSHUPS, R.id.pushupsByDayChart, R.id.avgPushupsPerDayNum,R.id.avgPushupsPerNum);
        updateCard(NEGPULLUPS, R.id.negPushupsByDayChart, R.id.avgNegpullupsPerDayNum, R.id.avgNegpullupsPerNum);
        updateCard(SQUATS, R.id.squatsByDayChart, R.id.avgSquatsPerDayNum, R.id.avgSquatsPerNum);
    }

    protected void updateCard(String type, int chart, int perDay, int per) {
        TreeMap<String, Integer> buckets = getBuckets(type);
        ((Overview)findViewById(chart)).setBuckets(buckets);
        ((TextView) findViewById(perDay)).setText(""+avgForBuckets(buckets));
        SummaryStatistics ss = statsService.statsForPeriod(type, FOURTYEIGHT_HOURS_IN_MS);
        ((TextView)findViewById(per)).setText(""+Math.round(ss.getMean()));
    }

    protected TreeMap<String, Integer> getBuckets(String type) {
        Map<Long, SummaryStatistics> byDay = statsService.typeByDay(type);
        List<Long> times = new ArrayList(byDay.keySet());
        Collections.sort(times);
        List<Long> lastWeek = times.subList(Math.max(0, times.size() - 7), times.size());
        Collections.reverse(lastWeek);
        TreeMap<String, Integer> counts = new TreeMap<>();
        for(Long time : lastWeek) {
            counts.put(time.toString(), (int) byDay.get(time).getSum());
        }
        return counts;
    }

    protected long avgForBuckets(TreeMap<String, Integer> buckets) {
        SummaryStatistics ss = new SummaryStatistics();
        for (Integer i : buckets.values()) {
            ss.addValue(i);
        }
        return Math.round(ss.getMean());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startSettingsActivity();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateCounts();
        switch(requestCode) {
            case FAB_ENTRY:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(findViewById(R.id.container), "Entry Saved", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("SEA", "ATTEMPTING TO UNDO");
                                }
                            })
                            .show();
                }
        }
    }


    public void getPushups(View view) { getCount(PUSHUPS); }

    public void getNegPullups(View view) { getCount(NEGPULLUPS); }

    public void getSquats(View view) { getCount(SQUATS); }

    protected Intent getCount(String type) {
        Intent intent = new Intent(this, ManualCountEntry.class);
        intent.putExtra(ManualCountEntry.INTENT_TYPE_STRING, type);
        startActivityForResult(intent, FAB_ENTRY);
        return intent;
    }


    @Subscribe public void usernameChanged(UserNameChanged event) {
        if(!event.getUsername().canIterate()) {
            Toast.makeText(this, "Enter your username", Toast.LENGTH_LONG);
            startSettingsActivity();
        }
    }

    private Intent startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return intent;
    }
}


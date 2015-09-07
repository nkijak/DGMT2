package com.kinnack.dgmt2.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.R;
import com.kinnack.dgmt2.event.UserNameChanged;
import com.kinnack.dgmt2.model.Record;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;
import com.kinnack.dgmt2.service.RecordRepository;
import com.kinnack.dgmt2.service.SnappyRepo;
import com.squareup.otto.Subscribe;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.kinnack.dgmt2.option.Option.None;
import static com.kinnack.dgmt2.option.Option.Some;

public class SelectExerciseActivity extends AppCompatActivity {
    private static final String PUSHUPS="pushups";
    private static final String NEGPULLUPS="-pullups";
    private static final String SQUATS="squats";
    private static final long FOURTYEIGHT_HOURS_IN_MS = 48 * 60 * 60 * 1000;
    private final int FAB_ENTRY=101;
    private Option<SnappyRepo> repo = None();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        DGMT2 dgmt = (DGMT2)getApplication();
        dgmt.getBus().register(this);

        repo = dgmt.getRecordRepo();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectExerciseActivity.this, ManualCountEntry.class), FAB_ENTRY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCounts();
    }

    protected void updateCounts() {
        int pushupsRemaining = 100 - fourtyEightHourCountFor(PUSHUPS);
        int negPullupsRemaining = 30 - fourtyEightHourCountFor(NEGPULLUPS);
        int squatsRemaining = 100 - fourtyEightHourCountFor(SQUATS);

        ((Button)findViewById(R.id.pushups)).setText(pushupsRemaining + " PUSHUPS");
        ((Button)findViewById(R.id.negPullups)).setText(negPullupsRemaining + " -PULLUPS");
        ((Button)findViewById(R.id.squats)).setText(squatsRemaining + " SQUATS");
    }

    protected int fourtyEightHourCountFor(final String type) {
        return repo.map(new Function1<SnappyRepo, Collection<Record>>() {
            @Override
            public Collection<Record> apply(SnappyRepo r) {
                long now = new Date().getTime();
                return r.query(type, new Date(now - FOURTYEIGHT_HOURS_IN_MS), new Date(now));
            }
        }).catamorph(0, new Function1<Collection<Record>, Integer>() {
            @Override
            public Integer apply(Collection<Record> records) {
                int total = 0;
                Option<Record> first = None();
                Option<Record> last = None();
                for(Record record: records)  {
                    if (first == null) first = Some(record);
                    last = Some(record);
                    total += record.getCount();
                }
                long firstWhen = first.map(new Function1<Record, Long>() {
                    @Override
                    public Long apply(Record element) {
                        return element.getWhen();
                    }
                }).getOrElse(0l);
                long lastWhen = last.map(new Function1<Record, Long>() {
                    @Override
                    public Long apply(Record element) {
                        return element.getWhen();
                    }
                }).getOrElse(0l);
                int firstCount = first.map(new Function1<Record, Integer>() {
                    @Override
                    public Integer apply(Record element) {
                        return element.getCount();
                    }
                }).getOrElse(0);

                double timeUntilNextExpire = firstWhen + FOURTYEIGHT_HOURS_IN_MS - lastWhen;
                double timeSinceLast = new Date().getTime() - lastWhen;
                double percentOfTimeElapsed = timeSinceLast / timeUntilNextExpire;

                double lost = Math.ceil(firstCount * percentOfTimeElapsed);
                return (int)(total - lost);
            }
        });
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


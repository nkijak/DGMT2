package com.kinnack.dgmt2.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.R;
import com.kinnack.dgmt2.event.UserNameChanged;
import com.squareup.otto.Subscribe;

public class SelectExerciseActivity extends AppCompatActivity {
    private final int FAB_ENTRY=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        DGMT2 dgmt = (DGMT2)getApplication();
        dgmt.getBus().register(this);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectExerciseActivity.this, ManualCountEntry.class), FAB_ENTRY);
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


    public void getPushups(View view) { getCount("pushups"); }

    public void getNegPullups(View view) { getCount("-pullups"); }

    public void getSquats(View view) { getCount("squats"); }

    protected Intent getCount(String type) {
        Intent intent = new Intent(this, ManualCountEntry.class);
        intent.putExtra(ManualCountEntry.INTENT_TYPE_STRING, type);
        startActivity(intent);
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


package com.kinnack.dgmt2.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.R;
import com.kinnack.dgmt2.model.Record;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;
import com.kinnack.dgmt2.service.RecordHandlerService;
import com.kinnack.dgmt2.service.RecordRepository;
import com.kinnack.dgmt2.service.SnappyRepo;

import java.util.Date;

public class ManualCountEntry extends AppCompatActivity {
    private Button submit;
    private EditText count;
    private TextView requirementText;
    private String type;
    private String tags;

    public static final String INTENT_TYPE_STRING="com.kinnack.dgmt2.activity.ManualCountEntry.TYPE";
    public static final String INTENT_TAGS_STRING="com.kinnack.dgmt2.activity.ManualCountEntry.TAGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_count_entry);
        submit = (Button) findViewById(R.id.submit);
        count = (EditText) findViewById(R.id.count);
        requirementText = (TextView) findViewById(R.id.requirementText);
        type = getIntent().getStringExtra(INTENT_TYPE_STRING);
        tags = getIntent().getStringExtra(INTENT_TAGS_STRING);

        //TODO set this up
        //((DGMT2)getApplication()).getBus().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_count_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSubmit(View view) {
        int value = Integer.parseInt(count.getText().toString());

        final long now = new Date().getTime();

        RecordRepository.recordEntry(this, tags, type, value, new Date().getTime());
        //TODO this goes in a subscribed callback
        setResult(RESULT_OK);
        finish();
    }
}

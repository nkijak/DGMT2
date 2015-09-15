package com.kinnack.dgmt2.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kinnack.dgmt2.DGMT2;
import com.kinnack.dgmt2.R;
import com.kinnack.dgmt2.model.Record;
import com.kinnack.dgmt2.option.Option;
import com.kinnack.dgmt2.service.SnappyRepo;
import com.kinnack.dgmt2.service.StatisticsService;
import com.kinnack.dgmt2.widget.Overview;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.TreeMap;

public class InboxActivity extends AppCompatActivity {
    private final static String TYPE_EXTRA = "com.kinnack.dgmt2.TYPE_EXTRA";

    public static Intent showHistory(Context context, String type) {
        Intent intent = new Intent(context, InboxActivity.class);
        intent.putExtra(TYPE_EXTRA, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        final Option<String> type = Option.apply(getIntent().getStringExtra(TYPE_EXTRA));

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle(type.getOrElse("Indoc"));
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(this));

        final SnappyRepo repo = ((DGMT2)getApplication()).getRecordRepo().get();

        final Record[] records = repo.queryAll(type.getOrElse("")).toArray(new Record[]{});

        TreeMap<String, Integer> counts = new TreeMap<>();
        for (Record record: records) {
            counts.put(record.getWhen()+"", record.getCount());
        }
        ((Overview) findViewById(R.id.bar_chart)).setBuckets(counts);

        rv.setAdapter(new RecyclerView.Adapter<ViewHolder>(){

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.inbox_item, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Record record = records[records.length - 1 - position ];
                holder.count.setText(""+record.getCount());
                holder.when.setText(""+new Date(record.getWhen()).toString());
            }

            @Override
            public int getItemCount() {
                return records.length;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
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

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView count;
        TextView when;

        public ViewHolder(View itemView) {
            super(itemView);
            count = (TextView) itemView.findViewById(R.id.count);
            when = (TextView) itemView.findViewById(R.id.when);
        }
    }
}

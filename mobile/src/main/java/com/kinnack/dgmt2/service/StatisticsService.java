package com.kinnack.dgmt2.service;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.kinnack.dgmt2.IntentUtils;
import com.kinnack.dgmt2.model.Record;
import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.kinnack.dgmt2.option.Option.None;
import static com.kinnack.dgmt2.option.Option.Some;

public class StatisticsService {
    private SnappyRepo mRepo;

    public static final long FOURTYEIGHT_HOURS_IN_MS = 48 * 60 * 60 * 1000;
    public static final long TWENTYFOUR_HOURS_IN_MS  = 24 * 60 * 60 * 1000;

    public StatisticsService(SnappyRepo repo) {
        mRepo = repo;
    }

    public Map<Long, SummaryStatistics> typeByDay(final String type) {
        Collection<Record> records = mRepo.queryAll(type);
        Multimap<Long, Record> countsByDay = Multimaps.index(records, new Function<Record, Long>() {
            @Override
            public Long apply(Record input) {
                Calendar when = Calendar.getInstance();
                when.setTimeInMillis(input.getWhen());
                when.set(Calendar.HOUR, 0);
                when.set(Calendar.MINUTE, 0);
                when.set(Calendar.SECOND, 0);
                when.set(Calendar.MILLISECOND, 0);
                return when.getTimeInMillis();
            }
        });
        Map<Long, SummaryStatistics> fuckingJavaFUCK = new HashMap<Long, SummaryStatistics>(countsByDay.size());
        for( Map.Entry<Long, Collection<Record>> entry : countsByDay.asMap().entrySet() ) {
            SummaryStatistics ss = new SummaryStatistics();
            for(Record record: entry.getValue()) {
                ss.addValue(record.getCount());
            }
            fuckingJavaFUCK.put(entry.getKey(), ss);
        }
        return fuckingJavaFUCK;
    }

    public int typeCountForPeriod(final String type, final long period) {
        long now = new Date().getTime();
        Collection<Record> records = mRepo.query(type, new Date(now - period), new Date(now));

        int total = 0;
        Option<Record> first = None();
        Option<Record> last = None();
        for(Record record: records)  {
            if (!first.canIterate()) first = Some(record);
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

}

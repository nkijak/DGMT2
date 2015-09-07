package com.kinnack.dgmt2.model;

import android.util.Log;

import com.kinnack.dgmt2.option.Option;
import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sample {
    private String userId;
    private long when;
    private List<String> tags;
    private boolean synced = false;
    private Map<String, Map<String, Object>> extras;

    public static Sample apply(String userId, long when, CsvTags tags, boolean synced) {
        Sample sample = new Sample(userId, when, tags.toList());

        sample.synced = synced;
        return sample;
    }

    public Sample(String userId, long when, List<String> tags) {
        if (userId == null || when <= 0 || tags == null)
            throw new IllegalArgumentException("All values must not be null for a sample");

        this.userId = userId;
        this.when = when;
        this.tags = tags;
        this.extras = new HashMap<>();
    }

    private Sample(String userId, long when, List<String> tags, Map<String, Map<String, Object>> extras) {
        this.userId = userId;
        this.when = when;
        this.tags = tags;
        this.extras = extras;
    }


    public Sample withTags(CsvTags tags) {
        return new Sample(this.userId, this.when, tags.toList(), this.extras);
    }

    public Sample withUser(String user) {
        return new Sample(user, this.when, this.tags, this.extras);
    }

    public String getUserId() {
        return userId;
    }

    public long getWhen() {
        return when;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isSynced() { return synced; }

    public Option<Map<String, Object>> getExtras(String key) {
        return Option.apply(this.extras.get(key));
    }
    public void addExtras(String key, Map<String, Object> extras) {
        this.extras.put(key, extras);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sample)) return false;

        Sample sample = (Sample) o;

        if (when != sample.when) return false;
        if (!tags.equals(sample.tags)) return false;
        if (!userId.equals(sample.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + (int) (when ^ (when >>> 32));
        result = 31 * result + tags.hashCode();
        return result;
    }

    public static final Joiner TagJoiner = Joiner.on(", ");
    public String toTagString() {
        Log.d("Sample", "Joining tags ");
        return TagJoiner.join(tags);
    }

    public String toString() {
        return String.format("userId: %s, when: %s, tags: %s", userId, when, tags);
    }

}

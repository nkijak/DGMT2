package com.kinnack.dgmt2.model;

import com.kinnack.dgmt2.option.Function1;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by prodrive555 on 4/18/15.
 */
public class CsvTags { // implments Functor[Array[String]]
    private String[] mTags;

    public CsvTags(String input) {
        if (input != null && input.length() > 0) {
            mTags = input.split(",");
        } else {
            mTags = new String[0];
        }
    }

    private CsvTags(String[] tags) {
        mTags = tags;
    }

    public CsvTags map(Function1<String, String> f) {
        String[] retval = new String[mTags.length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = f.apply(mTags[i]);
        }
        return new CsvTags(retval);
    }

    public <B> B catamorph(B notThere, Function1<String[], B> there) {
        return mTags.length == 0 ? notThere: there.apply(mTags);
    }

    public CsvTags associate(CsvTags other) {
        Set<String> tagset = new HashSet<String>(mTags.length + other.mTags.length);
        for (String tag : mTags) tagset.add(tag);
        for (String tag : other.mTags) tagset.add(tag);
        return new CsvTags(tagset.toArray(new String[]{}));
    }

    public String[] get() {
        return mTags;
    }

    public List<String> toList() {
        return catamorph(Collections.<String>emptyList(), new Function1<String[], List<String>>() {
                    @Override
                    public List<String> apply(String[] tags) {
                        return Arrays.asList(tags);
                    }
                }
        );
    }

}



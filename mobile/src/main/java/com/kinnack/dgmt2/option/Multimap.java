package com.kinnack.dgmt2.option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Multimap<K, V> {
    private Map<K, List<V>> _map = new HashMap<K, List<V>>();

    public static<K2, V2> Multimap<K2, V2> index(Collection<V2> input, Function1<V2, K2> fun) {
        Multimap<K2, V2> mmap = new Multimap<>();
        for (V2 value : input) {
            K2 key = fun.apply(value);
            mmap.put(key, value);
        }
        return mmap;
    }

    public void put(K key, V value) {
        List<V> list = _map.get(key);
        if (list == null) list = new ArrayList<>();
        list.add(value);
        _map.put(key, list);
    }

    public Map<K, List<V>> asMap() {
        return _map;
    }
    public int size() { return _map.size(); }
}

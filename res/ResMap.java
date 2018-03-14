package com.ntunin.cybervision.res;

import java.util.HashMap;

/**
 * Created by nikolay on 08.04.17.
 */

public class ResMap<K, V> extends HashMap {


    public Object get(int id) {
        return this.get(Res.string(id));
    }

    public void put(int id, V value) {
        this.put(Res.string(id), value);
    }
}

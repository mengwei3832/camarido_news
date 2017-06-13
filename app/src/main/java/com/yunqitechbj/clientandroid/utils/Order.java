package com.yunqitechbj.clientandroid.utils;

import com.yunqitechbj.clientandroid.entity.OfflineStorage;

import java.util.Comparator;

/**
 * Created by mengwei on 2017/2/27.
 */

public class Order implements Comparator<OfflineStorage> {
    @Override
    public int compare(OfflineStorage lhs, OfflineStorage rhs) {
        int r = (int) (rhs.getId() - lhs.getId());
        return r;
    }
}

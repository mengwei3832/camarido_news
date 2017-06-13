package com.yunqitechbj.clientandroid.http.response;

import java.util.ArrayList;

/**
 * Created by mengwei on 2016/10/25.
 */

public class Response<T,E> {
    public int totalCount;
    public T singleData;
    public ArrayList<E> data;
    public String[] enumFields;
    public boolean isSuccess;
    public String message;
    public int ErrCode;
}

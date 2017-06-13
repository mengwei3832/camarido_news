package com.yunqitechbj.clientandroid.entity;

import java.io.Serializable;

/**
 * Created by mengwei on 2016/10/28.
 */

public class LoginInfo implements Serializable {
    public String tokenValue;
    public String tokenExpires;
    public String userId;
    public String userType;
    public boolean isTempUser;
    public int userRegistStatus;
}

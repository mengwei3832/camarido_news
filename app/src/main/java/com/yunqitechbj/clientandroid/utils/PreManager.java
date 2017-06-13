package com.yunqitechbj.clientandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mengwei on 2016/10/25.
 */

public class PreManager {
    private static PreManager preManager;

    private SharedPreferences mShare;

    public SharedPreferences getShare() {
        return mShare;
    }

    private PreManager(Context context) {
        mShare = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized PreManager instance(Context context) {
        if (preManager == null)
            preManager = new PreManager(context);
        return preManager;
    }

    /**
     * @param firstStart
     * @throws
     * @Title:setFirstStart
     * @Description:设置程序是否第一次启动
     * @return:void
     * @Create: 2014-3-14 上午10:44:27
     * @Author : zhm 邮箱：zhaomeng@baihe.com
     */
    public void setFirstStart(boolean firstStart) {
        mShare.edit().putBoolean("first_start_key", firstStart).commit();
    }

    /**
     * @return
     * @throws
     * @Title:getFirstStart
     * @Description:获取程序是否第一次启动
     * @return:boolean
     * @Create: 2014-3-14 上午10:44:33
     * @Author : zhm 邮箱：zhaomeng@baihe.com
     */
    public boolean getFirstStart() {
        return mShare.getBoolean("first_start_key", true);
    }

    /**
     * 保存登录token
     *
     * @param token
     * @throws
     * @Description:
     * @Title:setToken
     * @return:void
     * @Create: 2015年11月22日 下午3:35:12
     * @Author : zhm
     */
    public void setToken(String token) {
        L.e("----------token------------"+token);
        mShare.edit().putString("tokeb_key", token).commit();
    }

    /**
     * 获取保存到本地的token
     *
     * @return
     * @throws
     * @Description:
     * @Title:getToken
     * @return:String
     * @Create: 2015年11月22日 下午3:35:29
     * @Author : zhm
     */
    public String getToken() {
        return mShare.getString("tokeb_key", "");
    }

    /**
     * 保存token过期时间
     *
     * @param token
     * @throws
     * @Description:
     * @Title:setTokenExpires
     * @return:void
     * @Create: 2015年11月22日 下午3:36:00
     * @Author : zhm
     */
    public void setTokenExpires(String token) {
        mShare.edit().putString("tokeb_expires_key", token).commit();
    }

    /**
     * 删除token过期时间
     *
     * @throws
     * @Description:
     * @Title:setTokenExpires
     * @return:void
     * @Create: 2015年11月22日 下午3:36:00
     * @Author : zhm
     */
    public void removeTokenExpires() {
        mShare.edit().remove("tokeb_expires_key").commit();
    }

    /**
     * token过期时间
     *
     * @return
     * @throws
     * @Description:
     * @Title:getTokenExpires
     * @return:long
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhm
     */
    public String getTokenExpires() {
        return mShare.getString("tokeb_expires_key","");
    }

    /**
     * 是否登录状态
     *
     * @return
     * @throws
     * @Description:
     * @Title:getTokenExpires
     * @return:boolean
     * @Create: 2015年12月05日
     * @Author : zhangwb
     */
    public boolean isLogin() {
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime < mShare.getLong("tokeb_expires_key", 0);
    }

    /**
     * 保存排序标识
     *
     * @param sortType
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setOrderSortType(int sortType) {
        mShare.edit().putInt("order_sort_type", sortType).commit();
    }

    /**
     * 获取排序标识
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhm
     */
    public int getOrderSortType() {
        return mShare.getInt("order_sort_type", 1);
    }

    /**
     * 保存纬度
     *
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setLatitude(String latitude) {
        mShare.edit().putString("latitude", latitude).commit();
    }

    /**
     * 获取纬度
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhangwb
     */
    public String getLatitude() {
        return mShare.getString("latitude", "");
    }

    /**
     * 保存经度
     *
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setLongitude(String longitude) {
        mShare.edit().putString("longitude", longitude).commit();
    }

    /**
     * 获取经度
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhangwb
     */
    public String getLongitude() {
        return mShare.getString("longitude", "");
    }

    /**
     * 保存uid
     *
     * @param userId
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setUserId(String userId) {
        mShare.edit().putString("uid", userId).commit();
    }

    /**
     * 获取uid
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhangwb
     */
    public String getUserId() {
        return mShare.getString("uid", "0");
    }

    /**
     * 保存消息气泡数
     *
     * @param msgBubble
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setMsgBubble(int msgBubble) {
        mShare.edit().putInt("msgBubble", msgBubble).commit();
    }

    /**
     * 获取消息气泡数
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhangwb
     */
    public int getMsgBubble() {
        return mShare.getInt("msgBubble", 0);
    }

    /**
     * 保存关注气泡数
     *
     * @param attentionBubble
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setAttentionBubble(int attentionBubble) {
        mShare.edit().putInt("attentionBubble", attentionBubble).commit();
    }

    /**
     * 获取关注气泡数
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhangwb
     */
    public int getAttentionBubble() {
        return mShare.getInt("attentionBubble", 0);
    }

    /**
     * 保存活动气泡数
     *
     * @param activeBubble
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setActiveBubble(int activeBubble) {
        mShare.edit().putInt("activeBubble", activeBubble).commit();
    }

    /**
     * 获取活动气泡数
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhangwb
     */
    public int getActiveBubble() {
        return mShare.getInt("activeBubble", 0);
    }

    /**
     * 保存头像
     *
     * @throws
     * @Description:
     * @return:void
     * @Create: 2015年11月22日
     * @Author : zhangwb
     */
    public void setAvatar(String avatar) {
        mShare.edit().putString("avatar", avatar).commit();
    }

    /**
     * 获取头像
     *
     * @return
     * @throws
     * @Description:
     * @return:int
     * @Create: 2015年11月22日 下午3:36:08
     * @Author : zhangwb
     */
    public String getAvatar() {
        return mShare.getString("avatar", "");
    }

    /**
     * @Description:class 保存企业名字简称
     * @Title:setCompanyName
     * @param companyName
     * @return:void
     * @throws
     * @Create: 2016-6-6 下午4:17:27
     * @Author : mengwei
     */
    public void setCompanyName(String companyName) {
        mShare.edit().putString("companyName", companyName).commit();
    }

    /**
     * @Description:class 获取企业名字简称
     * @Title:getCompanyName
     * @return
     * @return:String
     * @throws
     * @Create: 2016-6-6 下午4:17:39
     * @Author : mengwei
     */
    public String getCompanyName() {
        return mShare.getString("companyName", "");
    }

    /**
     * 保存自动登录状态
     * @param isAutoLogin
     */
    public void setAutoLoginStatus(boolean isAutoLogin){
        mShare.edit().putBoolean("autoLogin",isAutoLogin).commit();
    }

    /**
     * 获取自动登录状态
     * @return
     */
    public boolean getAutoLoginStatus(){
        return mShare.getBoolean("autoLogin",true);
    }

    /**
     * 保存是否登录进去
     * @param isInput
     */
    public void setIsLoginInput(boolean isInput){
        mShare.edit().putBoolean("isInput",isInput).commit();
    }

    /**
     * 获取是否登录进去
     * @return
     */
    public boolean getIsLoginInput(){
        return mShare.getBoolean("isInput",false);
    }

    /**
     * 保存登陆用户名
     * @param userName
     */
    public void setUserName(String userName){
        mShare.edit().putString("userName",userName).commit();
    }

    /**
     * 获取登录用户名
     * @return
     */
    public String getUserName(){
        return mShare.getString("userName","");
    }

    public void setLoadUrl(int mark){
        mShare.edit().putInt("mark", mark).commit();
    }

    public Integer getLoadUrl(){
        return mShare.getInt("mark",0);
    }

    /**
     * 保存派车页面网址
     * @param url 派车页面网址
     */
    public void setLoadTicketUrl(String url){
        mShare.edit().putString("VehicleUrl", url).commit();
    }

    /**
     * 获取派车页面网址
     * @return 派车页面网址
     */
    public String getLoadTicketUrl(){
        return mShare.getString("VehicleUrl","");
    }
}

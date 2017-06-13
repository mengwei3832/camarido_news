package com.yunqitechbj.clientandroid.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.yunqitechbj.clientandroid.entity.Phone;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mengwei on 2016/10/26.
 */

public class StringUtils {
    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测手机号码
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone){
        if (StringUtils.isEmpty(phone)){
            return false;
        }
        return phone.matches("^[1][3-8][0-9]{9}$");
    }

    public static boolean isVehicle(String vehicle){
        if (StringUtils.isEmpty(vehicle)){
            return false;
        }
        return vehicle.matches("^[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
    }

    /**
     * 检测密码是否符合要求
     * @param pass
     * @return
     */
    public static boolean isPass(String pass){
        if (StringUtils.isEmpty(pass)){
            return false;
        }
        return pass.matches("^[0-9a-zA-Z]{6,14}$");
    }

    /**
     * MD5加密
     * @param string
     * @return
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 将字符串转为时间戳
     * @param time
     * @return
     */
    public static long getStringToDate(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        long l = 0;
        try {
            date = sdf.parse(time);
            String str = String.valueOf(date.getTime());
            l = Long.valueOf(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static Long getCurrentTime(){
        long mCurrent = System.currentTimeMillis();
        return mCurrent;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断是否包含中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是闰年
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year){
        if (year % 100 == 0) {
            if (year % 400 == 0) {
                return true;
            }
        } else {
            if (year % 4 == 0) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Phone> readCotacts(Context mContext){
        ArrayList<Phone> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            ContentResolver resolver = mContext.getContentResolver();            //查询联系人数据
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,null,null,null,null);
            //遍历联系人列表
            while (cursor.moveToNext()){
                //获取联系人姓名
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                ));
                //获取联系人手机号
                String number = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                ));
                Log.v("woider", "Name:" + name + "\tPhone:" + number);
                Phone callPhone = new Phone();
                callPhone.setName(name);
                callPhone.setPhone(number);
                list.add(callPhone);
            }
            Log.e("woider","---------lisrt-------"+list.toString());
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return list;

    }

    /**
     * 获取手机联系人
     * @param context
     * @return
     */
    public static ArrayList<Phone> getAllContacts(Context context) {

        ArrayList<Phone> contacts = new ArrayList<Phone>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {

                //新建一个联系人实例
                Phone temp = new Phone();
                String contactId = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                temp.setName(name);

                //获取联系人所有电话号
                Cursor phones = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                                + contactId, null, null);
                while (phones.moveToNext()) {

                    String phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    temp.setPhone(phoneNumber);
                }
                phones.close();
                //获取联系人所有邮箱.
                Cursor emails = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
                                + contactId, null, null);

                while (emails.moveToNext()) {
                    String email = emails
                            .getString(emails
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    //                temp.emails.add(email);
                }
                emails.close();

                // 获取联系人头像
                //            temp.photo = getContactPhoto(context, contactId,
                //                    R.drawable.ic_launcher);
                contacts.add(temp);
            }
        }catch (Exception e){
            T.showShort(context,"请到设置中开启应用通讯录权限");
            e.printStackTrace();
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        L.e("-------contacts--------"+contacts.toString());
        return contacts;
    }

    /**
     * 获取手机联系人头像
     *
     * @param c
     * @param personId
     * @param defaultIco
     * @return
     */
    private static Bitmap getContactPhoto(Context c, String personId,
                                          int defaultIco) {
        byte[] data = new byte[0];
        Uri u = Uri.parse("content://com.android.contacts/data");
        String where = "raw_contact_id = " + personId
                + " AND mimetype ='vnd.android.cursor.item/photo'";
        Cursor cursor = c.getContentResolver()
                .query(u, null, where, null, null);
        if (cursor.moveToFirst()) {
            data = cursor.getBlob(cursor.getColumnIndex("data15"));
        }
        cursor.close();
        if (data == null || data.length == 0) {
            return BitmapFactory.decodeResource(c.getResources(), defaultIco);
        } else
            return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}

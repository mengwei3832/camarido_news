package com.yunqitechbj.clientandroid.entity;

/**
 * 报价单实体类
 * Created by mengwei on 2016/12/29.
 */

public class QuoteModel extends IDontObfuscate {
    public String id;
    public String tenantName;
    public String packageBeginAddress;
    public String packageEndAddress;
    public String effectiveTime;
    public String price;
    public String packageId;

    @Override
    public String toString() {
        return "QuoteModel{" +
                "id='" + id + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", packageBeginAddress='" + packageBeginAddress + '\'' +
                ", packageEndAddress='" + packageEndAddress + '\'' +
                ", effectiveTime='" + effectiveTime + '\'' +
                ", price='" + price + '\'' +
                ", packageId='" + packageId + '\'' +
                '}';
    }
}

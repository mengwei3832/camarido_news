package com.yunqitechbj.clientandroid.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by mengwei on 2016/12/21.
 */

public class OfflineStorage extends DataSupport implements Serializable {
    private long id;

    private String ticketCode;//运单号
    private String ticketId;//运单Id
    private String infoId;//报价单Id

    private String vehicleName;//车牌号
    private String vehiclePhone;//跟车电话

    private String minehairDun;//矿发吨数
    private String minehairDate;//矿发时间
    private String minehireImg;//矿发单据图片

    private String signedDun;//签收吨数
    private String signedDate;//签收时间
    private String signedImg;//签收单据图片
    private boolean uploadSuccess;//所有数据是否上传成功

    private long uploadTime;//上传时间
    private long updateTime;//更新时间

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    private long expired = 0;//判断数据是否已过期，0表示没过期，1表示已过期

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehiclePhone() {
        return vehiclePhone;
    }

    public void setVehiclePhone(String vehiclePhone) {
        this.vehiclePhone = vehiclePhone;
    }

    public String getMinehairDun() {
        return minehairDun;
    }

    public void setMinehairDun(String minehairDun) {
        this.minehairDun = minehairDun;
    }

    public String getMinehairDate() {
        return minehairDate;
    }

    public void setMinehairDate(String minehairDate) {
        this.minehairDate = minehairDate;
    }

    public String getMinehireImg() {
        return minehireImg;
    }

    public void setMinehireImg(String minehireImg) {
        this.minehireImg = minehireImg;
    }

    public String getSignedDun() {
        return signedDun;
    }

    public void setSignedDun(String signedDun) {
        this.signedDun = signedDun;
    }

    public String getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(String signedDate) {
        this.signedDate = signedDate;
    }

    public String getSignedImg() {
        return signedImg;
    }

    public void setSignedImg(String signedImg) {
        this.signedImg = signedImg;
    }

    public boolean isUploadSuccess() {
        return uploadSuccess;
    }

    public void setUploadSuccess(boolean uploadSuccess) {
        this.uploadSuccess = uploadSuccess;
    }

    @Override
    public String toString() {
        return "OfflineStorage{" +
                "id=" + id +
                ", ticketCode='" + ticketCode + '\'' +
                ", ticketId='" + ticketId + '\'' +
                ", infoId='" + infoId + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", vehiclePhone='" + vehiclePhone + '\'' +
                ", minehairDun='" + minehairDun + '\'' +
                ", minehairDate='" + minehairDate + '\'' +
                ", minehireImg='" + minehireImg + '\'' +
                ", signedDun='" + signedDun + '\'' +
                ", signedDate='" + signedDate + '\'' +
                ", signedImg='" + signedImg + '\'' +
                ", uploadSuccess=" + uploadSuccess +
                ", uploadTime=" + uploadTime +
                ", updateTime=" + updateTime +
                ", expired=" + expired +
                '}';
    }
}

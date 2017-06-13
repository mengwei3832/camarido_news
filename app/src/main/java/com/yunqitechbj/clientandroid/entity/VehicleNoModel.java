package com.yunqitechbj.clientandroid.entity;

/**
 * Created by mengwei on 2017/1/6.
 */

public class VehicleNoModel {
    public String VehicleNo;
    public String Tel;
    public String TicketId;
    public String TicketCode;
    public String InfoId;

    public String getInfoId() {
        return InfoId;
    }

    public void setInfoId(String infoId) {
        InfoId = infoId;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getTicketId() {
        return TicketId;
    }

    public void setTicketId(String ticketId) {
        TicketId = ticketId;
    }

    public String getTicketCode() {
        return TicketCode;
    }

    public void setTicketCode(String ticketCode) {
        TicketCode = ticketCode;
    }

    @Override
    public String toString() {
        return "VehicleNoModel{" +
                "VehicleNo='" + VehicleNo + '\'' +
                ", Tel='" + Tel + '\'' +
                ", TicketId='" + TicketId + '\'' +
                ", TicketCode='" + TicketCode + '\'' +
                ", InfoId='" + InfoId + '\'' +
                '}';
    }
}

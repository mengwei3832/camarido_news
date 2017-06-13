package com.yunqitechbj.clientandroid.http.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.yunqitechbj.clientandroid.entity.UploadResult;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.response.NullObject;
import com.yunqitechbj.clientandroid.http.response.Response;

import java.lang.reflect.Type;

/**
 * 上传离线数据请求类
 * Created by mengwei on 2017/1/4.
 */

public class UploadOfflineRequest extends IRequest {

    /**
     * 上传所需参数
     * @param context
     * @param infoId 报价单Id
     * @param ticketId 运单Id
     * @param vehicleNo 车牌号
     * @param vehicleContacts 跟车电话
     * @param weightInit 矿发吨数
     * @param initTime 矿发时间
     * @param initFileContent 矿发单据
     * @param initFileName 矿发单据名称
     * @param weightReach 签收吨数
     * @param reachTime 签收时间
     * @param reachFileContent 签收单据
     * @param reachFileName 签收单据名称
     */
    public UploadOfflineRequest(Context context,String infoId,String ticketId,String vehicleNo,
                                String vehicleContacts,String weightInit,String initTime,String initFileContent,
                                String initFileName,String weightReach,String reachTime,String reachFileContent,
                                String reachFileName) {
        super(context);
        mParams.put("InfoId",infoId);
        mParams.put("TicketId",ticketId);
        mParams.put("VehicleNo",vehicleNo);
        mParams.put("VehicleContacts",vehicleContacts);
        mParams.put("WeightInit",weightInit);
        mParams.put("InitTime",initTime);
        mParams.put("InitFileContent",initFileContent);
        mParams.put("InitFileName",initFileName);
        mParams.put("WeightReach",weightReach);
        mParams.put("ReachTime",reachTime);
        mParams.put("ReachFileContent",reachFileContent);
        mParams.put("ReachFileName",reachFileName);
    }

    @Override
    public String getUrl() {
        return HostUtil.getWebHost() + "api/Auth/OffLineArrageVehicle";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<UploadResult, NullObject>>() {
        }.getType();
    }
}

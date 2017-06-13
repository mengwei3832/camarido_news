package com.yunqitechbj.clientandroid.weight;

import com.yunqitechbj.clientandroid.http.HostUtil;

/**
 * Created by mengwei on 2016/11/2.
 */

public class Config {
    public static class API {
        //注册
        public static final String WEB_REGISTER_URL = HostUtil.getWebHost() + "InfoDepart/SecondAuth/BrokerRegisterLogin";
        //注册补全信息
        public static final String WEB_IMPROVE_INFORMATION_URL = HostUtil.getWebHost() + "InfoDepart/SecondAuth/RegisterLoginBrokerMessage?tel=";
        //交易广场
        public static final String WEB_TRANS_URL = HostUtil.getWebHost() + "infodepart/Trade/TradeIndex";
        //长跑路线
        public static final String WEB_RUN_URL = HostUtil.getWebHost() + "infodepart/Advantage/AdvantageIndex";
        //企业钱包
        public static final String WEB_WALLET_URL = HostUtil.getWebHost() + "infodepart/Wallet/Wallet";
        //车辆管理
        public static final String WEB_VEHICLE_URL = HostUtil.getWebHost() + "InfoDepart/VehicleManage/VehicleManagePage";
        //系统帮助
        public static final String WEB_HELP_URL = HostUtil.getWebHost() + "infodepart/Wallet/Service";
    }
}

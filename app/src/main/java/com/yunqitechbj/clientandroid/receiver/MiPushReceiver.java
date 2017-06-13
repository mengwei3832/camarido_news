package com.yunqitechbj.clientandroid.receiver;

import android.content.Context;

import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.yunqitechbj.clientandroid.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * PushMessageReceiver是个抽象类，该类继承了BroadcastReceiver。 以上这些方法运行在非UI线程中
 * Created by mengwei on 2016/10/25.
 */

public class MiPushReceiver extends PushMessageReceiver {
    public static final String MIPUSH_ACTION = "MIPUSH_ACTION"; // 小米推送广播action

    /**
     * 用来接收服务器向客户端发送的透传消息
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceivePassThroughMessage(Context context,
                                            MiPushMessage message) {
    }

    /**
     * 用来接收服务器向客户端发送的通知消息， 这个回调方法会在用户手动点击通知后触发
     *
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageClicked(Context context,
                                             MiPushMessage message) {
        L.e("TAG", "--------------jinlaile-----------");

        String content = message.getContent();

        L.e("TAG", "------------content------------" + content.toString());

        JSONObject data = null;
        try {
            data = new JSONObject(content);

            L.e("TAG", "--------------data-----------" + data.toString());

            int pushType = data.getInt("PushType");
            String relationId = data.getString("RelationId");

            L.e("TAG", "--------------pushType-----------" + pushType);

            L.e("TAG",
                    "--------------relationId-----------"
                            + relationId.toString());

            miPushInvoke(context, pushType, relationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来接收服务器向客户端发送的通知消息 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数
     *
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageArrived(Context context,
                                             MiPushMessage message) {
        String content = message.getContent();
        JSONObject data = null;
        try {
            data = new JSONObject(content);

            int pushType = data.getInt("PushType");

            //			miPushAddBubble(context, pushType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来接收客户端向服务器发送命令后的响应结果
     *
     * @param context
     * @param message
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {

    }

    /**
     * 用来接收客户端向服务器发送注册命令后的响应结果
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceiveRegisterResult(Context context,
                                        MiPushCommandMessage message) {

    }

    /**
     * 推送跳转逻辑
     *
     * @param pushType
     * @param relationId
     */
    private void miPushInvoke(Context context, int pushType, String relationId) {
        //		switch (pushType) {
        //		case PACAKGE_MSG:
        //			PackageListDetailActivity.invokeNewTask(context, relationId);
        //			break;
        //		case ORDER_MSG:
        //			MyTicketDetailActivity.invokeNewTask(context, relationId);
        //			break;
        //		case DETAIL_MSG:
        //			AttentionDetailActivity.invokeNewTask(context, relationId);
        //			break;
        //		case MESSAGE_MSG:
        //			L.e("TAG", "-----------跳转消息界面--------------");
        //
        //			MessageActivity.invokeNewTask(context);
        //			// Intent intent = new Intent(context, MessageActivity.class);
        //			// context.startActivity(intent);
        //			break;
        //		case PKG_PACKAGE_MSG:// 跳转到订单详情页面
        //			BaoLieBiaoDetailActivity.invokeNewTask(context, relationId);
        //
        //			break;
        //		case PKG_BAOJIA_MSG:// 跳转到订单详情页面
        //			BaoLieBiaoDetailActivity.invokeNewTask(context, relationId);
        //
        //			break;
        //		case PKG_SHENHE_MSG:// 跳转到运单页面
        //			CurrentTicketActivity.invokeNewTask(context, relationId);
        //
        //			break;
        //		case PKG_TICKET_CAR_MSG:// 跳转到运单页面
        //			CurrentTicketActivity.invokeNewTask(context, relationId);
        //
        //			break;
        //		case PKG_TICKET_VEHICLE_MSG:// 跳转到运单页面
        //			CurrentTicketActivity.invokeNewTask(context, relationId);
        //
        //			break;
        //
        //		case PKG_YIJIA_MSG: // 跳转到订单详情
        //			BaoLieBiaoDetailActivity.invokeNewTask(context, relationId);
        //			break;
        //		case PKG_QUXIAO_MAG: // 跳转到订单详情
        //			BaoLieBiaoDetailActivity.invokeNewTask(context, relationId);
        //			break;
        //		case PKG_TICKET_DANJU: // 跳转到单据信息
        //			UploadOrderAuditActivity.invokeNewTask(context, relationId, true,
        //					"6");
        //			break;
        //		case PKG_TICKET_JIESUAN: // 跳转到已结算详情
        //			UploadOrderActivity.invokeNewTask(context, relationId, "8", false);
        //			break;
        //		case PKG_QIANBAO_MINGXI: // 跳转到钱包明细界面
        //			MingXiActivity.invokeNewTask(context);
        //			break;
        //		}
        //
    }
}

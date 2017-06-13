package com.yunqitechbj.clientandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;

import org.litepal.crud.DataSupport;

/**
 * Created by mengwei on 2017/3/1.
 */

public class DialogUtils {

    /**
     * 取消运单的弹出窗
     * @param context
     * @param str1
     * @param str2
     * @param id
     */
    public static void createDialog(final Activity context, String str1, String str2, final long id){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View net_view = layoutInflater.inflate(R.layout.dialog_unnetwork, null);

        TextView tvCancel = (TextView) net_view.findViewById(R.id.tv_dialog_net_cancel);
        TextView tvSure = (TextView) net_view.findViewById(R.id.tv_dialog_net_sure);
        TextView tvText1 = (TextView) net_view.findViewById(R.id.tv_dialogutils_text1);
        TextView tvText2 = (TextView) net_view.findViewById(R.id.tv_dialogutils_text2);
        tvText1.setText(str1);
        tvText2.setText(str2);
        if (!StringUtils.isEmpty(str2)){
            tvText2.setVisibility(View.VISIBLE);
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final AlertDialog builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                DataSupport.delete(OfflineStorage.class,id);
                context.finish();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

    }

    public static void guoqiDialog(final Activity context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View net_view = layoutInflater.inflate(R.layout.dialog_tishi_display, null);

        Button btSure = (Button) net_view.findViewById(R.id.bt_dialogutils_sure_guoqi);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final AlertDialog builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();

        btSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }
}

package com.yunqitechbj.clientandroid.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;

/**
 * 多选辅助类
 * Created by mengwei on 2016/12/30.
 */

public class ChoiceListItemView extends LinearLayout implements Checkable {
    private TextView tvVehicleName;
    private TextView tvTicketCode;
    private TextView tvKuangDun;
    private TextView tvKuangDate;
    private TextView tvQianDun;
    private TextView tvQianDate;
    private CheckBox cbSelectBtn;

    public ChoiceListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View display_view = inflater.inflate(R.layout.item_offline_diplay,this,true);
        tvVehicleName = (TextView) display_view.findViewById(R.id.tv_item_display_vehiclename);
        tvTicketCode = (TextView) display_view.findViewById(R.id.tv_item_display_ticketcode);
        tvKuangDun = (TextView) display_view.findViewById(R.id.tv_item_display_kuangdun);
        tvKuangDate = (TextView) display_view.findViewById(R.id.tv_item_display_kuangdate);
        tvQianDun = (TextView) display_view.findViewById(R.id.tv_item_display_qiandun);
        tvQianDate = (TextView) display_view.findViewById(R.id.tv_item_display_qiandate);
        cbSelectBtn = (CheckBox) display_view.findViewById(R.id.cb_item_display_shangchuan);
    }

    public void setVehicleName(String VehicleName) {
        tvVehicleName.setText(VehicleName);
    }

    public void setTicketCode(String TicketCode) {
        tvTicketCode.setText(TicketCode);
    }

    public void setKuangDun(String KuangDun) {
        tvKuangDun.setText(KuangDun);
    }

    public void setKuangDate(String KuangDate) {
        tvKuangDate.setText(KuangDate);
    }

    public void setQianDun(String QianDun) {
        tvQianDun.setText(QianDun);
    }

    public void setQianDate(String QianDate) {
       tvQianDate.setText(QianDate);
    }

    @Override
    public void setChecked(boolean checked) {
        cbSelectBtn.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return cbSelectBtn.isChecked();
    }

    @Override
    public void toggle() {
        cbSelectBtn.toggle();
    }
}

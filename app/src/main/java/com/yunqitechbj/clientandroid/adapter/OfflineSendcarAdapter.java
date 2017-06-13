package com.yunqitechbj.clientandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.activity.OfflineSendCarActivity;
import com.yunqitechbj.clientandroid.activity.UploadDataActivity;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.utils.StringUtils;

import java.util.List;

/**
 * Created by mengwei on 2016/12/21.
 */

public class OfflineSendcarAdapter extends BaseAdapter {
        private Context mContext;
        private List<OfflineStorage> offlineList;

        public OfflineSendcarAdapter(Context context, List<OfflineStorage> offlineList) {
            mContext = context;
            this.offlineList = offlineList;
        }

        @Override
        public int getCount() {
            return offlineList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_offline_sendcar,null);
                viewHolder.tvVehicleName = (TextView) convertView.findViewById(R.id.tv_item_sendcar_vehiclename);
                viewHolder.tvTicketCode = (TextView) convertView.findViewById(R.id.tv_item_sendcar_ticketcode);
                viewHolder.tvKuangDun = (TextView) convertView.findViewById(R.id.tv_item_sendcar_kuangdun);
                viewHolder.tvKuangDate = (TextView) convertView.findViewById(R.id.tv_item_sendcar_kuangdate);
                viewHolder.tvQianDun = (TextView) convertView.findViewById(R.id.tv_item_sendcar_qiandun);
                viewHolder.tvQianDate = (TextView) convertView.findViewById(R.id.tv_item_sendcar_qiandate);
                viewHolder.btShangChuan = (LinearLayout) convertView.findViewById(R.id.bt_item_sendcar_shangchuan);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            OfflineStorage offlineStorage = offlineList.get(position);

            final long id = offlineStorage.getId();
            final String vehicleName = offlineStorage.getVehicleName();
            final String ticketCode = offlineStorage.getTicketCode();
            final String kuangDun = offlineStorage.getMinehairDun();
            final String kuangDate = offlineStorage.getMinehairDate();
            final String kuangImg = offlineStorage.getMinehireImg();
            final String qianDun = offlineStorage.getSignedDun();
            final String qianDate = offlineStorage.getSignedDate();
            final String qianImg = offlineStorage.getSignedImg();

            //车牌号
            if (!StringUtils.isEmpty(vehicleName)){
                viewHolder.tvVehicleName.setText(vehicleName);
            } else {
                viewHolder.tvVehicleName.setText("");
            }

            //运单号
            if (!StringUtils.isEmpty(ticketCode)){
                viewHolder.tvTicketCode.setText(ticketCode);
            } else {
                viewHolder.tvTicketCode.setText("");
            }

            //矿发数据
            if (!StringUtils.isEmpty(kuangDun)){
                viewHolder.tvKuangDun.setText(kuangDun);
            } else {
                viewHolder.tvKuangDun.setText("0");
            }
            if (!StringUtils.isEmpty(kuangDate)){
                viewHolder.tvKuangDate.setText("  ("+kuangDate.substring(0,16)+")");
            } else {
                viewHolder.tvKuangDate.setText("");
            }
            //签收数据
            if (!StringUtils.isEmpty(qianDun)){
                viewHolder.tvQianDun.setText(qianDun);
            } else {
                viewHolder.tvQianDun.setText("0");
            }
            if (!StringUtils.isEmpty(qianDate)){
                viewHolder.tvQianDate.setText("  ("+qianDate+")");
            } else {
                viewHolder.tvQianDate.setText("");
            }
            //上传数据
            viewHolder.btShangChuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入上传数据页面
//                    UploadDataActivity.invoke(mContext,id,vehicleName,ticketCode);
                    OfflineStorage mOfflineStorage1 = new OfflineStorage();
                    mOfflineStorage1.setId(id);
//                    mOfflineStorage1.setVehicleName(vehicleName);
//                    mOfflineStorage1.setTicketCode(ticketCode);
//                    mOfflineStorage1.setMinehairDun(kuangDun);
//                    mOfflineStorage1.setMinehairDate(kuangDate);
//                    mOfflineStorage1.setMinehireImg(kuangImg);
//                    mOfflineStorage1.setSignedDun(qianDun);
//                    mOfflineStorage1.setSignedDate(qianDate);
//                    mOfflineStorage1.setSignedImg(qianImg);

                    Intent intent = new Intent(mContext,UploadDataActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("OfflineModel", mOfflineStorage1);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            return convertView;
        }

    class ViewHolder{
        TextView tvVehicleName;
        TextView tvTicketCode;
        TextView tvKuangDun;
        TextView tvKuangDate;
        TextView tvQianDun;
        TextView tvQianDate;
        LinearLayout btShangChuan;
    }
}

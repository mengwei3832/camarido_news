package com.yunqitechbj.clientandroid.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;
import com.yunqitechbj.clientandroid.entity.QuoteModel;
import com.yunqitechbj.clientandroid.entity.UploadResult;
import com.yunqitechbj.clientandroid.http.HostUtil;
import com.yunqitechbj.clientandroid.http.request.QuoteListRequest;
import com.yunqitechbj.clientandroid.http.request.UploadOfflineRequest;
import com.yunqitechbj.clientandroid.http.response.Response;
import com.yunqitechbj.clientandroid.utils.L;
import com.yunqitechbj.clientandroid.utils.StringUtils;
import com.yunqitechbj.clientandroid.utils.T;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

/**
 * 报价单列表展示页面
 * Created by mengwei on 2016/12/29.
 */

public class QuoteListActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnPullListener {
    /* 页面控件对象 */
    private LinearLayout llAll;
    private RelativeLayout rlBack;
    private ListView lvQuoteView;
    private Button btUpload;
    private ProgressWheel progress_quote;
    PullToRefreshLayout pullToRefreshLayout;

    private int selectPosition = -1;//用于记录用户选择的变量

    private ArrayList<QuoteModel> mQuoteList = new ArrayList<>();
    private QuoteModel mQuoteModel;
    QuoteAdapter quoteAdapter;

    private final int GET_QUOTE_LIST = 1;
    private final int UPLOAD_OFFLINE = 2;

    private QuoteListRequest mQuoteListRequest;
    private UploadOfflineRequest mUploadOfflineRequest;

    //传递过来的集合
    private ArrayList<OfflineStorage> offlineList = new ArrayList<>();
    public String mPackageId;
    public String mPrice;

    /* 分页参数 */
    int isFinish = 0;
    private int pageIndex = 1;// 起始页
    private int pageSize = 10;// 每页显示数量
    private int totalCount;// 返回数据的总数量
    private int count;// 实际返回的数据数量
    private boolean isEnd = false;// 是否服务器无数据返回
    private Handler handler = new Handler();

    //选中集合数据下标
    int i = 0;
    public String mInfoId;
    private String mInitFileName = "Kuangfa.jpg";
    private String mReactFileName = "Qianshou.jpg";
    private long mLongId;
    private boolean isDelete = false;

    /* 上传数据弹窗 */
    AlertDialog builder;
    private TextView tvUploadNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_quote_list;
    }

    @Override
    protected void initView() {
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        pullToRefreshLayout.setOnPullListener(this);
        llAll = obtainView(R.id.ll_quote_all);
        rlBack = obtainView(R.id.rl_quote_back);
        lvQuoteView = (ListView) pullToRefreshLayout.getPullableView();
        btUpload = obtainView(R.id.bt_quote_upload);
        progress_quote = obtainView(R.id.progress_quote);

        //获取传递过来的数据
        offlineList = (ArrayList<OfflineStorage>) getIntent().getSerializableExtra("chooseList");

        L.e("-----------offlineList-----------" + offlineList.toString());

    }

    @Override
    protected void initData() {
        isShow(true);
        getQuoteListRequest();
        quoteAdapter = new QuoteAdapter(mContext, mQuoteList);
        lvQuoteView.setAdapter(quoteAdapter);
        lvQuoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                quoteAdapter.notifyDataSetChanged();
                mQuoteModel = mQuoteList.get(position);
                mInfoId = mQuoteModel.id;
                //                T.showShort(mContext,"选中"+mQuoteModel.tenantName+"---"+mInfoId);
            }
        });
    }

    private void getQuoteListRequest() {
        count = pageIndex * pageSize;
        mQuoteListRequest = new QuoteListRequest(mContext, pageIndex, pageSize);
        mQuoteListRequest.setRequestId(GET_QUOTE_LIST);
        httpPost(mQuoteListRequest);
    }

    @Override
    public void onStart(int requestId) {
        super.onStart(requestId);
    }

    @Override
    public void onSuccess(int requestId, Response response) {
        super.onSuccess(requestId, response);
        boolean isSuccess;
        String message;
        switch (requestId) {
            case UPLOAD_OFFLINE:
                isSuccess = response.isSuccess;
                message = response.message;
                if (isSuccess) {
                    UploadResult mUploadResult = (UploadResult) response.singleData;

                    String mTicketId = mUploadResult.id;
                    String mTicketCode = mUploadResult.ticketCode;

                    L.e("---------mLongId----------" + mLongId);
                    L.e("------------mTicketId--------" + mTicketId);
                    L.e("-----------mTicketCode------------" + mTicketCode);

                    //获取当前时间
                    //                    String mCurrentTime = StringUtils.getCurrentDate();
                    long mCurrentLong = StringUtils.getCurrentTime();//当前时间

                    //                    L.e("---------上传完毕mCurrentTime-----------"+mCurrentTime);
                    L.e("----------上传完毕mCurrentLong-------------" + mCurrentLong);


                    OfflineStorage offlineStorage = new OfflineStorage();
                    offlineStorage.setTicketId(mTicketId);
                    offlineStorage.setTicketCode(mTicketCode);
                    offlineStorage.setInfoId(mInfoId);
                    if (isDelete) {
                        offlineStorage.setUploadSuccess(true);
                        isDelete = false;
                    }
                    offlineStorage.setUploadTime(mCurrentLong);
                    offlineStorage.update(mLongId);
                }
                i++;
                uploadOfflines();
                break;
            case GET_QUOTE_LIST:
                isSuccess = response.isSuccess;
                message = response.message;
                totalCount = response.totalCount;
                if (isSuccess) {
                    ArrayList<QuoteModel> list = response.data;

                    L.e("--------list------------" + list.toString());

                    if (list != null) {
                        mQuoteList.addAll(list);
                    }
                    if (totalCount <= count) {
                        isEnd = true;
                    }

                    quoteAdapter.notifyDataSetChanged();
                } else {
                    T.showShort(mContext, message);
                }
                isShow(false);
                if (isFinish == 1) {
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else if (isFinish == 2) {
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestId, int httpCode, Throwable error) {
        super.onFailure(requestId, httpCode, error);
        T.showShort(mContext, this.getResources().getString(R.string.request_failure));
        isShow(false);
        switch (requestId) {
            case UPLOAD_OFFLINE:
                if (StringUtils.isNetworkConnected(mContext)) {
                    i++;
                    uploadOfflines();
                } else {
                    builder.dismiss();
                    showUnNetworkDialog();
                }
                break;
        }
    }

    @Override
    protected void setListener() {
        btUpload.setOnClickListener(this);
        rlBack.setOnClickListener(this);
    }

    //    @Override
    //    protected void onNetworkConnected(NetUtils.NetType type) {
    //        builder.dismiss();
    //        isShow(false);
    //        getQuoteListRequest();
    //    }
    //
    //    @Override
    //    protected void onNetworkDisConnected() {
    //        builder.dismiss();
    //        showUnNetworkDialog();
    //    }

    /**
     * 判断隐藏和显示
     *
     * @param show
     */
    private void isShow(boolean show) {
        if (show) {
            llAll.setVisibility(View.GONE);
            progress_quote.setVisibility(View.VISIBLE);
        } else {
            llAll.setVisibility(View.VISIBLE);
            progress_quote.setVisibility(View.GONE);
        }
    }

    /**
     * 判断按钮是否点击
     *
     * @param isEnabled
     */
    private void isEnabled(boolean isEnabled) {
        if (isEnabled) {
            btUpload.setEnabled(true);
            btUpload.setBackgroundResource(R.mipmap.upload_shuju_save);
        } else {
            btUpload.setEnabled(false);
            btUpload.setBackgroundResource(R.mipmap.upload_shuju_zhihui);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_quote_back:
                finish();
                break;
            case R.id.bt_quote_upload://上传
                if (!StringUtils.isEmpty(mInfoId)) {
                    for (int m = 0; m < offlineList.size(); m++) {
                        if (!StringUtils.isEmpty(offlineList.get(m).getInfoId())) {
                            L.e("--------报价单Id---------" + offlineList.get(m).getInfoId());
                            L.e("--------选中的报价ID---------" + mInfoId);
                            L.e("--------报价单boolean---------" + (mInfoId.equals(offlineList.get(m).getInfoId())));
                            if (!mInfoId.equals(offlineList.get(m).getInfoId())) {
                                T.showShort(mContext, "选择的运单中有已上传的，不能上传不同报价单");
                                return;
                            }
                        }
                    }
                    //上传数据请求类
                    showUploadDialog();
                    uploadOfflines();
                } else {
                    T.showShort(mContext, "请选择一个报价单");
                }
                break;
        }
    }

    private void uploadOfflines() {
        tvUploadNum.setText("已上传   " + i + "/" + (offlineList.size()));
        if (offlineList.size() != 0) {
            if (i >= offlineList.size()) {
                T.showShort(mContext, "数据上传完毕");
                isEnabled(true);
                builder.dismiss();

                L.e("---------网址----------" + HostUtil.getWebHost() + "infodepart/Trade/Dispatch?OrderId=" + mInfoId + "&Price=" + mPrice + "&Id=" + mPackageId);

                Intent intent = new Intent(QuoteListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Url", HostUtil.getWebHost() + "infodepart/Trade/Dispatch?OrderId=" + mInfoId + "&Price=" + mPrice + "&Id=" + mPackageId);
                //                intent.putExtra("mark",1);
                startActivity(intent);
                mPreManager.setLoadTicketUrl(HostUtil.getWebHost() + "infodepart/Trade/Dispatch?OrderId=" + mInfoId + "&Price=" + mPrice + "&Id=" + mPackageId);
                mPreManager.setLoadUrl(1);
                QuoteListActivity.this.finish();
            } else {
                mLongId = offlineList.get(i).getId();
                OfflineStorage offlineStorage = DataSupport.find(OfflineStorage.class, mLongId);
                //                String mTicketId = offlineList.get(i).getTicketId();
                //                String mVehicleNo = offlineList.get(i).getVehicleName();
                //                String mVehicleContacts = offlineList.get(i).getVehiclePhone();
                //                String mWeightInit = offlineList.get(i).getMinehairDun();
                //                String mInitTime = offlineList.get(i).getMinehairDate();
                //                String mInitFileContent = offlineList.get(i).getMinehireImg();
                //                String mWeightReach = offlineList.get(i).getSignedDun();
                //                String mReachTime = offlineList.get(i).getSignedDate();
                //                String mReachFileContent = offlineList.get(i).getSignedImg();
                String mTicketId = offlineStorage.getTicketId();
                String mVehicleNo = offlineStorage.getVehicleName();
                String mVehicleContacts = offlineStorage.getVehiclePhone();
                String mWeightInit = offlineStorage.getMinehairDun();
                String mInitTime = offlineStorage.getMinehairDate();
                String mInitFileContent = offlineStorage.getMinehireImg();
                String mWeightReach = offlineStorage.getSignedDun();
                String mReachTime = offlineStorage.getSignedDate();
                String mReachFileContent = offlineStorage.getSignedImg();

                if (!StringUtils.isEmpty(mVehicleNo) &&
                        !StringUtils.isEmpty(mWeightInit) && !StringUtils.isEmpty(mWeightReach)) {
                    isDelete = true;
                }

                if (StringUtils.isEmpty(mTicketId)) {
                    mTicketId = "";
                }
                if (StringUtils.isEmpty(mWeightInit)) {
                    mWeightInit = "";
                }
                if (StringUtils.isEmpty(mInitTime)) {
                    mInitTime = "";
                }
                if (StringUtils.isEmpty(mInitFileContent)) {
                    mInitFileContent = "";
                    mInitFileName = "";
                } else {
                    mInitFileName = "Kuangfa.jpg";
                }
                if (StringUtils.isEmpty(mWeightReach)) {
                    mWeightReach = "";
                }
                if (StringUtils.isEmpty(mReachTime)) {
                    mReachTime = "";
                }
                if (StringUtils.isEmpty(mReachFileContent)) {
                    mReachFileContent = "";
                    mReactFileName = "";
                } else {
                    mReactFileName = "Qianshou.jpg";
                }

                mUploadOfflineRequest = new UploadOfflineRequest(mContext, mInfoId, mTicketId, mVehicleNo, mVehicleContacts,
                        mWeightInit, mInitTime, mInitFileContent, mInitFileName, mWeightReach, mReachTime, mReachFileContent, mReactFileName);
                mUploadOfflineRequest.setRequestId(UPLOAD_OFFLINE);
                httpPost(mUploadOfflineRequest);
                isEnabled(false);
            }
        } else {
            T.showShort(mContext, "未选中任何数据");
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mQuoteList.clear();
        isFinish = 1;
        quoteAdapter.notifyDataSetChanged();
        pageIndex = 1;
        getQuoteListRequest();
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        isFinish = 2;
        if (!isEnd) {
            ++pageIndex;
            getQuoteListRequest();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    T.showShort(mContext, "已经是最后一页了");
                }
            }, 100);
        }
    }

    public class QuoteAdapter extends BaseAdapter {
        Context context;
        List<QuoteModel> quoteList;
        LayoutInflater mInflater;

        public QuoteAdapter(Context context, List<QuoteModel> quoteList) {
            this.context = context;
            this.quoteList = quoteList;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return quoteList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_quote_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_item_quote_fabaoname);
                viewHolder.tvBegin = (TextView) convertView.findViewById(R.id.tv_item_quote_begin);
                viewHolder.tvEnd = (TextView) convertView.findViewById(R.id.tv_item_quote_end);
                viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_item_quote_date);
                viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tv_item_quote_price);
                viewHolder.select = (RadioButton) convertView.findViewById(R.id.rb_item_quote_select);
                viewHolder.llBackground = (LinearLayout) convertView.findViewById(R.id.ll_item_quote_background);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            QuoteModel quoteModel = quoteList.get(position);

            String mInfoId = quoteModel.id;
            String mPackageName = quoteModel.tenantName;
            String mBegin = quoteModel.packageBeginAddress;
            String mEnd = quoteModel.packageEndAddress;
            String mDate = quoteModel.effectiveTime;
            mPrice = quoteModel.price;
            mPackageId = quoteModel.packageId;

            L.e("---------mDate----------" + mDate);

            if (!StringUtils.isEmpty(mPackageName)) {
                viewHolder.tvName.setText(mPackageName);
            } else {
                viewHolder.tvName.setText("");
            }
            if (!StringUtils.isEmpty(mBegin)) {
                viewHolder.tvBegin.setText(mBegin);
            } else {
                viewHolder.tvBegin.setText("");
            }
            if (!StringUtils.isEmpty(mEnd)) {
                viewHolder.tvEnd.setText(mEnd);
            } else {
                viewHolder.tvEnd.setText("");
            }
            if (!StringUtils.isEmpty(mDate)) {
                String str1 = mDate.substring(0, 10);
                String str2 = mDate.substring(11, 16);
                viewHolder.tvDate.setText("生效时间：" + str1 + " " + str2);
            } else {
                viewHolder.tvDate.setText("");
            }
            if (!StringUtils.isEmpty(mPrice)) {
                viewHolder.tvPrice.setText(Html.fromHtml("<font color='#ff4444'>" + mPrice + "</font>元/吨"));
            } else {
                viewHolder.tvPrice.setText("");
            }

            if (selectPosition == position) {
                viewHolder.select.setChecked(true);
                viewHolder.llBackground.setBackgroundResource(R.mipmap.quote_check_true);
            } else {
                viewHolder.select.setChecked(false);
                viewHolder.llBackground.setBackgroundResource(R.mipmap.quote_check_false);
            }

            return convertView;
        }
    }

    public class ViewHolder {
        TextView tvName;
        TextView tvBegin;
        TextView tvEnd;
        TextView tvDate;
        TextView tvPrice;
        RadioButton select;
        LinearLayout llBackground;
    }

    /**
     * 报价单列表展示页面跳转
     *
     * @param context
     */
    public static void invoke(Context context) {
        Intent intent = new Intent(context, QuoteListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 上传进度提示弹窗
     */
    private void showUploadDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View net_view = layoutInflater.inflate(R.layout.dialog_upload_offline_progress, null);

        tvUploadNum = (TextView) net_view.findViewById(R.id.tv_dialog_upload_txt);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();
    }

    /**
     * 没有网络提示弹窗
     */
    private void showUnNetworkDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View net_view = layoutInflater.inflate(R.layout.dialog_unnetwork, null);

        TextView tvCancel = (TextView) net_view.findViewById(R.id.tv_dialog_net_cancel);
        TextView tvSure = (TextView) net_view.findViewById(R.id.tv_dialog_net_sure);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        builder = dialog.create();
        builder.setView(net_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        builder.show();

        //取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出当前App
                T.showShort(mContext, "=======点击了取消=======");
                builder.dismiss();
            }
        });
        //确定
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入离线派车界面
                Intent intent = new Intent(QuoteListActivity.this, OfflineSendCarActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                builder.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void initStatus(boolean isNet) {

    }
}

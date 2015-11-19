
package com.qianjing.finance.ui.activity.history.fragment;

import com.qianjing.finance.constant.CustomConstants;
import com.qianjing.finance.model.history.TradeLogs;
import com.qianjing.finance.net.connection.AnsynHttpRequest;
import com.qianjing.finance.net.connection.HttpCallBack;
import com.qianjing.finance.net.connection.UrlConst;
import com.qianjing.finance.ui.activity.history.WalletHistoryDetails;
import com.qianjing.finance.ui.activity.history.adapter.WalletHistoryAdapter;
import com.qianjing.finance.ui.fragment.BaseFragment;
import com.qianjing.finance.util.FormatNumberData;
import com.qianjing.finance.util.LogUtils;
import com.qianjing.finance.util.helper.StringHelper;
import com.qianjing.finance.widget.pulltorefresh.PullToRefreshBase;
import com.qianjing.finance.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.qianjing.finance.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.qianjing.finance.widget.pulltorefresh.PullToRefreshListView;
import com.qjautofinancial.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

public class WalletHistoryFragment extends BaseFragment {

    private static final int HISTORY_WALLET = 0x02;
    private int operate = CustomConstants.DEFAULT_VALUE;
    /**
     * 请求数据区分集合
     */
    ArrayList<TradeLogs> runningList = new ArrayList<TradeLogs>();
    ArrayList<TradeLogs> doneList = new ArrayList<TradeLogs>();

    private WalletHistoryAdapter historyAdapter;
    /**
     * paging
     */
    private int count = 0;
    private boolean IS_PULL_DOWN_TO_REFRESH = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_history_assemable, null);
        initView(view);
        return view;
    }

    public void setWalletParameter(int operate) {
        this.operate = operate;
    }

    private void initView(View view) {

        topTitle = (LinearLayout) view.findViewById(R.id.top_title);
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        defPage = (FrameLayout) view.findViewById(R.id.in_default_page);
        ImageView defaultPic = (ImageView) view.findViewById(R.id.iv_deault_page_pic);
        TextView defaultTxt = (TextView) view.findViewById(R.id.tv_deault_page_text);

        defPage.setVisibility(View.GONE);
        defaultPic.setBackgroundResource(R.drawable.img_history_empty);
        defaultTxt.setText(R.string.history_wallet_empty);

        ptrsv = (PullToRefreshListView) view.findViewById(R.id.ptrflv);
        ptrsv.setMode(Mode.BOTH);
        ptrsv.setShowIndicator(false);
        ptrsv.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                /**
                 * pull down to refresh
                 */
                IS_PULL_DOWN_TO_REFRESH = true;
                // Toast.makeText(getActivity(), "count="+count, 1).show();
                requestHistoryList(0, count * CustomConstants.HISTORY_PAGE_NUMBER);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                /**
                 * pull up to load
                 */
                IS_PULL_DOWN_TO_REFRESH = false;
                // Toast.makeText(getActivity(), "count="+count, 1).show();
                requestHistoryList(CustomConstants.HISTORY_PAGE_NUMBER * count,
                        CustomConstants.HISTORY_PAGE_NUMBER);
            }
        });
        refreshableView = ptrsv.getRefreshableView();
        refreshableView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

                // Caused by: java.lang.NullPointerException
                if (historyAdapter != null) {

                    if (historyAdapter.getRunningTitlePosition() == -1
                            && historyAdapter.getCompletedTitlePosition() == -1) {
                        topTitle.setVisibility(View.GONE);
                    } else if (historyAdapter.getRunningTitlePosition() == -1) {

                        if (firstVisibleItem > historyAdapter.getCompletedTitlePosition()) {
                            topTitle.setVisibility(View.VISIBLE);
                            ivIcon.setBackgroundResource(R.drawable.be_sured);
                            tvTitle.setText("已确认");
                        } else {
                            topTitle.setVisibility(View.GONE);
                        }

                    } else if (historyAdapter.getCompletedTitlePosition() == -1) {

                        if (firstVisibleItem > historyAdapter.getRunningTitlePosition()) {
                            topTitle.setVisibility(View.VISIBLE);
                            ivIcon.setBackgroundResource(R.drawable.is_running);
                            tvTitle.setText("进行中");
                        } else {
                            topTitle.setVisibility(View.GONE);
                        }

                    } else {

                        if (firstVisibleItem > historyAdapter.getCompletedTitlePosition()) {
                            topTitle.setVisibility(View.VISIBLE);
                            ivIcon.setBackgroundResource(R.drawable.be_sured);
                            tvTitle.setText("已确认");
                        } else if (firstVisibleItem > historyAdapter.getRunningTitlePosition()) {
                            topTitle.setVisibility(View.VISIBLE);
                            ivIcon.setBackgroundResource(R.drawable.is_running);
                            tvTitle.setText("进行中");
                        } else {
                            topTitle.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        refreshableView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // Toast.makeText(getActivity(), "当前条目"+position, 1).show();

                if (historyAdapter != null) {

                    TradeLogs schemaoplogsInfo = null;

                    if (position == 0) {
                        return;
                    }

                    if (historyAdapter.getRunningTitlePosition() == -1
                            && historyAdapter.getCompletedTitlePosition() == -1) {
                        return;
                    } else if (historyAdapter.getRunningTitlePosition() == -1) {

                        if (position == 1) {
                            return;
                        }

                        schemaoplogsInfo = doneList.get(position - 2);

                    } else if (historyAdapter.getCompletedTitlePosition() == -1) {

                        if (position == 1) {
                            return;
                        }

                        schemaoplogsInfo = runningList.get(position - 2);

                    } else {

                        if (position == 1) {
                            return;
                        }

                        if (position == runningList.size() + 2) {
                            return;
                        }

                        if (position > 1 && position < runningList.size() + 2) {
                            schemaoplogsInfo = runningList.get(position - 2);
                        } else if (position > runningList.size() + 2) {
                            schemaoplogsInfo = doneList.get(position - 3 - runningList.size());
                        }
                    }

                    Intent mintent = new Intent(getActivity(), WalletHistoryDetails.class);
                    mintent.putExtra("schemaoplogs", schemaoplogsInfo);
                    startActivity(mintent);
                }
            }
        });
        initHeader();
        showLoading();
        requestHistoryList(0, CustomConstants.HISTORY_PAGE_NUMBER);
    }

    public void initHeader() {

        View view = View.inflate(getActivity(), R.layout.history_header, null);

        ImageView ivTotalP = (ImageView) view.findViewById(R.id.iv_total_purchase);
        TextView tvTotalPt = (TextView) view.findViewById(R.id.tv_total_purchase_t);
        tvTotalP = (TextView) view.findViewById(R.id.tv_total_purchase);

        ImageView ivTotalR = (ImageView) view.findViewById(R.id.iv_total_redeem);
        TextView tvTotalRt = (TextView) view.findViewById(R.id.tv_total_redeem_t);
        tvTotalR = (TextView) view.findViewById(R.id.tv_total_redeem);

        ImageView ivTotalF = (ImageView) view.findViewById(R.id.iv_total_fee);
        TextView tvTotalFt = (TextView) view.findViewById(R.id.tv_total_fee_t);
        tvTotalF = (TextView) view.findViewById(R.id.tv_total_fee);

        ivTotalF.setBackgroundResource(R.drawable.history_quick);

        tvTotalPt.setText("累计转入(元):");
        tvTotalRt.setText("普通转出(元):");
        tvTotalFt.setText("快速转出(元):");

        refreshableView.addHeaderView(view);
    }

    public void requestHistoryList(int of, int nm) {
        Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
        hashTable.put("page_number", nm);
        hashTable.put("offset", of);
        if (operate != CustomConstants.DEFAULT_VALUE) {
            hashTable.put("operate", operate);
        }
        AnsynHttpRequest.requestByPost(getActivity(),
                UrlConst.TOTAL_WALLET_DETAILS, new HttpCallBack() {

                    @Override
                    public void back(String data, int status) {
                        Message msg = Message.obtain();
                        msg.obj = data;
                        msg.what = HISTORY_WALLET;
                        handler.sendMessage(msg);
                    }
                }, hashTable);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String jsonStr = (String) msg.obj;
            switch (msg.what) {
                case HISTORY_WALLET:
                    LogUtils.syso("活期宝历史列表头信息:" + jsonStr);
                    analysisHistoryData(jsonStr);
                    break;
            }
        };
    };
    private ListView refreshableView;
    private LinearLayout topTitle;
    private ImageView ivIcon;
    private TextView tvTitle;
    private PullToRefreshListView ptrsv;
    private TextView tvTotalP;
    private TextView tvTotalR;
    private TextView tvTotalF;
    private FrameLayout defPage;

    protected void analysisHistoryData(String jsonStr) {

        if (jsonStr == null || "".equals(jsonStr)) {
            dismissLoading();
            showHintDialog(getString(R.string.net_prompt));
            ptrsv.onRefreshComplete();
            return;
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            int ecode = jsonObj.optInt("ecode");
            String emsg = jsonObj.optString("emsg");
            if (ecode == 0) {

                JSONObject data = jsonObj.optJSONObject("data");

                if (StringHelper.isBlank(data.optString("list"))) {
                    defPage.setVisibility(View.VISIBLE);
                    dismissLoading();
                    ptrsv.onRefreshComplete();
                    return;
                }

                JSONArray tradeLog = data.optJSONArray("list");

                if (IS_PULL_DOWN_TO_REFRESH) {
                    runningList.clear();
                    doneList.clear();
                }

                if (tradeLog.length() < CustomConstants.HISTORY_PAGE_NUMBER) {
                    ptrsv.setMode(Mode.PULL_FROM_START);
                }

                // 解析数据
                for (int i = 0; i < tradeLog.length(); i++) {

                    TradeLogs tradeLogs = new TradeLogs();
                    JSONObject historyItem = tradeLog.getJSONObject(i);

                    tradeLogs.abbrev = historyItem.optString("abbrev");
                    tradeLogs.applyserial = historyItem.optString("applyserial");
                    tradeLogs.bank = historyItem.optString("bank");
                    tradeLogs.card = historyItem.optString("card");
                    tradeLogs.fdcode = historyItem.optString("fdcode");
                    tradeLogs.opDate = historyItem.optString("opDate");
                    tradeLogs.operate = historyItem.optString("operate");
                    tradeLogs.state = historyItem.optString("state");
                    tradeLogs.sum = historyItem.optString("sum");
                    tradeLogs.uid = historyItem.optString("uid");

                    tradeLogs.id = historyItem.optString("id");
                    tradeLogs.balance = historyItem.optString("balance");
                    tradeLogs.orderno = historyItem.optString("orderno");
                    tradeLogs.profit_arrive = historyItem.optString("profit_arrive");
                    tradeLogs.profit_time = historyItem.optString("profit_time");
                    tradeLogs.remark = historyItem.optString("remark");
                    tradeLogs.tradeacco = historyItem.optString("tradeacco");
                    tradeLogs.utime = historyItem.optString("utime");

                    if ("0".equals(tradeLogs.state) || "1".equals(tradeLogs.state)
                            || "2".equals(tradeLogs.state)) {
                        runningList.add(tradeLogs);
                    } else {
                        doneList.add(tradeLogs);
                    }
                }

                if (!IS_PULL_DOWN_TO_REFRESH) {
                    count++;
                }

                if (runningList.size() + doneList.size() == 0) {
                    defPage.setVisibility(View.VISIBLE);
                }

                initAdapter();

                FormatNumberData.simpleFormatNumber(
                        (float) (StringHelper.isBlank(data.optString("total_encash")
                                ) ? 0.00 : Double.parseDouble(data.optString("total_encash"))),
                        tvTotalR);
                FormatNumberData
                        .simpleFormatNumber(
                                (float) (StringHelper.isBlank(data.optString("total_quick_encash")
                                        ) ? 0.00 : Double.parseDouble(data
                                        .optString("total_quick_encash"))), tvTotalF);
                FormatNumberData.simpleFormatNumber(
                        (float) (StringHelper.isBlank(data.optString("total_recharge")
                                ) ? 0.00 : Double.parseDouble(data.optString("total_recharge"))),
                        tvTotalP);

                ptrsv.onRefreshComplete();
                dismissLoading();

            } else {
                dismissLoading();
                ptrsv.onRefreshComplete();
                showHintDialog(emsg);
            }

        } catch (JSONException e) {
            dismissLoading();
            ptrsv.onRefreshComplete();
            e.printStackTrace();
        }
    }

    private void initAdapter() {
        if (historyAdapter == null) {
            historyAdapter = new WalletHistoryAdapter(getActivity(), runningList, doneList);
            ptrsv.setAdapter(historyAdapter);
        } else {
            historyAdapter.notifyDataSetChanged();
        }
    }

}

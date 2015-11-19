package com.qianjing.finance.widget.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qianjing.finance.model.common.Card;
import com.qjautofinancial.R;


/**
 * <p>CardListAdapter</p>
 * <p>Description: 银行卡列表适配器</p>
 * @date 2015年2月5日
 */
public class QuickCardListAdapter2 extends BaseAdapter {
	
	private ArrayList<Card> mListData; 
	private LayoutInflater mInflater;  
	private ItemViewHolder holder;
	private String strSelectedBankId;
	
	public QuickCardListAdapter2(Context context, ArrayList<Card> listData, String strSelectedBankId) {
		this.mListData = listData ;
		this.strSelectedBankId = strSelectedBankId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView != null) {  
            holder = (ItemViewHolder) convertView.getTag();  
        } 
        else {  
           	convertView = mInflater.inflate(R.layout.item_bank2, null);  
           	holder = new ItemViewHolder();  
             
           	holder.iconBankImageView = (ImageView) convertView.findViewById(R.id.iv_image);
           	holder.nameBankTextView = (TextView) convertView.findViewById(R.id.tv_bankname);
           	holder.iv_check=(ImageView) convertView.findViewById(R.id.iv_check);
           
           	convertView.setTag(holder);  
        }
        Card card = mListData.get(position);
        String name = card.getBankName();
        int iconRid = getBankIconRid(getBankIdIndex(card.getBankCode()));
   		holder.iv_check.setVisibility(View.INVISIBLE);
        if (strSelectedBankId!=null && TextUtils.equals(strSelectedBankId, card.getBankCode())) {
       		holder.iv_check.setVisibility(View.VISIBLE);
        }
        holder.iconBankImageView.setImageResource(iconRid);
       	holder.nameBankTextView.setText(name);
		return convertView;
	}

    private class ItemViewHolder {  
		ImageView iconBankImageView;
		TextView nameBankTextView;
		ImageView iv_check;
    }  
    
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mListData.size();
	}

	/*
	 * 007招商银行   002工商银行   003农业银行   004中国银行  005建设银行 
	 * 008北京银行   920平安银行   006交通银行
	 */
	private String[] bankkeys = new String[] { 
			"007", "002", " 003", "004",
			"005", "008", "920", "011",
			"021", "016", "013", "012",
			"014", "017", "009", "006"};
	
	private int[] bankIdArray = new int[] { 
			R.drawable.zhaoshang,R.drawable.gongshang,R.drawable.nonghang,
			R.drawable.zhongguo, R.drawable.jianhang,R.drawable.beijing,
			R.drawable.pingan,R.drawable.pufa,R.drawable.xingye,
			R.drawable.guangfa,R.drawable.shenzhenfazhan,R.drawable.huaxia,
			R.drawable.minsheng,R.drawable.shanghai, R.drawable.guangda, 
			R.drawable.jiaotong, R.drawable.defult };
	
	private int getBankIdIndex(String idStr){
		for (int i = 0; i < bankkeys.length; i++) {
			if(bankkeys[i].endsWith(idStr)) return i;
		}
		return -1;
	}
	
	private int getBankIconRid(int bankIdIndex){
		if(bankIdIndex>=0 && bankIdIndex<bankIdArray.length)
			return bankIdArray[bankIdIndex];
		return bankIdArray[bankIdArray.length-1];
	}
	
}
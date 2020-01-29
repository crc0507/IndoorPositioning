package com.example.emma.qrcode_iteration2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jizhimeicrc on 17/7/12.
 */

public class ShopListAdapter extends BaseAdapter{
    private List<PoiData> mData;
    private Context mContext;

    public ShopListAdapter(List<PoiData> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.shop_item,parent,false);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView info = (TextView) convertView.findViewById(R.id.info);
        title.setText(mData.get(position).getTitle());
        //info.setText(mData.get(position).getText());
        return convertView;
    }

}

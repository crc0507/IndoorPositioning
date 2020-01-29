package com.example.emma.qrcode_iteration2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * Created by jizhimeicrc on 17/7/11.
 */

public class ShopList extends Activity  implements AdapterView.OnItemClickListener{
    List<PoiData> shopListdata = null;
    private ShopListAdapter shopListAdapter;
    private Context mContext = null;
    private ListView shop_list;
    static double shopJingdu,shopWeidu;
    static String text;
    static boolean isSortClicked;
    PoiData poiData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shop_list);
        shop_list = (ListView)findViewById(R.id.listView);
        shop_list.setOnItemClickListener(this);
        mContext = ShopList.this;
        isSortClicked=false;

        Intent intent = getIntent();
        shopListdata = (List<PoiData>)getIntent().getSerializableExtra("poiitemslist");

        shopListAdapter = new ShopListAdapter(shopListdata, mContext);

        shop_list.setAdapter(shopListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(mContext,"你点击了第" + position + "项",Toast.LENGTH_SHORT).show();
        isSortClicked=true;
        String shopname = shopListdata.get(position).getTitle();
        String address = shopListdata.get(position).getText();
        text=address;
        shopJingdu = shopListdata.get(position).getLatitude();
        shopWeidu = shopListdata.get(position).getLongitude();
        Intent intent = new Intent(ShopList.this, PlaceDescription_type.class);
        Bundle bundle = new Bundle();
        bundle.putString("type_shopname", shopname);
        bundle.putString("type_address", address);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static double getShopJingdu() {
        return shopJingdu;
    }

    public static void setShopJingdu(double shopJingdu) {
        ShopList.shopJingdu = shopJingdu;
    }

    public static double getShopWeidu() {
        return shopWeidu;
    }

    public static void setShopWeidu(double shopWeidu) {
        ShopList.shopWeidu = shopWeidu;
    }

    public static String getText() {
        return text;
    }

    public static void setText(String text) {
        ShopList.text = text;
    }

    public static boolean isSortClicked() {
        return isSortClicked;
    }

    public static void setIsSortClicked(boolean isSortClicked) {
        ShopList.isSortClicked = isSortClicked;
    }
}

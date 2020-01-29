package com.example.emma.qrcode_iteration2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Feedback extends AppCompatActivity {

    private EditText building;
    private EditText floor;
    private EditText telephone;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //更改显示文本颜色
        TextView location = (TextView) findViewById(R.id.location);
        SpannableStringBuilder style33 = new SpannableStringBuilder("所在位置");
        style33.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        location.setText(style33);

        TextView build = (TextView) findViewById(R.id.building);
        SpannableStringBuilder style = new SpannableStringBuilder("建筑物名称*");
        style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.RED), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        build.setText(style);

        TextView fl = (TextView) findViewById(R.id.floor);
        SpannableStringBuilder style1 = new SpannableStringBuilder("楼层*");
        style1.setSpan(new ForegroundColorSpan(Color.BLACK),0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style1.setSpan(new ForegroundColorSpan(Color.RED),2,3,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fl.setText(style1);

        TextView tele = (TextView) findViewById(R.id.tel);
        SpannableStringBuilder style2 = new SpannableStringBuilder("联系方式*");
        style2.setSpan(new ForegroundColorSpan(Color.BLACK),0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style2.setSpan(new ForegroundColorSpan(Color.RED),4,5,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tele.setText(style2);

        TextView descri = (TextView) findViewById(R.id.description);
        SpannableStringBuilder style3 = new SpannableStringBuilder("地点描述*");
        style3.setSpan(new ForegroundColorSpan(Color.BLACK),0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style3.setSpan(new ForegroundColorSpan(Color.RED),4,5,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descri.setText(style3);

        //点击按钮进入选址页面
        Button selectLocation = (Button) findViewById(R.id.selectLocation);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Feedback.this,selectPoint.class);
                startActivity(intent);
            }
        });

        String longitude;
        String latitude;
        //获得地点的经纬度
        Intent intent=getIntent();
        longitude= intent.getStringExtra("longitude");
        latitude=intent.getStringExtra("latitude");
        if (longitude==null||latitude==null)
        {
            longitude="116.519035";
            latitude="39.924548";
        }

        //点击按钮发送反馈信息给服务端，成功则进入优惠券界面
        Button upload = (Button) findViewById(R.id.upload);

        final String finalLatitude = latitude;
        final String finalLongitude = longitude;
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获得建筑物名称
                building=(EditText) findViewById(R.id.buildingInput);
                final String buildingInput = building.getText().toString();

                //获得楼层号
                floor = (EditText)findViewById(R.id.floorInput);
                final String floorInput = floor.getText().toString();

                //获得手机号
                telephone=(EditText)findViewById(R.id.telInput);
                final String telInput = telephone.getText().toString();

                //获得描述
                description=(EditText)findViewById(R.id.descripInput);
                final String descripInput = description.getText().toString();

                if (buildingInput.length() <= 0
                        || floorInput.length() <= 0
                        || telInput.length() <= 0
                        || descripInput.length() <= 0)
                {
                    Toast.makeText(Feedback.this, "请完成反馈信息的填写", Toast.LENGTH_LONG).show();
                }
                else{
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            final String urlPath = "http://10.8.176.105:8080/QRCodeAdmin/commAction.action";
                            URL url;

                            try {
                                url = new URL(urlPath);

                                JSONObject feedbackInfo = new JSONObject();
                                feedbackInfo.put("building", buildingInput);
                                feedbackInfo.put("floor", floorInput);
                                feedbackInfo.put("telephone", telInput);
                                feedbackInfo.put("description", descripInput);
                                feedbackInfo.put("latitude", finalLatitude);
                                feedbackInfo.put("longitude", finalLongitude);

                                /**
                                 *
                                 * 封装feedback数组
                                 使用JsonObject封装
                                 {"building":"","floor":"","telephone":"","description":"",
                                 "latitude":"","longitude":""}
                                 */
                                //将JSON数组转换成String类型使用输出流向服务器写
                                String content = String.valueOf(feedbackInfo);

                                //输出
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setConnectTimeout(5000);
                                conn.setDoOutput(true);//允许输出
                                conn.setRequestMethod("POST");
                                //conn.setRequestProperty("User-Agent", "Fiddler");
                                conn.setRequestProperty("Content-Type", "application/json");
                                OutputStream os = conn.getOutputStream();
                                os.write(content.getBytes());
                                os.close();

                                if (conn.getResponseCode() == 200) {
                                    //传回信息，信息应当为优惠券的信息
                                    InputStream is = conn.getInputStream();

                                    //下面的Json数据是{"id":"","path":""}的string形式
                                    String json = NetUtils.readString(is);
                                    awardBean award = new awardBean();
                                    JSONObject jsonObject = new JSONObject(json);

                                    //获得award的id和path
                                    String id = jsonObject.getString("id");
                                    String path = jsonObject.getString("path");

                                    //将获得的优惠券信息发送给award界面
                                    Intent intent1 = new Intent(Feedback.this, Award.class);
                                    intent1.putExtra("id", id);
                                    intent1.putExtra("path", path);
                                    startActivity(intent1);
                                } else {
                                    Toast.makeText(Feedback.this, "发送失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(Feedback.this, "发送失败", Toast.LENGTH_SHORT).show();
                            } catch (MalformedURLException e) {
                                Toast.makeText(Feedback.this, "发送失败", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(Feedback.this, "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.start();
                }
            }

        });


    }
}

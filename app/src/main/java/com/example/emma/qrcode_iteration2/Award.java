package com.example.emma.qrcode_iteration2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Award extends AppCompatActivity {

    private ImageView awardImg;
    private TextView awardID;
    private Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award);

        //获取并显示优惠券ID
        Intent intent = getIntent();
        awardID=(TextView)findViewById(R.id.awardID);
        String id = intent.getStringExtra("id");
        awardID.setText("ID:"+id);

        //获取并显示优惠券图片
        awardImg=(ImageView)findViewById(R.id.awardImg);
        String path = intent.getStringExtra("path");
        Bitmap bm= BitmapFactory.decodeFile(path);
        awardImg.setImageBitmap(bm);

        done = (Button)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处跳转界面应更改
                Intent intent = new Intent(Award.this,GeocoderActivity.class);
                startActivity(intent);
            }
        });

    }
}

package com.example.emma.qrcode_iteration2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sy.qrcodelibrary.interfaces.OnScanSucceedListener;
import com.sy.qrcodelibrary.utils.QRCodeUtil;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private TextView tv;
    private TextView tvResult;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
        tv = (TextView) findViewById(R.id.tv);
        tvResult = (TextView) findViewById(R.id.tv_result) ;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCodeUtil.scanCode(MainActivity.this, new OnScanSucceedListener() {
                    @Override
                    public void onScanSucceed(String resultString) {
                        //tv.setText(resultString);
                        result=resultString;
                        Intent intent=new Intent(MainActivity.this,RouteActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("result",resultString);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
//                Intent intent=new Intent(MainActivity.this,RouteActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("result",result);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
//        if (resultCode == RESULT_OK) {
//            Bundle bundle = data.getExtras();
//            String scanResult = bundle.getString("result");
//            tvResult.setText(scanResult);
//            Intent intent=new Intent(MainActivity.this,RouteActivity.class);
//            bundle.putString("result",scanResult);
//            startActivity(intent);
  //      }
    }
}

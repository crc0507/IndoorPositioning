package com.sy.qrcodelibrary.utils;

import android.content.Context;
import android.content.Intent;

import com.sy.qrcodelibrary.interfaces.OnScanSucceedListener;
import com.sy.qrcodelibrary.ui.QRCodeActivity;
public class QRCodeScanTask {

    private static QRCodeScanTask instance = null;//是否是final的不重要，因为最多只可能实例化一次。

    private QRCodeScanTask() {
    }

    public static QRCodeScanTask getInstance() {
        if (instance == null) {
            //双重检查加锁，只有在第一次实例化时，才启用同步机制，提高了性能。
            synchronized (QRCodeScanTask.class) {
                if (instance == null) {
                    instance = new QRCodeScanTask();
                }
            }
        }
        return instance;
    }

    private OnScanSucceedListener listener;

    public void QRCodeScan(Context context, OnScanSucceedListener listener) {
        this.listener = listener;
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public OnScanSucceedListener getOnScanSucceedListener() {
        return listener;
    }
}

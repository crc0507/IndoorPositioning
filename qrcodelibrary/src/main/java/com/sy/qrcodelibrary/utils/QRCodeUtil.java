package com.sy.qrcodelibrary.utils;

import android.content.Context;

import com.sy.qrcodelibrary.interfaces.OnScanSucceedListener;

public class QRCodeUtil {

    //扫描二维码
    public static void scanCode(Context context, OnScanSucceedListener listener) {
        QRCodeScanTask.getInstance().QRCodeScan(context, listener);
    }


}

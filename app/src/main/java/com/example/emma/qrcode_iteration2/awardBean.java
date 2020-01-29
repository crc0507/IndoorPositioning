package com.example.emma.qrcode_iteration2;

import java.io.Serializable;
/**
 * Created by Euphemia Xiao on 2017/7/13.
 */

public class awardBean implements Serializable{
    public String path;
    public String id;

    public awardBean()
    {
        this.path = path;
        this.id=id;
    }
    public String getPath()
    {
        return path;
    }

    public String getId()
    {
        return id;
    }
}

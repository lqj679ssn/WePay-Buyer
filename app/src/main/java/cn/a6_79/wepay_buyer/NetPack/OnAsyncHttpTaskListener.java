package cn.a6_79.wepay_buyer.NetPack;

import org.json.JSONException;

public interface OnAsyncHttpTaskListener {
    void callback(HttpTaskResponse httpTaskResponse) throws JSONException;
}

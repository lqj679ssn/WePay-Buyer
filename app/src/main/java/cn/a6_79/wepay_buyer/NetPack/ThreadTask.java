package cn.a6_79.wepay_buyer.NetPack;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

public class ThreadTask extends AsyncTask<Void, Integer, HttpTaskResponse> {
    private OnAsyncTaskListener listener;
    private HttpTaskRequest httpTaskRequest;
    public ThreadTask(HttpTaskRequest httpTaskRequest, OnAsyncTaskListener listener) {
        this.httpTaskRequest = httpTaskRequest;
        this.listener = listener;
    }

    @Override
    protected HttpTaskResponse doInBackground(Void... voids) {
        return HttpTask.htmlTask(httpTaskRequest);
    }

    @Override
    protected void onPostExecute(HttpTaskResponse httpTaskResponse) {
        super.onPostExecute(httpTaskResponse);
        if (isCancelled())
            return;
        if (listener != null) {
            try {
                listener.callback(httpTaskResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

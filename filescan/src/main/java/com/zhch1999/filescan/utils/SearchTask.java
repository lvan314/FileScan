package com.zhch1999.filescan.utils;

import android.os.AsyncTask;
import android.widget.TextView;


public class SearchTask extends AsyncTask {
    @Override
    protected void onPreExecute() {
        /**
         * 执行异步任务前操作
         */
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        /**
         * 工作线程执行的方法 不能调用ui操作
         */
        publishProgress();
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        /**
         * 可以在此方法内更新操作进度 需要调用publishProgress 方法才会执行
         */
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object o) {
        /**
         * 接收任务执行结果 进行ui操作
         * 工作在主线程，所以可以更新ui操作
         */
        super.onPostExecute(o);
    }
}

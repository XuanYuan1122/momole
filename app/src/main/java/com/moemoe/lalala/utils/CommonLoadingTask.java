package com.moemoe.lalala.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/5/1 0001.
 */
public class CommonLoadingTask extends AsyncTask<Void, Void, Object> {
    /**
     * progress dialog
     */
    private ProgressDialog prgDlg;
    /**
     * the android context
     */
    private Context mContext;
    /**
     * the TaskCallback interface to do in Task
     */
    private TaskCallback mCallback;
    /**
     * the progress dialog message content
     */
    private String mPrgMsg;

    /**
     * constructor for CommonLoadingTask
     *
     * @param context
     *            the android context
     * @param callback
     *            TaskCallback instance
     * @param prgMsg
     *            progress dialog message to show
     */
    public CommonLoadingTask(Context context, TaskCallback callback, String prgMsg) {
        mContext = context;
        mCallback = callback;
        mPrgMsg = prgMsg;
    }

    @Override
    protected void onPreExecute() {
        showProgressDlg();
    }

    @Override
    protected Object doInBackground(Void... params) {
        if (mCallback != null) {
           return mCallback.processDataInBackground();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        dismissProgressDlg();
        if (mCallback != null) {
            mCallback.handleData(result);
        }
    }

    /**
     * show progress dialog
     */
    private void showProgressDlg() {
        if (prgDlg == null) {
            prgDlg = new ProgressDialog(mContext);
            prgDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDlg.setCancelable(false);
            if (TextUtils.isEmpty(mPrgMsg)) {
                mPrgMsg = mContext.getString(R.string.a_global_msg_loading);
            }
            prgDlg.setMessage(mPrgMsg);
        }
        prgDlg.show();
    }

    /**
     * dismiss progress dialog
     */
    private void dismissProgressDlg() {
        if (prgDlg != null) {
            try {
                prgDlg.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * interface to be userd in doInBackground and onPostExecute
     *
     * @author Ben
     */
    public static interface TaskCallback {
        /**
         * this method will be call in doInBackground
         *
         * @return parameters in Object
         */
        public Object processDataInBackground();

        /**
         * this method will be call in onPostExecute
         *
         * @param
         *            parameters returned by processDataInBackground
         */
        public void handleData(Object o);
    }
}

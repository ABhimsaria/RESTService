package com.example.android.restservice.webServices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.android.restservice.Constants;
import com.example.android.restservice.R;

import org.json.JSONObject;

/**
 * Created by ABhimsaria on 6/1/2016.
 */
public abstract class WebServiceTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = WebServiceTask.class.getSimpleName();
    public abstract void showProgress();

    public abstract boolean performRequest();

    public abstract void performSuccesfulOperation();

    public abstract void hideProgress();

    private String mMessage;
    private Context mContext;

    public WebServiceTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        showProgress();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(!WebServicesUtils.hasInternetConnection(mContext)){
            mMessage = Constants.CONNECTION_MESSAGE;
            return false;
        }
        return performRequest();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        hideProgress();
        if(success){
            performSuccesfulOperation();
        }
        if(mMessage != null && !mMessage.isEmpty()){
            Toast.makeText(mContext,mMessage,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled(Boolean b) {
        hideProgress();
    }
    public boolean hasError(JSONObject obj){
        if(obj != null){
            int status = obj.optInt(Constants.STATUS);
            Log.d(TAG, "Response: " + obj.toString());
            mMessage = obj.optString(Constants.MESSAGE);
            if (status == Constants.STATUS_ERROR || status == Constants.STATUS_UNAUTHORIZED){
                return  true;
            }else {
                return false;
            }
        }
        mMessage = mContext.getString(R.string.error_url_not_found);
        return true;
    }



}

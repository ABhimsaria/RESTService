package com.example.android.restservice;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.restservice.data.User;
import com.example.android.restservice.webServices.WebServiceTask;
import com.example.android.restservice.webServices.WebServicesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private UserInfoTask mUserInfoTask = null;
    private UserEditTask mUserEditTask = null;
    private UserResetTask mUserResetTask = null;
    private UserDeleteTask mUserDeleteTask = null;
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mPhoneNoText;
    private EditText mNoteText;
    private EditText mNameText;

    private interface ConfirmationListener{
        void onConfirmation(boolean isConfirmed);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiViews();
        showProgress(true);
        mUserInfoTask = new UserInfoTask();
        mUserInfoTask.execute();
    }

    private void intiViews(){
        mEmailText = (EditText) findViewById(R.id.email);
        mPasswordText = (EditText) findViewById(R.id.password);
        mNameText = (EditText) findViewById(R.id.name);
        mPhoneNoText = (EditText) findViewById(R.id.phoneNumber);
        mNoteText = (EditText) findViewById(R.id.note);
    }

    private void showProgress(final boolean isShow){
        findViewById(R.id.progress).setVisibility(isShow ? View.VISIBLE : View.GONE);
        findViewById(R.id.info_form).setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    public void clickUpdateButton(View view){
        if(mPasswordText.getText().toString().trim().length()>=5){
            showProgress(true);
            mUserEditTask = new UserEditTask();
            mUserEditTask.execute();
        }else {
            Toast.makeText(this,R.string.error_password_length,Toast.LENGTH_LONG).show();
            }
    }

    public void clickDeleteButton(View view){
        showConfigurationDialog(new ConfirmationListener(){
           @Override
            public void onConfirmation(boolean isConfirmed){
               if(isConfirmed){
                   showProgress(true);
                   mUserDeleteTask = new UserDeleteTask();
                   mUserDeleteTask.execute();
               }
           }
        });
    }

    private void showConfigurationDialog(final ConfirmationListener confirmationListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are You Sure ? Once done can not be Undone");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmationListener.onConfirmation(true);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmationListener.onConfirmation(false);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public void clickResetButton(View view){
        showConfigurationDialog(new ConfirmationListener() {
            @Override
            public void onConfirmation(boolean isConfirmed) {
                if(isConfirmed){
                    showProgress(true);
                    mUserResetTask = new UserResetTask();
                    mUserResetTask.execute();
                }
            }
        });
    }

    public void clickSignOutButton(View view){
        showLoginScreen();
    }

    private void showLoginScreen(){
        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void populateText(){
        User user = RESTServiceApplication.getInstance().getUser();
        mEmailText.setText(user.getEmail());
        mPasswordText.setText(user.getPassword());
        mNameText.setText(user.getName() == null ? "" : user.getName());
        mPhoneNoText.setText(user.getPhoneNumber() == null ? "" : user.getPhoneNumber());
        mNoteText.setText(user.getNote() == null ? "" : user.getNote());
    }

    private abstract class ActivityWebServiceTask extends WebServiceTask{
        public ActivityWebServiceTask(WebServiceTask webServiceTask) {
            super(MainActivity.this);
        }

        @Override
        public void performSuccesfulOperation() {
            populateText();
        }

        @Override
        public void showProgress() {
            MainActivity.this.showProgress(true);
        }

        @Override
        public void hideProgress() {
            MainActivity.this.showProgress(false);
        }
    }

    private class UserInfoTask extends ActivityWebServiceTask{
        public UserInfoTask() {
            super(mUserInfoTask);
        }
        public boolean performRequest(){
            ContentValues contentValues = new ContentValues();
            User user = RESTServiceApplication.getInstance().getUser();
            contentValues.put(Constants.ID, user.getId());
            contentValues.put(Constants.ACCESS_TOKEN,
                    RESTServiceApplication.getInstance().getAccessToken());

            JSONObject obj = WebServicesUtils.requestJSONObject(Constants.INFO_URL,
                    WebServicesUtils.METHOD.GET, contentValues, null);
            if (!hasError(obj)){
                JSONArray jsonArray = obj.optJSONArray(Constants.INFO);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                user.setName(jsonObject.optString(Constants.NAME));
                if(user.getName().equalsIgnoreCase("null")){
                    user.setName(null);
                }

                user.setPhoneNumber(jsonObject.optString(Constants.PHONE_NUMBER));
                if(user.getPhoneNumber().equalsIgnoreCase("null")){
                    user.setPhoneNumber(null);
                }

                user.setNote(jsonObject.optString(Constants.NOTE));
                if(user.getNote().equalsIgnoreCase("null")){
                    user.setNote(null);
                }
                user.setId(jsonObject.optLong(Constants.ID_INFO));
                return true;
            }
            return false;
        }
    }

    private class UserEditTask extends ActivityWebServiceTask{
        public UserEditTask() {
            super(mUserEditTask);
        }
        public boolean performRequest(){
            ContentValues contentValues = new ContentValues();
            User user = RESTServiceApplication.getInstance().getUser();
            contentValues.put(Constants.ID, user.getId());
            contentValues.put(Constants.NAME,mNameText.getText().toString());
            contentValues.put(Constants.PASSWORD, mPasswordText.getText().toString());
            contentValues.put(Constants.PHONE_NUMBER, mPhoneNoText.getText().toString());
            contentValues.put(Constants.NOTE, mNoteText.getText().toString());

            ContentValues urlValues = new ContentValues();
            urlValues.put(Constants.ACCESS_TOKEN, RESTServiceApplication.getInstance().getAccessToken());

            JSONObject obj = WebServicesUtils.requestJSONObject(Constants.UPDATE_URL,
                    WebServicesUtils.METHOD.POST, urlValues, contentValues);
            if(!hasError(obj)){
                JSONArray jsonArray = obj.optJSONArray(Constants.INFO);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                user.setName(jsonObject.optString(Constants.NAME));
                user.setPhoneNumber(jsonObject.optString(Constants.PHONE_NUMBER));
                user.setNote(jsonObject.optString(Constants.NOTE));
                user.setPassword(jsonObject.optString(Constants.PASSWORD));
                return true;
            }
            return false;
        }
    }

    private class UserResetTask extends ActivityWebServiceTask{
        public UserResetTask() {
            super(mUserResetTask);
        }
        public boolean performRequest()
        {
            ContentValues contentValues = new ContentValues();
            User user = RESTServiceApplication.getInstance().getUser();
            contentValues.put(Constants.ID, user.getId());
            contentValues.put(Constants.ACCESS_TOKEN,
                    RESTServiceApplication.getInstance().getAccessToken());
            JSONObject obj = WebServicesUtils.requestJSONObject(Constants.RESET_URL, WebServicesUtils.METHOD.POST, contentValues,null);
            if(!hasError(obj)){
                user.setName("");
                user.setPhoneNumber("");
                user.setNote("");
                return true;
            }
            return false;
        }
    }

    private class UserDeleteTask extends ActivityWebServiceTask{
        public UserDeleteTask() {
            super(mUserDeleteTask);
        }

        @Override
        public void performSuccesfulOperation() {
            showLoginScreen();
        }

        public boolean performRequest(){
            ContentValues contentValues = new ContentValues();
            User user = RESTServiceApplication.getInstance().getUser();
            contentValues.put(Constants.ID, user.getId());
            contentValues.put(Constants.ACCESS_TOKEN, RESTServiceApplication.getInstance().getAccessToken());
            JSONObject obj = WebServicesUtils.requestJSONObject(Constants.DELETE_URL, WebServicesUtils.METHOD.DELETE, contentValues, null);
            if(!hasError(obj)){
                RESTServiceApplication.getInstance().setUser(null);
                return true;
            }
            return false;
        }
    }
}

package com.example.android.restservice;

/**
 * Created by ABhimsaria on 5/22/2016.
 */
public class Constants {

    //Constants for web Connection.
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;
    public static final int STATUS_ERROR = 400;
    public static final int STATUS_UNAUTHORIZED = 401;

    //Application key and Secret Key that comes from the Server
    // to access the API.
    public static final String APP_KEY = "0deb5eeff03e41d0b4e7f0ff73589d7d";
    public static final String APP_SECRET = "eb7b16f26175497fb8e40ebbf7942b2e";

    //URL's to be used to access the API
    public static final String END_POINT = "http://www.beteljuiceproductions.com/api";
    public static final String LOGIN_URL = END_POINT + "/login.php";
    public static final String SIGNUP_URL = END_POINT + "/signup.php";
    public static final String INFO_URL = END_POINT + "/info.php";
    public static final String UPDATE_URL = END_POINT + "/update.php";
    public static final String DELETE_URL = END_POINT + "/delete.php";
    public static final String RESET_URL = END_POINT + "/reset.php";

    //Constants used in JSON Parsing or values attached to url server.
    public static final String AUTHORIZATION = "authorization";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCESS = "access";
    public static final String INFO = "info";
    public static final String STATUS = "status";
    public static final String MESSAGE = "msg";
    public static final String ID = "id";
    public static final String ID_INFO = "ID";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String NOTE = "note";
    public static final String NAME="name";

    public static final String CONNECTION_MESSAGE = "No Internet Connection";
}

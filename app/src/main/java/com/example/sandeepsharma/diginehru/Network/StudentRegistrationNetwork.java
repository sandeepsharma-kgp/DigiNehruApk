package com.example.sandeepsharma.diginehru.Network;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.volley.VolleyError;
import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;
import com.example.sandeepsharma.diginehru.Frameworks.JSONResponseParser;
import com.example.sandeepsharma.diginehru.Frameworks.NetworkCallBack;
import com.example.sandeepsharma.diginehru.Frameworks.Networks.VolleyPostRequest;
import com.example.sandeepsharma.diginehru.Frameworks.Networks.VolleyRequestQueue;
import com.example.sandeepsharma.diginehru.Frameworks.Response;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by sandeepsharma on 30/06/17.
 */

public class StudentRegistrationNetwork implements com.android.volley.Response.Listener, com.android.volley.Response.ErrorListener {
    Activity mActivity;
    int mEventType;

    Response response;
    NetworkCallBack callBack;
    ProgressDialog progressDialog;
    String mName, mRoll, mRoom, mEmail, mMobile, mPassword;

    public  StudentRegistrationNetwork(Activity activity, int eventType, String name, String roll, String room, String email, String mobile, String password) {
        mActivity = activity;
        mEventType = eventType;
        mName = name;
        mRoll = roll;
        mRoom = room;
        mEmail = email;
        mMobile = mobile;
        mPassword = password;
        callBack = (NetworkCallBack) activity;
        progressDialog = ProgressDialog.show(activity, "Please wait", null, false);
        VolleyRequestQueue.getInstance(activity).addToRequestQueue(new VolleyPostRequest(this, AppConstants.MAIN_URL+"student/studentregister/", getRequestMap(), activity));
    }

    private HashMap<String, String> getRequestMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", mName);
        map.put("roll", mRoll);
        map.put("room", mRoom);
        map.put("email", mEmail);
        map.put("mobile", mMobile);
        map.put("password", mPassword);
        return map;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        response = new Response();
        response.setReponseCode(400);
        try {
            String s=new String(error.networkResponse.data,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callBack.setResult(response, mEventType);
    }

    @Override
    public void onResponse(Object resp) {
        try {
            response = new JSONResponseParser().serialize((String) resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.setReponseCode(200);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callBack.setResult(response, mEventType);
    }
}

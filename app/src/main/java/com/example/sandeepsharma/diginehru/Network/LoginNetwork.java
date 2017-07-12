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

import java.util.HashMap;

/**
 * Created by sandeepsharma on 10/07/17.
 */

public class LoginNetwork implements com.android.volley.Response.Listener, com.android.volley.Response.ErrorListener {
    Activity mActivity;
    int mEventType;

    Response response;
    NetworkCallBack callBack;
    ProgressDialog progressDialog;
    String mUserId, mPassword;
    boolean mStudentType;

    public LoginNetwork(Activity activity, int eventType, String user_id, String password, boolean type) {
        mActivity = activity;
        mEventType = eventType;
        mUserId = user_id;
        mPassword = password;
        mStudentType = type;
        callBack = (NetworkCallBack) activity;
        progressDialog = ProgressDialog.show(activity, "Please wait", null, false);
        if (type) {
            VolleyRequestQueue.getInstance(activity).addToRequestQueue(new VolleyPostRequest(this, AppConstants.MAIN_URL + "student/studentlogin/", getRequestMap(), activity));
        } else {
            VolleyRequestQueue.getInstance(activity).addToRequestQueue(new VolleyPostRequest(this, AppConstants.MAIN_URL + "staff/stafflogin/", getRequestMap(), activity));
        }
    }

    private HashMap<String, String> getRequestMap() {
        HashMap<String, String> map = new HashMap<>();
        if (mStudentType) {
            map.put("roll", mUserId);
        } else {
            map.put("empid", mUserId);

        }

        map.put("password", mPassword);
        return map;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        response = new Response();
        response.setReponseCode(400);
        try {
            String s = new String(error.networkResponse.data, "UTF-8");
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
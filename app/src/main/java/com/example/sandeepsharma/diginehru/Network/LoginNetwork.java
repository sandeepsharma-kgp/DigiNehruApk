package com.example.sandeepsharma.diginehru.Network;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.provider.Settings.Secure;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;
import com.example.sandeepsharma.diginehru.Frameworks.JSONResponseParser;
import com.example.sandeepsharma.diginehru.Frameworks.NetworkCallBack;
import com.example.sandeepsharma.diginehru.Frameworks.Networks.VolleyPostRequest;
import com.example.sandeepsharma.diginehru.Frameworks.Networks.VolleyRequestQueue;
import com.example.sandeepsharma.diginehru.Frameworks.Response;
import com.example.sandeepsharma.diginehru.R;

import org.json.JSONException;

import java.util.HashMap;

import static java.security.AccessController.getContext;

/**
 * Created by sandeepsharma on 10/07/17.
 */

public class LoginNetwork implements com.android.volley.Response.Listener, com.android.volley.Response.ErrorListener {
    Activity mActivity;
    int mEventType;

    Response response;
    NetworkCallBack callBack;
    ProgressDialog progressDialog;
    String mToken ;
    boolean mMealType;
    AlertDialog.Builder mAlertDialog_Builder;
    String android_id;

    public LoginNetwork(Activity activity, int eventType, String token, boolean type) {
        mActivity = activity;
        mEventType = eventType;
        mToken = token;
        mMealType = type;
        android_id = Secure.getString(mActivity.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);
        callBack = (NetworkCallBack) activity;
        progressDialog = ProgressDialog.show(activity, "Please wait", null, false);
        VolleyRequestQueue.getInstance(activity).addToRequestQueue(new VolleyPostRequest(this, AppConstants.MAIN_URL + "studentmeal/mealcount/", getRequestMap(), activity));
    }

    private HashMap<String, String> getRequestMap() {
        HashMap<String, String> map = new HashMap<>();
        if (mMealType) {
            map.put("vn", "VE");
        } else {
            map.put("vn", "NV");
        }
        map.put("id", mToken);
        map.put("d_id", android_id);
        return map;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mAlertDialog_Builder = new AlertDialog.Builder(mActivity);
        LayoutInflater myLayout = LayoutInflater.from(mActivity);
        View dialogView = myLayout.inflate(R.layout.dialog_no, null);
        TextView messageView = (TextView)dialogView.findViewById(R.id.textView);
        final AlertDialog alertDialog = mAlertDialog_Builder.create();
        alertDialog.setView(dialogView);
        response = new Response();
        response.setReponseCode(400);
        try {
            String s = new String(error.networkResponse.data, "UTF-8");
            response = new JSONResponseParser().serialize(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//            Toast.makeText(mActivity.getApplicationContext(), "This is my Toast message!", Toast.LENGTH_LONG).show();
//            alertDialog.setTitle("No Network");
//            alertDialog.setMessage("No Network");
            messageView.setText("No Network");
        }
//        } else if (error instanceof AuthFailureError) {
//            AlertDialog alertDialog = mAlertDialog_Builder.create();
//            alertDialog.setView(dialogView);
////            alertDialog.setTitle("Select The Directory");
//            alertDialog.show();
        else {

            messageView.setText(response.getErrorText());
        }
        alertDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                alertDialog.dismiss();
            }
        }, 5000);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callBack.setResult(response, mEventType);
    }

    @Override
    public void onResponse(Object resp) {
        mAlertDialog_Builder = new AlertDialog.Builder(mActivity);
        LayoutInflater myLayout = LayoutInflater.from(mActivity);
        View dialogView = myLayout.inflate(R.layout.dialog_yes, null);
        final AlertDialog alertDialog = mAlertDialog_Builder.create();
        TextView messageView = (TextView)dialogView.findViewById(R.id.textView);
        alertDialog.setView(dialogView);

        try {
            response = new JSONResponseParser().serialize((String) resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.setReponseCode(200);
        messageView.setText(response.getErrorText());
        alertDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                alertDialog.dismiss();
            }
        }, 5000);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callBack.setResult(response, mEventType);
    }
}
package com.example.sandeepsharma.diginehru.Frameworks.Networks;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sandeepgautam on 23/07/16.
 */
public class VolleyPostRequest extends Request<NetworkResponse> {
    private final Response.Listener<Object> mListener;
    private HashMap<String, String> mParams;
    private Application mApplication;


    public VolleyPostRequest(Response.Listener listener, String url, HashMap<String, String> params, Activity activity) {
        super(Method.POST, url, (Response.ErrorListener) listener);
        mListener = listener;
        mParams = params;
        mApplication = activity.getApplication();

        setRetryPolicy(new DefaultRetryPolicy(300000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public VolleyPostRequest(Response.Listener listener, String url, HashMap<String, String> params, Application application) {
        super(Method.POST, url, (Response.ErrorListener) listener);
        mListener = listener;
        mParams = params;
        mApplication = application;

        setRetryPolicy(new DefaultRetryPolicy(300000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse networkResponse) {

        mListener.onResponse(new String(networkResponse.data));
    }

}
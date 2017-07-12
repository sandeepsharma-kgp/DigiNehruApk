package com.example.sandeepsharma.diginehru.Frameworks.Networks;

import com.android.volley.AuthFailureError;
        import com.android.volley.DefaultRetryPolicy;
        import com.android.volley.NetworkResponse;
        import com.android.volley.ParseError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.toolbox.HttpHeaderParser;

        import java.io.UnsupportedEncodingException;
        import java.net.URLEncoder;
        import java.util.HashMap;
        import java.util.Map;

/**
 * Created by sandeepgautam on 23/07/16.
 */
public class VolleyGetRequest extends Request<NetworkResponse> {
    private final Response.Listener<Object> mListener;
    private HashMap<String, String> mParams;
    private Map<String, String> mHeaders;
    private String mUrl;

    public VolleyGetRequest(Response.Listener listner, String url, HashMap<String, String> params, Map<String, String> headers) {
        super(Method.GET, url, (Response.ErrorListener) listner);
        mListener = listner;
        mParams = params;
        mUrl = url;
        mHeaders = headers;

        setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        /*HashMap<String, String> headers = new HashMap<>();

        if (((EverythingDotMe) mActivity).

                mSharedPreferences.
                getString(AppConstants.RESPONSE_CID, null) == null) {

            return super.getHeaders();
        } else {
            headers.put("HAPPAY-CID", ((EverythingDotMe) mActivity).
                    mSharedPreferences.
                    getString(AppConstants.RESPONSE_CID, ""));
            headers.put("HAPPAY-TOKEN", ((EverythingDotMe) mActivity).
                    mSharedPreferences.
                    getString(AppConstants.RESPONSE_TOKEN, ""));

            return headers;
        }*/

        return mHeaders;
    }

    @Override
    public String getUrl() {
        try {
            return mUrl + getRequestString(mParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mUrl;
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

        String response = new String(networkResponse.data);

        mListener.onResponse(response);

    }

    private String getRequestString(HashMap<String, String> params) throws UnsupportedEncodingException {
        if(params != null) {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                    result.append("?");
                } else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        return "";
    }
}
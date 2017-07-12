package com.example.sandeepsharma.diginehru.Frameworks.Networks;

        import android.app.Activity;
        import android.app.Application;
        import android.graphics.Bitmap;
        import android.util.Log;

        import com.android.volley.AuthFailureError;
        import com.android.volley.DefaultRetryPolicy;
        import com.android.volley.NetworkResponse;
        import com.android.volley.ParseError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyLog;
        import com.android.volley.toolbox.HttpHeaderParser;
        import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;
        import com.example.sandeepsharma.diginehru.Utils.ProjectUtil;

        import org.apache.http.entity.mime.MultipartEntity;
        import org.apache.http.entity.mime.content.ByteArrayBody;
        import org.apache.http.entity.mime.content.FileBody;
        import org.apache.http.entity.mime.content.StringBody;
        import org.json.JSONArray;
        import org.json.JSONException;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.IOException;
        import java.io.UnsupportedEncodingException;
        import java.nio.charset.Charset;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

        import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sandeepgautam on 15/03/16.
 */
public class MultipartBinaryRequest extends Request<NetworkResponse> {

    private MultipartEntity entity = new MultipartEntity();

    private final Response.Listener<String> mListener;
    private HashMap<String, String> mParams;
    private String mFileName;
    private String mFile;
    String[] mUrlLIdArray;
    private JSONArray mFileArray;
    Activity mActivity;
    Application mApplication;

    private String TAG = "MultipartBinaryRequest";


    public MultipartBinaryRequest(Response.Listener listner, String url, HashMap<String, String> params, String fileName, String file, Activity activity) {
        super(Method.POST, url, (Response.ErrorListener) listner);
        mListener = listner;
        mParams = params;
        mFileName = fileName;
        mFile = file;
        mActivity = activity;
        mApplication = activity.getApplication();

        setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        buildMultipartEntity();
    }

    public MultipartBinaryRequest(Response.Listener listner, String url, HashMap<String, String> params, ArrayList<String> bills, String fileName, Activity activity) {
        super(Method.POST, url, (Response.ErrorListener) listner);
        mListener = listner;
        mParams = params;
        mFileName = fileName;
        mActivity = activity;
        mApplication =  activity.getApplication();

        setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            buildListMultipartEntity(bills);

    }

    public MultipartBinaryRequest(Response.Listener listner, String url, HashMap<String, String> params, ArrayList<String> bills, String fileName, Application application) {
        super(Method.POST, url, (Response.ErrorListener) listner);
        mListener = listner;
        mParams = params;
        mFileName = fileName;
        mApplication =application;

        setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        buildListMultipartEntity(bills);
    }

   /* public MultipartBinaryRequest(Response.Listener listner, String url, HashMap<String, String> params, String fileName, ArrayList<ImageModel> bills) {
        super(Method.POST, url, (Response.ErrorListener)listner);
        mListener = listner;
        mParams = params;
        mFileName = fileName;

        setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        buildArrayMultipartEntity(bills);
    }*/

    public MultipartBinaryRequest(Response.Listener listner, String url, HashMap<String, String> params, String fileName, JSONArray fileArray, String[] lIdArray, Activity activity) {
        super(Method.POST, url, (Response.ErrorListener) listner);
        mListener = listner;
        mParams = params;
        mFileName = fileName;
        mFileArray = fileArray;
        mUrlLIdArray = lIdArray;
        mActivity = activity;
        mApplication =activity.getApplication();

        setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        buildArrayMultipartEntity();
    }

    private void buildMultipartEntity() {
        if (mFile != null && !mFile.isEmpty()) {

            entity.addPart(mFileName, new FileBody(new File(mFile)));
        }

        try {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                entity.addPart(entry.getKey(), new StringBody(entry.getValue() == null ? "" : entry.getValue(), Charset.forName("UTF-8")));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    private void buildArrayMultipartEntity() {
        if (mFileArray != null && mFileArray.length() > 0) {
            for (int i = 0; i < mFileArray.length(); i++) {
                /*if( i == 0 && type != 0) {
                    try {
                        addBillByteArray(mUrlLIdArray[i], mFileArray.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {

                    try {
                        addBillByteArray(mUrlLIdArray[i], mFileArray.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
                try {
                    addBillByteArray(mUrlLIdArray[i], mFileArray.getString(i));
                } catch (JSONException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                } catch (NullPointerException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        try {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                entity.addPart(entry.getKey(), new StringBody(entry.getValue() == null ? "" : entry.getValue(), Charset.forName("UTF-8")));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    public void attachExtraAttachments(JSONArray attachmentsArray) {
        if (attachmentsArray != null && attachmentsArray.length() > 0) {
            for (int i = 0; i < attachmentsArray.length(); i++) {
                try {
                    addBillByteArray(attachmentsArray.getString(i), attachmentsArray.getString(i));
                } catch (JSONException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                } catch (NullPointerException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

  /*  private void buildArrayMultipartEntity(ArrayList<ImageModel> bills) {
        if(bills != null) {
            for(int i = 0; i< bills.size(); i++) {
                entity.addPart(bills.get(i).getId(), new FileBody(new File(bills.get(i).getLocalUrl())));

            }
        }

        try {
            for(Map.Entry<String, String> entry : mParams.entrySet()) {
                entity.addPart(entry.getKey(), new StringBody(entry.getValue() == null ? "": entry.getValue()));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }*/

    private void buildListMultipartEntity(ArrayList<String> bills) {
        if (bills != null) {
            StringBuilder keys = new StringBuilder();
            for (int i = 0; i < bills.size(); i++) {
                keys.append(mFileName + "-" + i + (i == bills.size() - 1 ? "" : ","));
                entity.addPart(mFileName + "-" + i, new FileBody(new File(bills.get(i))));

            }

            try {
                entity.addPart("txn_attachments", new StringBody(keys.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                entity.addPart(entry.getKey(), new StringBody(entry.getValue() == null ? "" : entry.getValue(), Charset.forName("UTF-8")));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    private void buildListMultipartEntityForMoneyRequest(ArrayList<String> bills) {
        if (bills != null) {
            StringBuilder keys = new StringBuilder();
            for (int i = 0; i < bills.size(); i++) {
                keys.append(mFileName + "-" + i + (i == bills.size() - 1 ? "" : ","));
                entity.addPart(mFileName, new FileBody(new File(bills.get(i))));

            }

            try {
                entity.addPart("txn_attachments", new StringBody(keys.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                entity.addPart(entry.getKey(), new StringBody(entry.getValue() == null ? "" : entry.getValue(), Charset.forName("UTF-8")));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
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
        int responseCode = networkResponse.statusCode;
        String response = "";
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            response = new String(networkResponse.data);

        } else if (responseCode == 503 || responseCode == 502) {

            response = new String(networkResponse.data);

        } else if (responseCode == 500) {

            response = new String(networkResponse.data);
        } else {
            response = new String(networkResponse.data);
        }

        mListener.onResponse(response);
    }

    private void addBillByteArray(String fileName, String file) {
        if (fileName != null && file != null) {

            if (file.endsWith("pdf")) {

                entity.addPart(fileName, new FileBody(new File(file)));

            } else {

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ProjectUtil.getSmallBitmap(file, AppConstants.BILL_WIDTH, AppConstants.BILL_HEIGHT)
                        .compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();

                entity.addPart(fileName, new ByteArrayBody(data, file));

            }
        }
    }

}
package com.example.sandeepsharma.diginehru.Frameworks;

/**
 * Created by sandeepsharma on 30/06/17.
 */


import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONResponseParser {
    Response mResponse;

    public Response serialize(String response) throws JSONException {
        JSONObject resObject = new JSONObject(response);

        JSONObject resDet = new JSONObject();
        if (resObject.has(AppConstants.RES_DET)) {
            resDet = resObject.getJSONObject(AppConstants.RES_DET);
        }
        String resStr = "";

        if (resObject.has(AppConstants.RES_STR)) {
            resStr = resObject.getString(AppConstants.RES_STR);
        }

        mResponse = new Response();
        mResponse.setReponseText(resDet.toString());
        mResponse.setErrorText(resStr);

        return mResponse;
    }

    public Response arraySerialize(String response) throws JSONException {
        JSONObject resObject = new JSONObject(response);
        JSONArray resDet;
        if (resObject.getString(AppConstants.RES_DET).equalsIgnoreCase("[]")) {
            resDet = new JSONArray();
        } else {
            resDet = resObject.getJSONArray(AppConstants.RES_DET);
        }
        String resStr = resObject.getString(AppConstants.RES_STR);
//        String resCode = resObject.getString(AppConstants.RES_CODE);

        mResponse = new Response();
        mResponse.setReponseText(resDet.toString());
        mResponse.setErrorText(resStr);

        return mResponse;
    }

}

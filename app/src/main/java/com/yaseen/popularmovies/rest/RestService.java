package com.yaseen.popularmovies.rest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Pasonet on 18-02-2016.
 */
public class RestService extends IntentService {

    public static final String TAG = RestService.class.getName();


    public RestService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras == null || !extras.containsKey(RestApi.EXTRA_ACTION)) {
            Log.e(TAG, "Missing data with the Intent");
            return;
        }

        String REST_ACTION=extras.getString(RestApi.EXTRA_ACTION);

        HashMap params = (HashMap) extras.get(RestApi.EXTRA_PARAMS);
        String requestURL=(String)extras.get(RestApi.EXTRA_URL);

        String response = "";
        int statusCode = RestApi.INVALID_ACTION;

        try {
            String urlwithparams = requestURL+"?"+ getExtraParams(params);

            Log.d(TAG,urlwithparams);

            URL url = new URL(urlwithparams);
            Log.d(TAG, requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            statusCode = conn.getResponseCode();

            if (statusCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                br.close();
            } else {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                br.close();
            }
            BroadcastResponse(statusCode, response,REST_ACTION);
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void BroadcastResponse(int statusCode, String responseString,String extraAction) {
//        Log.v(TAG, "Sending broadcast for:" + ProcessorFactory.getActionFilter(action));
        Intent intent = new Intent(extraAction);
        intent.putExtra(RestApi.EXTRA_RESPONSE_CODE, statusCode);
        intent.putExtra(RestApi.EXTRA_RESPONSE_DATA, responseString);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String getExtraParams(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

}

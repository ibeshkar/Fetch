package com.invcode.vv0z.fetch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class       : Fetch
 * Version     : 1.1.0
 * Description : Use for download or upload data from/to server
 * Coder      : vv0z <i.beshkar@gmail.com>
 */
public class Fetch {

    private static final String TAG = "Fetch";
    private Context mContext;
    private String url;
    private Method method;
    private Map<String, String> mParams;
    private Map<String, String> mHeader;
    private Response response = new Response();
    private CookieManager cookieManager;
    private boolean cache;
    private boolean cookie;


    Fetch(RequestBuilder builder) {
        this.mContext = builder.getContext();
        this.url = builder.getUrl();
        this.method = builder.getMethod();
        this.mParams = builder.getParams();
        this.mHeader = builder.getHeader();
        this.cookie = builder.isCookie();
        this.cache = builder.isCache();
        // Initialize cookie manager
        this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    /**
     * Check net available
     */
    @SuppressWarnings("ConstantConditions")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Start connection process
     */
    @SuppressLint("StaticFieldLeak")
    public void start(final IResponse iResponse) {

        if (isNetworkAvailable()) {

            // Start request process and get response from server
            new AsyncTask<Void, Void, Response>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    // Add cookie to request
                    if (cookie) {
                        setCookie();
                    }

                    // Add header properties if required
                    if (method.equals(Method.GET)) {
                        url = getGetDataString((HashMap<String, String>) mParams);
                    }

                }

                @Override
                protected Response doInBackground(Void... voids) {

                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) (new URL(url)).openConnection();
                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(10000);
                        connection.setRequestMethod(method.name());
                        connection.setDefaultUseCaches(cache);
                        connection.setUseCaches(cache);

                        // Attach header properties
                        for (int i = 0; i < mHeader.size(); i++) {
                            connection.setRequestProperty(
                                    mHeader.keySet().toArray()[i].toString(),
                                    mHeader.values().toArray()[i].toString()
                            );
                        }

                        // POST method settings
                        if (method.equals(Method.POST)) {
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            OutputStream os = connection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(getPostDataString((HashMap<String, String>) mParams));
                            writer.flush();
                            writer.close();
                            os.close();
                        }

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {

                            // Save cookies if required
                            if (cookie) {
                                saveCookie();
                            }

                            BufferedReader br = new BufferedReader(new InputStreamReader(
                                    connection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String resp;
                            while ((resp = br.readLine()) != null) {
                                sb.append(resp);
                            }
                            response.setSuccess(sb.toString());
                        } else {
                            response.setFailed(FetchException.RESPONSE);
                        }
                    } catch (Exception e) {
                        response.setFailed(e.getMessage());
                    } finally {
                        if (connection != null)
                            connection.disconnect();
                    }
                    return response;
                }

                @Override
                protected void onPostExecute(Response response) {
                    super.onPostExecute(response);
                    if(response != null){
                    if (!response.getSuccess().isEmpty())
                        iResponse.OnSuccess(response.getSuccess());
                    else
                        iResponse.OnFailed(response.getFailed());
                    }else {
                        iResponse.OnFailed(FetchException.RESPONSE);
                    }
                }

            }.execute();

        } else {

            iResponse.OnFailed(FetchException.CONNECTION);
        }
    }

    /**
     * Set post data
     */
    private String getPostDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                if (entry.getValue() != null)
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                else
                    result.append("");
            }
        } catch (UnsupportedEncodingException exception) {
            return exception.getMessage();
        }
        return result.toString();
    }

    /**
     * Set get data
     */
    private String getGetDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        result.append(url);
        result.append("?");
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                if (entry.getValue() != null)
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                else
                    result.append("");
            }
        } catch (UnsupportedEncodingException exception) {
            return exception.getMessage();
        }
        return result.toString();
    }


    /**
     * Save cookie in device
     */
    private void saveCookie() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(
                mContext.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString("Cookie", cookieManager.getCookieStore().getCookies().toString());
        editor.apply();
    }

    /**
     * Add cookie to request
     */
    private void setCookie() {
        SharedPreferences sp = mContext.getSharedPreferences(
                mContext.getPackageName(), Context.MODE_PRIVATE);
        String COOKIE = sp.getString("Cookie", "");
        if (!COOKIE.equals("")) {
            mHeader.put("Set-Cookie", COOKIE);
        }
    }
}

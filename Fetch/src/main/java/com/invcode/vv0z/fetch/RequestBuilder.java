package com.invcode.vv0z.fetch;

import android.content.Context;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    private Context context;
    private String url;
    private Method method;
    private Map<String, String> mParams;
    private Map<String, String> mHeader;
    private boolean cache;
    private boolean cookie;


    public RequestBuilder(Context context) {
        this.context = context;
        this.mParams = new HashMap<>();
        this.mHeader = new HashMap<>();
    }

    public RequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Set method of connection
     *
     * @param method
     * @return
     */
    public RequestBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    /**
     * Set parameter for request
     *
     * @param key
     * @param value
     * @return
     */
    public RequestBuilder setParam(String key, String value) {
        this.mParams.put(key, value);
        return this;
    }

    /**
     * Set all params in map type
     *
     * @param params
     * @return
     */
    public RequestBuilder setParams(Map<String, String> params) {
        this.mParams = params;
        return this;
    }

    /**
     * Set Header properties
     *
     * @param key   : Header property
     * @param value : Value of property
     */
    public RequestBuilder setHeader(String key, String value) {
        this.mHeader.put(key, value);
        return this;
    }

    /**
     * Set all header properties
     *
     * @param headers
     * @return
     */
    public RequestBuilder setHeaders(Map<String, String> headers) {
        this.mHeader = headers;
        return this;
    }

    /**
     * Set cookie
     *
     * @return
     */
    public RequestBuilder setCookies() {
        this.cookie = true;
        return this;
    }

    /**
     * Set cache
     *
     * @return
     */
    public RequestBuilder setCaches() {
        this.cache = true;
        return this;
    }

    /**
     * Get request params and return fetch instance
     *
     * @return
     */
    public Fetch create() {
        return new Fetch(this);
    }


    public Context getContext() {
        return context;
    }

    public String getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public Map<String, String> getHeader() {
        return mHeader;
    }

    public boolean isCache() {
        return cache;
    }

    public boolean isCookie() {
        return cookie;
    }
}

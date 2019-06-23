package com.invcode.vv0z.fetch;

public interface IResponse {

    void OnSuccess(String response);
    void OnFailed(String error);
}

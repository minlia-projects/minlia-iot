package com.minlia.iot.listener;

import com.minlia.iot.config.ApiRequestConfiguration;
import com.minlia.iot.http.HttpResponse;

/**
 * 客户端监听器的适配器
 */
public class ApiClientListenerAdapter implements ApiClientListener {

    @Override
    public void httpSuccess(ApiRequestConfiguration configuration, HttpResponse httpResponse) {

    }

    @Override
    public void httpFailed(ApiRequestConfiguration configuration, HttpResponse httpResponse) {

    }

    @Override
    public void prepareCheckSignFailed(ApiRequestConfiguration configuration, HttpResponse httpResponse) {

    }
}

package com.minlia.iot.listener;

import com.minlia.iot.config.ApiRequestConfiguration;
import com.minlia.iot.http.HttpResponse;

/**
 * 微信支付客户端监听器
 */
public interface ApiClientListener {

    /**
     * http请求成功回调
     * @param configuration
     * @param httpResponse
     */
    void httpSuccess(ApiRequestConfiguration configuration, HttpResponse httpResponse);

    /**
     * http请求失败回调
     * @param configuration
     * @param httpResponse
     */
    void httpFailed(ApiRequestConfiguration configuration, HttpResponse httpResponse);

    /**
     * 预校验失败回调
     * @param configuration
     * @param httpResponse
     */
    void prepareCheckSignFailed(ApiRequestConfiguration configuration, HttpResponse httpResponse);

}

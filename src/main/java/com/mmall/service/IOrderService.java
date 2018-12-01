package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by 11790 on 2018/11/30.
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
}

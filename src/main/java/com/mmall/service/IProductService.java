package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * Created by 11790 on 2018/11/11.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
}

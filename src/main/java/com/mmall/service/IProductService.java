package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by 11790 on 2018/11/11.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<ProductDetailVo> getProductDetailForPortal(Integer productId);
    ServerResponse getProductList(int pageNum,int pageSize);
    ServerResponse<PageInfo> productSearch(String productName, Integer productId, int pageNum, int pageSize);
    ServerResponse<PageInfo> getProductByKeywordAndCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}

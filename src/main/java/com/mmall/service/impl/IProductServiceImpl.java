package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 11790 on 2018/11/11.
 */
@Service("iProductService")
public class IProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null)
        {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品不成功");
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessMessage("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增和更新产品参数不正确");
    }

    /**
     * @param productId 需要修改的产品的id
     * @param status 目标状态
     * @return
     */
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId==null || status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }
}

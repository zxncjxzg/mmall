package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 11790 on 2018/11/24.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 将一定数量的产品添加到购物车中
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId==null || count==null){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.ILLEGAL_ARGUMENT.getCode(),Constant.ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //1.根据用户ID和产品ID获取购物车记录
        Cart cart=cartMapper.selectCartByUserIdAndProductId(userId,productId);
        //2.如果购物车记录不存在则新增；存在则更新产品在购物车中的购买数量
        if(cart==null){
            //这个产品不在购物车中，需要新增
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Constant.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //如果产品已存在，数量相加
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //3.返回前端页面购物车VO对象
        return this.list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId==null || count==null){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.ILLEGAL_ARGUMENT.getCode(),Constant.ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        List<String> productIdList= Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.ILLEGAL_ARGUMENT.getCode(),Constant.ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdAndProductIds(userId,productIdList);
        return this.list(userId);
    }

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo=this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUnchecked(userId,productId,checked);
        return this.list(userId);
    }

    /**
     * 获取当前用户购物车下的商品数量
     * @param userId
     * @return
     */
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    /**
     * 获取购物车VO对象，里面包括一个CartProductVo集合
     * @param userId
     * @return
     */
    private CartVo getCartVo(Integer userId){
        CartVo cartVo=new CartVo();
        //1.根据用户ID获取购物车记录集合
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList= Lists.newArrayList();
        BigDecimal cartTotalPrice=new BigDecimal("0");
        //2.根据Cart对象与Product对象组装CartProductVo对象
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem:cartList){
                CartProductVo cartProductVo=new CartProductVo();
                //将Cart对象中的属性赋值到CartProductVo对象中
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setChecked(cartItem.getChecked());

                //将Product对象中的属性赋值到CartProductVo对象中
                //根据产品ID获取product对象
                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductStatus(product.getStatus());

                    //判断库存
                    int buyLimitCount=0;
                    //因为产品库存数量是随时变化的状态，这时需要将库存数量与购物车记录中的购买数量进行比对
                    if(product.getStock()>=cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_SUCCESS);//符合库存需求
                    }else{
                        //购物车记录中的购买数量已经大于库存了，设置限制购买数量为现有的库存数量，并且设置前端提示信息
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新购买数量为有效库存
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算某一个产品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                }
                //如果已经勾选，增加到整个购物车总价中
                if(cartItem.getChecked()==Constant.Cart.CHECKED){
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     * 判断购物车中的所有商品是否都处于全选状态
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId){
        if(userId==null){
            return false;
        }
        return cartMapper.selectCartProductAllCheckedStatusByUserId(userId)==0;
    }
}

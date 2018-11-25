package com.mmall.controller.portal;

import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 11790 on 2018/11/24.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    /**
     * 查询购物车
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    /**
     * 全选购物车
     * @param session
     * @return
     */
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Constant.Cart.CHECKED);
    }

    /**
     * 全反选购物车
     * @param session
     * @return
     */
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Constant.Cart.UN_CHECKED);
    }

    /**
     * 选中购物车中的某一件商品
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Constant.Cart.CHECKED);
    }

    /**
     * 反选购物车中某一件商品
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Constant.Cart.UN_CHECKED);
    }

    /**
     * 获取当前用户购物车下的商品数量
     * @param session
     * @return
     */
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}

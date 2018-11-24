package com.mmall.controller.portal;

import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by 11790 on 2018/11/24.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }
}

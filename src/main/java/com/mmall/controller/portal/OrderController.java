package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 11790 on 2018/11/30.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * 支付
     * @param session
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        String path=request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    /**
     * 支付宝回调链接
     * @param request
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params=new HashMap<>();
        //1.从支付宝传过来的参数
        Map requestParams=request.getParameterMap();
        for(Iterator iterator=requestParams.keySet().iterator();iterator.hasNext();){
            String name=(String) iterator.next();
            String[] values=(String[])requestParams.get(name);
            String valueStr="";
            for(int i=0;i<values.length;i++){
                valueStr=(i==values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        /**
         * https://support.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.ebae4b70W74QXk&treeId=193&articleId=103296&docType=1
         * 当面付异步通知-仅用于扫码支付
         */
        logger.info("支付宝回调，sign:{}，trade_status:{}，参数：{}",params.get("sign"),params.get("trade_status"),params.toString());
        //2.移除sign_type
        params.remove("sign_type");
        try {
            //3.验证回调的正确性，是不是支付宝发的，并且还要避免重复通知
            boolean alipayRSACheckedV2= AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求则报警！");
            }
        } catch (AlipayApiException e) {
            logger.info("支付宝验证回调异常",e);
        }

        //todo 验证各种参数

        ServerResponse serverResponse=iOrderService.alipayCallback(params);
        if(serverResponse.isSuccess()){
            return Constant.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Constant.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询是否支付成功
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user=(User)session.getAttribute(Constant.CURRENT_USER);
        if(user==null){
            ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),Constant.ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse=iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}

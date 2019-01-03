package com.mmall.controller.portal;

import com.mmall.common.Constant;
import com.mmall.common.RedisPool;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by 11790 on 2018/10/31.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 登录
     * 传递参数：username,password
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
        ServerResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            //session.setAttribute(Constant.CURRENT_USER,response.getData()); 第一版业务逻辑
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.objToString(response.getData()),Constant.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }


    /**
     * 获取登录用户信息
     */
    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
        //User user=(User) session.getAttribute(Constant.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(userJsonStr,User.class);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }


    /**
     * 退出登录
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisPoolUtil.del(loginToken);
        //session.removeAttribute(Constant.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册
     * 传递参数：username,password,email,phone,question,answer，这些参数在前端会被封装为User对象
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 检查用户名是否有效,str可以是用户名也可以是email
     * 传递参数：str,type
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }



    /**
     * 根据用户名获取忘记密码问题提示
     * 传递参数：username
     * @param username
     * @return
     */
    @RequestMapping(value = "forgetGetQuestion.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 提交问题答案
     * 传递参数：username,question,answer
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "checkForgetAnswer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkForgetAnswer(String username,String question,String answer){
        return iUserService.checkForgetAnswer(username,question,answer);
    }

    /**
     * 忘记密码的重设密码
     * 传递参数：username,passwordNew,forgetToken
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forgetResetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetResetPassword(username,newPassword,forgetToken);
    }

    /**
     * 登录中状态重置密码
     * 传递参数：passwordOld,passwordNew
     * @param session
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "resetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest,String oldPassword,String newPassword){
        //1.判断用户是否已登录
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(oldPassword,newPassword,user);
    }

    /**
     * 登录状态更新个人信息
     * 传递参数：email,phone,question,answer
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "updateUserInformation.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInformation(HttpServletRequest httpServletRequest,User user){
        //1.判断用户是否登录
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisPoolUtil.get(loginToken);
        User currentUser=JsonUtil.stringToObj(userJsonStr,User.class);

        if(currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        //2.更新用户信息，返回一个泛型为User的响应对象
        ServerResponse<User> response=iUserService.updateUserInformation(user);
        if(response.isSuccess()){
            RedisPoolUtil.setEx(loginToken, JsonUtil.objToString(response.getData()),Constant.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 获取当前登录用户的详细信息
     * 传递参数：无参数
     * @param session
     * @return
     */
    @RequestMapping(value = "getInformation.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest httpServletRequest){
        //1.先判断一下该用户是否已经登录
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisPoolUtil.get(loginToken);
        User currentUser=JsonUtil.stringToObj(userJsonStr,User.class);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}

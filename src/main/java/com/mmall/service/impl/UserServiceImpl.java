package com.mmall.service.impl;

import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by 11790 on 2018/10/31.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount=userMapper.checkUsername(username);
        //1.用户名不存在
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //判断之前对传过来的密码进行一下加密
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,md5Password);
        //2.密码错误
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //设置密码为空
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        //3.返回一个ServerResponse对象
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
        //1.查看新注册用户名是否已存在
        ServerResponse<String> validResponse=this.checkValid(user.getUsername(),Constant.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //2.查看邮箱名是否已存在
        validResponse=this.checkValid(user.getEmail(),Constant.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //3.设置用户为普通用户
        user.setRole(Constant.Role.ROLE_CUSTOMER);
        //4.MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //5.将用户插入数据库中
        int resultCount=userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if(Constant.USERNAME.equals(type)){
                int resultCount=userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Constant.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("该邮箱已被注册");
                }
            }
        }
        else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username){
        //1.验证该用户名是否存在，如果success表明该用户名不存在
        ServerResponse<String> validResponse=this.checkValid(username,Constant.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //2.该用户存在查找该用户的密码问题
        String question=userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("该用户还未设置密码问题");
    }

    public ServerResponse<String> checkForgetAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkForgetAnswer(username,question,answer);
        if(resultCount>0){
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);//本地缓存把token放进去了
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        //1.验证forgetToken是否为空
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("token需要传递");
        }
        //2.验证用户是否存在
        ServerResponse<String> validResponse=this.checkValid(username,Constant.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //3.判断本地缓存中token是否存在
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token过期");
        }
        //4.使用StringUtils类中的equals方法不用考虑里面两个参数是否为null，修改密码
        if(StringUtils.equals(forgetToken,token)){
            String md5Password=MD5Util.MD5EncodeUtf8(newPassword);
            int resultCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        //5.修改密码失败
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,User user){
        //1.验证旧密码
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        //2.为用户设置新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateUserInformation(User user){
        //1.验证邮箱是否已被其他用户使用
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("email已存在，请更新其他email");
        }
        //为什么不直接更新user呢，而是新建一个updateUser呢，用意何在呢
        //因为updateByPrimaryKeySelective方法根据userId来更新User，而此方法只更新传过去的User中不为空的属性，为空的属性保持不变
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("更新用户信息成功");
        }
        return ServerResponse.createByErrorMessage("更新用户信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse checkAdminRole(User user){
        //用户不为空，并且判断为管理员标志
        if(user != null && user.getRole().intValue()==Constant.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}

package com.mmall.controller.backend;

import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by 11790 on 2018/11/5.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;


    /**
     * 增加节点
     * 传递参数：parentId(default=0)，categoryName
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpServletRequest httpServletRequest, String categoryName, @RequestParam(value="parentId",defaultValue="0") int parentId){
        //1.判断用户是否已登录
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //2.校验一下是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //3.增加品类
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 修改品类名字
     * 传递参数：categoryId，categoryName
     * @param session
     * @param categoryName
     * @param categoryId
     * @return
     */
    @RequestMapping("update_category_name.do")
    @ResponseBody
    public ServerResponse updateCategoryName(HttpServletRequest httpServletRequest,String categoryName,int categoryId){
        //1.判断用户是否已登录
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //2.校验一下是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //3.修改品类名称
            return iCategoryService.updateCategoryName(categoryName,categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取品类子节点
     * 传递参数：categoryId(default=0)
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_children_parallel_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpServletRequest httpServletRequest, @RequestParam(value="categoryId",defaultValue = "0") Integer categoryId){
        //1.判断用户是否已登录
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //2.校验一下是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //3.查询子类别
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取当前分类id及递归子节点的Id
     * 传递参数：categoryId
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_category_and_deep_children_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest httpServletRequest, @RequestParam(value="categoryId",defaultValue = "0") Integer categoryId){
        //1.判断用户是否已登录
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(Constant.ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //2.校验一下是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //3.查询当前分类及其所有递归子分类的id
            return iCategoryService.getCategoryAndDeepChildrenCategoryById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}

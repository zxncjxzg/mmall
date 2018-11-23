package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 11790 on 2018/11/22.
 */
@Controller
@RequestMapping("/product/")
public class ProjectManager {

    @Autowired
    private IProductService iProductService;


    /**
     * 与后台获取产品详情功能的区别在于需要获取产品的状态
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> productDetail(Integer productId){
        return iProductService.getProductDetailForPortal(productId);
    }

    /**
     * 产品搜索，返回列表，并且可实现动态排序
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value="keyword",required = false) String keyword,
                                         @RequestParam(value="categoryId",required = false) Integer categoryId,
                                         @RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10")int pageSize,
                                         @RequestParam(value="orderBy",defaultValue = "")String orderBy){
        return iProductService.getProductByKeywordAndCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}

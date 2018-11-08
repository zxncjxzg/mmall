package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by 11790 on 2018/11/6.
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);
    ServerResponse updateCategoryName(String categoryName,Integer categoryId);
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServerResponse getCategoryAndDeepChildrenCategoryById(Integer categoryId);
}

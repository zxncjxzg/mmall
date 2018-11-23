package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by 11790 on 2018/11/6.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName,Integer parentId){
        //1.对参数categoryName和parentId进行判断
        if(parentId==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        //2.新建一个category对象
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的
        //3.将category对象插入数据库中
        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    //根据categoryId来修改categoryName
    public ServerResponse updateCategoryName(String categoryName,Integer categoryId){
        //1.对参数categoryName和parentId进行判断
        if(categoryId==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改品类参数错误");
        }
        //2.新建一个category对象
        Category category=new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        //3.修改品类名称
        int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("更新品类名称成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名称失败");
    }


    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        //1.根据分类Id获取所有的子分类对象
        List<Category> categoryList=categoryMapper.selectChildrenParallelCategory(categoryId);
        //2.判断集合是否为空
        if(CollectionUtils.isEmpty(categoryList)){
            //日志会打印在哪个目录呢：打印在logback.xml配置的文件中
            logger.info("未找到当前分类的子分类");
        }
        //3.不为空则返回分类对象集合
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id及孩子节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategoryById(Integer categoryId){
        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList= Lists.newArrayList();
        if(categoryId!=null){
            for(Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
    //递归算法，算出子节点
    //直接使用Set就可以排除重复的对象，这个时候需要对Category类中的equals和hashcode方法进行重写
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出的条件,mybatis不会返回一个Null
        List<Category> categoryList=categoryMapper.selectChildrenParallelCategory(categoryId);
        for(Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}

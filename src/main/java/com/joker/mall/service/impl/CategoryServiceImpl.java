package com.joker.mall.service.impl;

import com.joker.mall.dao.CategoryMapper;
import com.joker.mall.pojo.Category;
import com.joker.mall.service.ICategoryService;
import com.joker.mall.vo.CategoryVo;
import com.joker.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.joker.mall.consts.MallConst.ROOT_PARENT_ID;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {

        List<CategoryVo> categoryVoList;
        List<Category> categories = categoryMapper.selectAll();

//        for (Category category : categories) {
//            if(category.getParentId().equals(ROOT_PARENT_ID)){
//                CategoryVo categoryVo = new CategoryVo();
//                BeanUtils.copyProperties(category,categoryVo);
//                categoryVoList.add(categoryVo);
//            }
//        }

       //lambda + stream
       categoryVoList = categories.stream()
                .filter(e -> e.getParentId().equals(ROOT_PARENT_ID))
                .map(this::categoryVo2CategoryVo)
               .sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
                .collect(Collectors.toList());

       //查询子目录
        findSubCategory(categoryVoList,categories);



        return ResponseVo.successByData(categoryVoList);
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id,resultSet,categories);
    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {

        for (Category category : categories) {
            if(category.getParentId().equals(id)){
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(),resultSet,categories);
            }
        }

    }

    private void findSubCategory(List<CategoryVo> categoryVoList,
                                 List<Category> categories){
        for (CategoryVo categoryVo : categoryVoList) {

            List<CategoryVo> subCategoryVoList = new ArrayList<>();
            for (Category category : categories) {
                //如果查到内容，设置subCategory，继续往下查
                if(categoryVo.getId().equals(category.getParentId())){

                    CategoryVo subCategoryVo = categoryVo2CategoryVo(category);
                    subCategoryVoList.add(subCategoryVo);
                }

                subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
                categoryVo.setSubCategories(subCategoryVoList);

                findSubCategory(subCategoryVoList,categories);
            }
        }
    }

    private CategoryVo categoryVo2CategoryVo(Category category){

        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}

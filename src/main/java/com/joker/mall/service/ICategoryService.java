package com.joker.mall.service;

import com.joker.mall.vo.CategoryVo;
import com.joker.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {

    ResponseVo<List<CategoryVo>> selectAll();

    void findSubCategoryId(Integer id, Set<Integer> resultSet);

}

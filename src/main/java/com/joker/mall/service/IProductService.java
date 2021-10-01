package com.joker.mall.service;

import com.github.pagehelper.PageInfo;
import com.joker.mall.vo.ProductDetailVo;
import com.joker.mall.vo.ResponseVo;

public interface IProductService {

    ResponseVo<PageInfo> list(Integer categroyId, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);

}

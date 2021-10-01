package com.joker.mall.service;

import com.github.pagehelper.PageInfo;
import com.joker.mall.MallApplicationTests;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.vo.ProductDetailVo;
import com.joker.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IProductServiceTest extends MallApplicationTests {

    @Autowired
    private IProductService productService;
    @Test
    public void list() {

        ResponseVo<PageInfo> responseVo = productService.list(null, 1, 1);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo .getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<ProductDetailVo> responseVo = productService.detail(26);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo .getStatus());
    }
}
package com.joker.mall.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joker.mall.MallApplicationTests;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.form.CartAddForm;
import com.joker.mall.form.CartUpdateForm;
import com.joker.mall.vo.CartVo;
import com.joker.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ICartServiceTest extends MallApplicationTests {

    @Autowired
    private ICartService cartService;
    private Integer uid = 1;

    private Integer productId = 29;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Before
    public void add() {
        log.info("【新增购物车...】");
        CartAddForm form =new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        ResponseVo<CartVo> list = cartService.add(uid, form);
        log.info("add={}",gson.toJson(list));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), list.getStatus());
    }

    @Test
    public void list() {
        ResponseVo<CartVo> list = cartService.list(uid);
        log.info("list={}",gson.toJson(list));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), list.getStatus());
    }
    @Test
    public void update() {
        CartUpdateForm cartUpdateForm = new CartUpdateForm();
        cartUpdateForm.setQuantity(5);
        cartUpdateForm.setSelected(false);
        ResponseVo<CartVo> list = cartService.update(uid,productId,cartUpdateForm);
        log.info("update={}",gson.toJson(list));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), list.getStatus());
    }
    @After
    public void delete() {
        log.info("【删除购物车...】");
        ResponseVo<CartVo> list = cartService.delete(uid,productId);
        log.info("delete={}",gson.toJson(list));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), list.getStatus());
    }

    @Test
    public void selectAll() {
        ResponseVo<CartVo> responseVo = cartService.selectAll(uid);
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void unSelectAll() {
        ResponseVo<CartVo> responseVo = cartService.unSelectAll(uid);
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void sum() {
        ResponseVo<Integer> responseVo = cartService.sum(uid);
        log.info("result={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}
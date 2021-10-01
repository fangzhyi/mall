package com.joker.mall.service;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joker.mall.MallApplicationTests;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.form.CartAddForm;
import com.joker.mall.vo.CartVo;
import com.joker.mall.vo.OrderVo;
import com.joker.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class IOrderServiceTest extends MallApplicationTests {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    private Integer uid = 1;

    private Integer shippingId = 12;

    private Integer productId = 26;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();;


    @Before
    public void before() {
        CartAddForm form = new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(uid, form);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    private ResponseVo<OrderVo> createTest() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("create={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        return responseVo;
    }
    @Test
    public void create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("create={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> ResponseVo = orderService.list(uid, 1,2);
        log.info("list={}",gson.toJson(ResponseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), ResponseVo.getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<OrderVo> orderVoResponseVo = createTest();
        ResponseVo<OrderVo> ResponseVo = orderService.detail(uid, orderVoResponseVo.getData().getOrderNo());
        log.info("detail={}",gson.toJson(ResponseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), ResponseVo.getStatus());

    }

    @Test
    public void cancel() {
        ResponseVo<OrderVo> orderVoResponseVo = createTest();
        ResponseVo<OrderVo> ResponseVo = orderService.cancel(uid, orderVoResponseVo.getData().getOrderNo());
        log.info("detail={}",gson.toJson(ResponseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), ResponseVo.getStatus());

    }

    @Test
    public void paid() {
    }
}
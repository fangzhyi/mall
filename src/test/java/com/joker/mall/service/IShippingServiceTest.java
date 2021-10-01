package com.joker.mall.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joker.mall.MallApplicationTests;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.form.ShippingForm;
import com.joker.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
public class IShippingServiceTest extends MallApplicationTests {

    @Autowired
    private IShippingService shippingService;

    private  Integer uid = 1;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private ShippingForm shippingForm;

    private Integer shippingId;
    @Before
    public void before(){
        ShippingForm shippingForm = new ShippingForm();
        shippingForm.setReceiverName("joker");
        shippingForm.setReceiverAddress("joker.com");
        shippingForm.setReceiverCity("潮州市");
        shippingForm.setReceiverMobile("457439874387");
        shippingForm.setReceiverPhone("23511545534");
        shippingForm.setReceiverProvince("广东省");
        shippingForm.setReceiverZip("515645");
        this.shippingForm = shippingForm;
        add();
    }

    public void add() {
        ResponseVo<Map<String, Integer>> responseVo = shippingService.add(uid, shippingForm);
        log.info("responseVo={}",gson.toJson(responseVo));
        this.shippingId = responseVo.getData().get("shippingId");
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @After
    public void delete() {
        ResponseVo responseVo = shippingService.delete(uid, shippingId);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }

    @Test
    public void update() {
        shippingForm.setReceiverCity("梅州市");
        ResponseVo responseVo = shippingService.update(uid, shippingId ,shippingForm);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }

    @Test
    public void list() {
        ResponseVo responseVo = shippingService.list(uid, 1,10);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }
}
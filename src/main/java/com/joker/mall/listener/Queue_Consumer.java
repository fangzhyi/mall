package com.joker.mall.listener;

import com.google.gson.Gson;
import com.joker.mall.pojo.PayInfo;
import com.joker.mall.service.IOrderService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

@Component
public class Queue_Consumer {

    private IOrderService orderService;
    // 注册一个监听器。destination指定监听的主题。
    @JmsListener(destination = "${myqueue}")
    public void receive(TextMessage textMessage) throws  Exception{
        PayInfo payInfo = new Gson().fromJson(textMessage.getText(), PayInfo.class);
        if(payInfo.getPlatformStatus().equals("SUCCESS")){
            //修改订单状态
            orderService.paid(payInfo.getOrderNo());
        }
    }

}

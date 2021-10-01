package com.joker.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joker.mall.dao.ShippingMapper;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.form.ShippingForm;
import com.joker.mall.pojo.Shipping;
import com.joker.mall.service.IShippingService;
import com.joker.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;


    @Override
    public ResponseVo<Map<String,Integer>> add(Integer uid, ShippingForm form) {
       Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form,shipping);
        shipping.setUserId(uid);
        int row = shippingMapper.insertSelective(shipping);
        if(row == 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        System.out.println(shipping.getId());
        Map<String, Integer> map = new HashMap<>();
        map.put("shippingId",shipping.getId());
        return ResponseVo.successByData(map);
    }

    @Override
    public ResponseVo delete(Integer uid, Integer shippingId) {

        int row = shippingMapper.deleteByIdAndUid(uid, shippingId);
        if(row == 0){
           return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.successByMsg();
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm form) {

        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form,shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if(row == 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.successByMsg();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectByUid(uid);
        PageInfo pageInfo = new PageInfo(shippings);
        return ResponseVo.successByData(pageInfo);
    }
}

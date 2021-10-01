package com.joker.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joker.mall.dao.OrderItemMapper;
import com.joker.mall.dao.OrderMapper;
import com.joker.mall.dao.ProductMapper;
import com.joker.mall.dao.ShippingMapper;
import com.joker.mall.enums.OrderStatusEnum;
import com.joker.mall.enums.PaymentTypeEnum;
import com.joker.mall.enums.ProductStatusEnum;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.pojo.*;
import com.joker.mall.service.ICartService;
import com.joker.mall.service.IOrderService;
import com.joker.mall.vo.OrderItemVo;
import com.joker.mall.vo.OrderVo;
import com.joker.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceimpl implements IOrderService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {

        //收货地址校验
        Shipping shipping = shippingMapper.selectByUidAndShippingId(uid, shippingId);
        if(shipping == null){
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        //获取购物车
        List<Cart> carts = cartService.listForCart(uid).stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(carts)){
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        //获取carts里的products
        Set<Integer> productIdSet = carts.stream().map(Cart::getProductId).collect(Collectors.toSet());

        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        Map<Integer,Product> map = productList.stream()
                .collect(Collectors.toMap(Product::getId,product -> product));

        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOederNo();;
        for (Cart cart : carts) {

            Product product = map.get(cart.getProductId());
            //是否有该商品
            if (product == null){
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,cart.getProductId()+"商品不存在.");
            }
            //商品的上下架状态
            if(!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())){
                return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE,product.getName()+"商品不是在售状态.");
            }

            //库存是否充足
            if(product.getStock() < cart.getQuantity()){
                return  ResponseVo.error(ResponseEnum.PROODUCT_STOCK_ERROR,product.getName()+"库存不足。");
            }
            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);

            //减库存
            product.setStock(product.getStock() - cart.getQuantity());
            int rowForProduct = productMapper.updateByPrimaryKeySelective(product);
            if (rowForProduct <= 0){
                return ResponseVo.error(ResponseEnum.ERROR);
            }

        }

        //计算被选中的商品的总价格
        //生成订单,入库：同时写入order和order_item（事务）
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);

        int rowForOrder = orderMapper.insertSelective(order);
        if (rowForOrder <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);

        }
        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);

        }

        //更新购物车（选中的商品）
        //Redis不能回滚
        for (Cart cart : carts) {
            cartService.delete(uid,cart.getProductId());
        }
        //构造orderVo
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);

        return ResponseVo.successByData(orderVo);
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {

        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo);

        List<OrderItemVo> orderItemVoList = orderItemList.stream().map(e -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(e, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVoList);
        if (shipping != null){
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }
        return orderVo;

    }

    private Order buildOrder(Integer uid,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList
    ) {
        BigDecimal payment = orderItemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return order;
    }
    private Long generateOederNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer uid, Long orderNo, Integer quantity, Product product) {
        OrderItem item = new OrderItem();
        item.setUserId(uid);
        item.setOrderNo(orderNo);
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setCurrentUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid);


        Set<Long> orderNoSet = orderList.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toSet());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Map<Long,List<OrderItem>> orderItemMap = orderItemList.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));

        Set<Integer> shippingIdSet = orderList.stream()
                .map(Order::getShippingId)
                .collect(Collectors.toSet());
        List<Shipping> shippingList = shippingMapper.selectByIdSet(shippingIdSet);
        Map<Integer,Shipping> shippingMap = shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId,shipping -> shipping));

        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order,
                    orderItemMap.get(order.getOrderNo()),
                    shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }

        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ResponseVo.successByData(pageInfo);
    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        Set<Long> orderNoSet = new HashSet<>();
        orderNoSet.add(order.getOrderNo());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);

        return ResponseVo.successByData(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        //只有未付款订单可以取消
        if(!OrderStatusEnum.NO_PAY.getCode().equals(order.getStatus())){
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }

        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if(row <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.successByMsg();
    }

    @Override
    public void paid(Long orderNo) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc() + "订单id:" + orderNo);
        }
        //只有未付款订单可以付款
        if(!OrderStatusEnum.NO_PAY.getCode().equals(order.getStatus())){
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc() + "订单id:" + orderNo);
        }

        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if(row <= 0){
            throw new RuntimeException("将订单更新为已支付状态失败！" + "订单id:" + orderNo);
        }
    }
}

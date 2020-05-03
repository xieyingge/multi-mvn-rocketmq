package com.roman.shop;


import com.roman.shop.entity.Result;
import com.roman.shop.pojo.TradeOrder;

public interface IOrderService {

    /**
     * 下单接口
     * @param order
     * @return
     */
    public Result confirmOrder(TradeOrder order);

}

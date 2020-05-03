package com.roman.shop.pojo;

import java.io.Serializable;

public class TradeGoodsNumberLogKey implements Serializable {
    private static final long serialVersionUID = -5809782578272943999L;
    private Long goodsId;

    private Long orderId;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
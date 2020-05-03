package com.roman.shop;


import com.roman.shop.entity.Result;
import com.roman.shop.pojo.TradeGoods;
import com.roman.shop.pojo.TradeGoodsNumberLog;

public interface IGoodsService {
    /**
     * 根据ID查询商品对象
     * @param goodsId
     * @return
     */
    TradeGoods findOne(Long goodsId);

    /**
     * 扣减库存
     * @param goodsNumberLog
     * @return
     */
    Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog);
}

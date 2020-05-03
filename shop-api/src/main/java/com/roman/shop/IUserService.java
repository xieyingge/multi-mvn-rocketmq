package com.roman.shop;


import com.roman.shop.entity.Result;
import com.roman.shop.pojo.TradeUser;
import com.roman.shop.pojo.TradeUserMoneyLog;

public interface IUserService {
    TradeUser findOne(Long userId);

    Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog);
}

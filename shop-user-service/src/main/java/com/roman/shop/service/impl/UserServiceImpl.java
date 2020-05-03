package com.roman.shop.service.impl;

import com.roman.shop.IUserService;
import com.roman.shop.constant.ShopCode;
import com.roman.shop.entity.Result;
import com.roman.shop.exception.CastException;
import com.roman.shop.mapper.TradeUserMapper;
import com.roman.shop.mapper.TradeUserMoneyLogMapper;
import com.roman.shop.pojo.TradeUser;
import com.roman.shop.pojo.TradeUserMoneyLog;
import com.roman.shop.pojo.TradeUserMoneyLogExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@com.alibaba.dubbo.config.annotation.Service(interfaceClass = IUserService.class)
public class UserServiceImpl implements IUserService {

    @Autowired
    private TradeUserMapper userMapper;

    @Autowired
    private TradeUserMoneyLogMapper userMoneyLogMapper;

    @Override
    public TradeUser findOne(Long userId) {
        if(userId==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        //判断请求参数是否合法
        if (userMoneyLog == null
                || userMoneyLog.getUserId() == null
                || userMoneyLog.getUseMoney() == null
                || userMoneyLog.getOrderId() == null
                || userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        //查询该订单是否存在付款记录
        TradeUserMoneyLogExample userMoneyLogExample = new TradeUserMoneyLogExample();
        userMoneyLogExample.createCriteria()
                .andUserIdEqualTo(userMoneyLog.getUserId())
                .andOrderIdEqualTo(userMoneyLog.getOrderId());
        int count = userMoneyLogMapper.countByExample(userMoneyLogExample);
//        TradeUser tradeUser = new TradeUser();
//        tradeUser.setUserId(userMoneyLog.getUserId());
//        tradeUser.setUserMoney(userMoneyLog.getUseMoney().longValue());
        TradeUser tradeUser = userMapper.selectByPrimaryKey(userMoneyLog.getUserId());
        //判断余额操作行为
        //【付款操作】
        if (userMoneyLog.getMoneyLogType().equals(ShopCode.SHOP_USER_MONEY_PAID.getCode())) {
            //订单已经付款，则抛异常
            if (count > 0) {
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            //用户账户扣减余额
            tradeUser.setUserMoney(BigDecimal.valueOf(tradeUser.getUserMoney()).subtract(userMoneyLog.getUseMoney()).longValue());
            userMapper.updateByPrimaryKey(tradeUser);
//            userMapper.reduceUserMoney(tradeUser);
        }
        //【退款操作】
        if (userMoneyLog.getMoneyLogType().equals(ShopCode.SHOP_USER_MONEY_REFUND.getCode())) {
            //如果订单未付款,则不能退款,抛异常
            if (count == 0) {
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY);
            }
            //防止多次退款
            userMoneyLogExample = new TradeUserMoneyLogExample();
            userMoneyLogExample.createCriteria()
                    .andUserIdEqualTo(userMoneyLog.getUserId())
                    .andOrderIdEqualTo(userMoneyLog.getOrderId())
                    .andMoneyLogTypeEqualTo(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
            count = userMoneyLogMapper.countByExample(userMoneyLogExample);
            if (count > 0) {
                CastException.cast(ShopCode.SHOP_USER_MONEY_REFUND_ALREADY);
            }
            //用户账户添加余额
            tradeUser.setUserMoney(BigDecimal.valueOf(tradeUser.getUserMoney()).add(userMoneyLog.getUseMoney()).longValue());
            userMapper.updateByPrimaryKey(tradeUser);
//            userMapper.addUserMoney(tradeUser);
        }

        //记录用户使用余额日志
        userMoneyLog.setCreateTime(new Date());
        userMoneyLogMapper.insert(userMoneyLog);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }
}

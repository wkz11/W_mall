package com.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.trade.domain.po.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}

package com.api.fallback;

import com.api.client.ItemClient;
import com.api.dto.ItemDTO;
import com.api.dto.OrderDetailDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Component
public class ItemClientFallbackFactory implements FallbackFactory<ItemClient> {
    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {
            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                //查询异常，返回空集合
                return Collections.emptyList();
            }

            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                //扣库存失败
                throw new RuntimeException(cause);

            }
        };
    }
}

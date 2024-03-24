package com.trade.listeners;

import com.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayStatusListener {

    private  final IOrderService orderService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "mark.order.pay.queue",declare = "true"),
            exchange = @Exchange(name = "pay.topic",type = ExchangeTypes.TOPIC),
            key = "pay.success"
    ))
    public void listenOrderPay(Long orderId){
      //标志订单已支付
        orderService.markOrderPaySuccess(orderId);
    }
}

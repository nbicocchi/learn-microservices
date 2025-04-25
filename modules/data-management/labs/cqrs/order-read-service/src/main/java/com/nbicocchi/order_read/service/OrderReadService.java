package com.nbicocchi.order_read.service;

import com.nbicocchi.order_read.dto.Order;
import com.nbicocchi.order_read.persistence.model.OrderRead;
import com.nbicocchi.order_read.persistence.repository.OrderReadRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@AllArgsConstructor
@Component
@Slf4j
public class OrderReadService {
    private final OrderReadRepository orderReadRepository;

    public void updateReadModel(Order order) {
        log.info("updating read model {}...", order);
        Optional<OrderRead> optionalOrderRead = orderReadRepository.findByCustomerIdAndMAndY(
                order.getCustomerId(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear()
        );
        OrderRead orderRead;
        if (optionalOrderRead.isPresent()) {
            orderRead = optionalOrderRead.get();
            orderRead.setN(orderRead.getN() + 1);
        } else {
            orderRead = new OrderRead(
                    order.getCustomerId(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getYear(),
                    1);
        }
        orderReadRepository.save(orderRead);
    }
}

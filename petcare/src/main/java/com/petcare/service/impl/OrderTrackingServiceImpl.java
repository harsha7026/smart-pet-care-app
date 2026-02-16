package com.petcare.service.impl;

import com.petcare.dto.OrderTrackingEventDTO;
import com.petcare.service.OrderTrackingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderTrackingServiceImpl implements OrderTrackingService {

    // NOTE: Minimal in-memory implementation for prototype. Replace with repository-based implementation.

    @Override
    public List<OrderTrackingEventDTO> getTrackingEvents(Long orderId) {
        // return empty list for now
        return new ArrayList<>();
    }

    @Override
    public OrderTrackingEventDTO addTrackingEvent(Long orderId, OrderTrackingEventDTO dto) {
        // In a real impl, save to DB and update order status. Here, echo back with id and timestamp.
        dto.setId(System.currentTimeMillis());
        dto.setOrderId(orderId);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }
}

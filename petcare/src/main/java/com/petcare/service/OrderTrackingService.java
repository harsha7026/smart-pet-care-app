package com.petcare.service;

import com.petcare.dto.OrderTrackingEventDTO;

import java.util.List;

public interface OrderTrackingService {
    List<OrderTrackingEventDTO> getTrackingEvents(Long orderId);
    OrderTrackingEventDTO addTrackingEvent(Long orderId, OrderTrackingEventDTO dto);
}

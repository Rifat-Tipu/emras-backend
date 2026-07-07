package com.emras.order.mapper;
import com.emras.order.dto.response.OrderItemResponse;
import com.emras.order.dto.response.OrderResponse;
import com.emras.order.dto.response.OrderStatusHistoryResponse;
import com.emras.order.entity.Order;
import com.emras.order.entity.OrderItem;
import com.emras.order.entity.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    OrderResponse toResponse(Order order);
    OrderItemResponse toItemResponse(OrderItem item);
    OrderStatusHistoryResponse toHistoryResponse(OrderStatusHistory history);
    List<OrderResponse> toResponseList(List<Order> orders);
}
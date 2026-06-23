package com.emras.inventory.mapper;
import com.emras.inventory.dto.response.InventoryItemResponse;
import com.emras.inventory.dto.response.StockAvailabilityResponse;
import com.emras.inventory.entity.InventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {
    @Mapping(target = "availableQuantity", expression = "java(item.getAvailableQuantity())")
    @Mapping(target = "inStock",           expression = "java(item.isInStock())")
    @Mapping(target = "lowStock",          expression = "java(item.isLowStock())")
    InventoryItemResponse toResponse(InventoryItem item);

    @Mapping(target = "availableQuantity", expression = "java(item.getAvailableQuantity())")
    @Mapping(target = "inStock",           expression = "java(item.isInStock())")
    @Mapping(target = "lowStock",          expression = "java(item.isLowStock())")
    StockAvailabilityResponse toAvailabilityResponse(InventoryItem item);
}
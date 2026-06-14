package com.emras.user.mapper;
import com.emras.user.dto.response.WishlistItemResponse;
import com.emras.user.entity.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WishlistItemMapper {
    @Mapping(target = "addedAt", source = "createdAt")
    WishlistItemResponse toResponse(WishlistItem item);
    List<WishlistItemResponse> toResponseList(List<WishlistItem> items);
}
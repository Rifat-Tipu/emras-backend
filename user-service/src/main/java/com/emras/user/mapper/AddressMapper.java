package com.emras.user.mapper;
import com.emras.user.dto.request.CreateAddressRequest;
import com.emras.user.dto.response.AddressResponse;
import com.emras.user.entity.Address;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    AddressResponse toResponse(Address address);
    List<AddressResponse> toResponseList(List<Address> addresses);
    /**
     * @BeanMapping(builder = @Builder(disableBuilder = true))
     * Forces MapStruct to use setters instead of Lombok's @Builder.
     * Without this, MapStruct tries to use the builder and cannot resolve
     * properties like "id" and "userId" as valid targets.
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "userId",   ignore = true)
    @Mapping(target = "isDefault", source = "isDefault")
    Address toEntity(CreateAddressRequest request);
}
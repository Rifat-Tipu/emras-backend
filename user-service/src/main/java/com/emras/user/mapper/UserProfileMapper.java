package com.emras.user.mapper;
import com.emras.user.dto.request.UpdateProfileRequest;
import com.emras.user.dto.response.UserProfileResponse;
import com.emras.user.entity.UserProfile;
import org.mapstruct.*;
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {
    UserProfileResponse toResponse(UserProfile profile);
    /** Only updates non-null fields from the request — preserves existing values */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile profile);
}
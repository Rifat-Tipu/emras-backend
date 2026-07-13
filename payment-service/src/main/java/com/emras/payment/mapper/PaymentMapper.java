package com.emras.payment.mapper;

import com.emras.payment.dto.response.PaymentResponse;
import com.emras.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    PaymentResponse toResponse(Payment payment);
}
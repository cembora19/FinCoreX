package com.fincorex.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(

        @NotNull UUID userId,

        @NotNull @DecimalMin(value = "0.01") BigDecimal amount

) {
}
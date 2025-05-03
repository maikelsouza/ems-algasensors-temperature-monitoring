package com.algaworks.algasensors.temperature.monitoring.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorAlertInput {

    @NotNull(message = "is requeried")
    private Double maxTemperature;

    @NotNull(message = "is requeried")
    private Double minTemperature;
}

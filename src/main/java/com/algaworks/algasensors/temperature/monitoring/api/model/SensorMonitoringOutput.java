package com.algaworks.algasensors.temperature.monitoring.api.model;

import io.hypersistence.tsid.TSID;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class SensorMonitoringOutput {

    public TSID id;

    public Double lastTemperature;

    public OffsetDateTime updateAt;

    public Boolean enable;

}

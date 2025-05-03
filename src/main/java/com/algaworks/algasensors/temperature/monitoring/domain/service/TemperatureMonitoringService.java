package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemperatureMonitoringService {

    private final SensorMonitoringRepository sensorMonitoringRepository;

    private final TemperatureLogRepository temperatureLogRepository;

    @Transactional
    public void processTemperatureReading(TemperatureLogData temperatureLogData){
        log.info("processTemperatureReading");
        if(temperatureLogData.getValeu().equals(10.5)){
            throw new RuntimeException("Test Erros");
        }
        sensorMonitoringRepository.findById(new SensorId(temperatureLogData.getSensorID())).
                ifPresentOrElse(sensorMonitoring -> handleSensorMonitoring(temperatureLogData, sensorMonitoring),
                        () -> logIgnoredTemperature(temperatureLogData));
    }

    private void logIgnoredTemperature(TemperatureLogData temperatureLogData) {
        log.info("Temperature Ignored: SensorId {} temp {}", temperatureLogData.getSensorID(), temperatureLogData.getValeu());
    }

    private void handleSensorMonitoring(TemperatureLogData temperatureLogData, SensorMonitoring sensorMonitoring) {
        if (sensorMonitoring.isEnabled()){
            sensorMonitoring.setLastTemperature(temperatureLogData.getValeu());
            sensorMonitoring.setUpdateAt(OffsetDateTime.now());
            sensorMonitoringRepository.save(sensorMonitoring);
            TemperatureLog temperatureLog = TemperatureLog.
                    builder()
                    .id(new TemperatureLogId(temperatureLogData.getId()))
                    .registeredAt(temperatureLogData.getRegisteredAt())
                    .value(temperatureLogData.getValeu())
                    .sensorId(new SensorId(temperatureLogData.getSensorID()))
                    .build();

            temperatureLogRepository.save(temperatureLog);

            log.info("Temperature Updated: SensorId {} temp {}", temperatureLogData.getSensorID(), temperatureLogData.getValeu());
        }else {
            logIgnoredTemperature(temperatureLogData);
        }
    }
}

package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/sensors/{sensorId}/monitoring")
@RequiredArgsConstructor
public class SensorMonitoringController {


    private final SensorMonitoringRepository repository;

    @GetMapping
    public SensorMonitoringOutput getDetail(@PathVariable TSID sensorId){
        SensorId sensorID = new SensorId(sensorId);
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorID);
        return SensorMonitoringOutput
                .builder()
                .id(sensorMonitoring.getId().getValue())
                .enable(sensorMonitoring.getEnable())
                .lastTemperature(sensorMonitoring.getLastTemperature())
                .updateAt(sensorMonitoring.getUpdateAt())
                .build();
    }

    private SensorMonitoring findByIdOrDefault(SensorId sensorID) {
        return repository.findById(sensorID)
                .orElse(SensorMonitoring
                        .builder()
                        .id(sensorID)
                        .enable(false)
                        .lastTemperature(null)
                        .updateAt(null)
                        .build());
    }

    @PutMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable TSID sensorId){
        SensorId sensorID = new SensorId(sensorId);
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorID);
        if (sensorMonitoring.isEnabled()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        sensorMonitoring.setEnable(true);
        repository.saveAndFlush(sensorMonitoring);
    }

    @PutMapping("/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SneakyThrows
    public void disable(@PathVariable TSID sensorId){
        SensorId sensorID = new SensorId(sensorId);
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorID);
        if (!sensorMonitoring.isEnabled()){
            Thread.sleep(Duration.ofSeconds(10));
        }
        sensorMonitoring.setEnable(false);
        repository.saveAndFlush(sensorMonitoring);
    }

}
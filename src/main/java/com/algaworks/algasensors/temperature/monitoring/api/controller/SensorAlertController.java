package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.service.SensorAlertService;
import io.hypersistence.tsid.TSID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensors/{sensorId}/alert")
@RequiredArgsConstructor
public class SensorAlertController {

    private final SensorAlertService service;

    @GetMapping
    public SensorAlertOutput getOne(@PathVariable TSID sensorId){
        SensorId sensorID = buildSensorID(sensorId);
        SensorAlert sensorAlert = service.findById(sensorID);
        return convertToOutput(sensorAlert);
    }

    @PutMapping
    public SensorAlertOutput createOrUpdate(@PathVariable TSID sensorId,
                                            @Valid @RequestBody SensorAlertInput input) {
        SensorAlert sensorAlert = findByIdOrDefault(sensorId);
        sensorAlert.setMinTemperature(input.getMinTemperature());
        sensorAlert.setMaxTemperature(input.getMaxTemperature());
        return convertToOutput(service.save(sensorAlert));
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteBySensorId(@PathVariable TSID sensorId){
        SensorId sensorID = buildSensorID(sensorId);
        if (service.existsBySensorId(sensorID)) {
            service.deleteBySensorId(sensorID);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private SensorId buildSensorID(TSID sensorId) {
        return new SensorId(sensorId);
    }

    private SensorAlert findByIdOrDefault(TSID sensorId) {
        return service.findByIdOrDefault(new SensorId(sensorId));
    }

    private SensorAlertOutput convertToOutput(SensorAlert sensorAlert) {
        return SensorAlertOutput.builder()
                .id(sensorAlert.getId().getValue())
                .maxTemperature(sensorAlert.getMaxTemperature())
                .minTemperature(sensorAlert.getMinTemperature())
                .build();
    }

}
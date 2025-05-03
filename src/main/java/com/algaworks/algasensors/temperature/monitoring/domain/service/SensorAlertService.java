package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
@Slf4j
public class SensorAlertService {

    private SensorAlertRepository repository;


    public SensorAlert findById(SensorId sensorID){
        return repository.findById(sensorID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public SensorAlert findByIdOrDefault(SensorId sensorId){
        return  repository.findById(sensorId)
                    .orElse(SensorAlert.builder()
                    .id(new SensorId(sensorId.getValue()))
                    .minTemperature(null)
                    .maxTemperature(null)
                    .build());
    }

    @Transactional
    public void handleAlert(TemperatureLogData temperatureLogData){
        repository.findById(new SensorId(temperatureLogData.getSensorID()))
                .ifPresentOrElse(sensorAlert -> {
                    if(sensorAlert.getMaxTemperature() != null &&
                            temperatureLogData.getValeu().compareTo(sensorAlert.getMaxTemperature()) >= 0){
                        log.info("Alert Max Temp: SensorId {}, temp {}", temperatureLogData.getSensorID(), temperatureLogData.getValeu());
                    } else if (sensorAlert.getMinTemperature() != null &&
                            temperatureLogData.getValeu().compareTo(sensorAlert.getMinTemperature()) < 0) {
                        log.info("Alert Min Temp: SensorId {}, temp {}", temperatureLogData.getSensorID(), temperatureLogData.getValeu());
                    }else{
                        log.info("Alert Ignored: SensorId {}, temp {}", temperatureLogData.getSensorID(), temperatureLogData.getValeu());
                    }

                    },() ->{
                       log.info("Alert Ignored: SensorId {}, temp {}", temperatureLogData.getSensorID(), temperatureLogData.getValeu());
                });
    }


    @Transactional
    public SensorAlert save(SensorAlert sensorAlert){
        return repository.save(sensorAlert);
    }

    public boolean existsBySensorId(SensorId sensorID){
        return repository.existsById(sensorID);
    }

    @Transactional
    public void deleteBySensorId(SensorId sensorID){
        repository.deleteById(sensorID);
    }

}
package telran.probes.controller;

import static telran.probes.messages.ErrorMessages.WRONG_SENSOR_ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.Range;
import telran.probes.service.SensorRangeProviderService;

@RestController
@Slf4j
public class SensorRangesProviderController {
	@Autowired
	SensorRangeProviderService service;
	@GetMapping("/sensor/range/{id}")
	Range getSensorRange(@PathVariable @Min(value = 1, message = WRONG_SENSOR_ID) long id) {
		Range sensorRange = service.getSensorRange(id);
		log.debug("sensor range received {}", sensorRange);
		return sensorRange;
	}
}

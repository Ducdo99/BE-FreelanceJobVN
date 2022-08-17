package group5.freelancejob.controllers;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import group5.freelancejob.services.MQTT.MqttService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApi {
    private final MqttService _mqttService;

    public RestApi(MqttService mqttService) {
        _mqttService = mqttService;
    }

    @GetMapping(value = "/")
    public String welcome() {
        return "Welcome to Freelance Job Vietnam Project!\n";
    }

    @GetMapping(value = "/test-mqtt/{id}")
    public ResponseEntity<?> testMqtt(@PathVariable String id) {
        _mqttService.publish("Test Something", "/msg/"+id, MqttQos.AT_LEAST_ONCE, false);
        return ResponseEntity.noContent().build();
    }
}
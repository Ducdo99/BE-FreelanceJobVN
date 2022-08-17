package group5.freelancejob.services.MQTT;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MqttService {
    final String host = "57fed5bb047e4ddca916fcfd631dfa06.s2.eu.hivemq.cloud";
    final String username = "fvn-mqtt";
    final String password = "wT5Ur0rfFNeLgaYqaN";

    Mqtt5BlockingClient client;

    public MqttService() {
        client = MqttClient.builder().useMqttVersion5().serverHost(host).serverPort(8883).sslWithDefaultConfig().buildBlocking();
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();
        System.out.println("Connected successfully");
    }

    public void publish(String payload, String topic, MqttQos qos, boolean retained) {
        client.publishWith().topic(topic).payload(UTF_8.encode(payload)).qos(qos).retain(retained).send();
    }

}

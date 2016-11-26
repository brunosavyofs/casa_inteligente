package com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by bruno on 26/11/16.
 */

public interface TopicoCallback {
    String getIdTopico();

    void doMessageArrived(MqttMessage mensagem) throws Exception;
}

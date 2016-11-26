package com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by catolica on 19/11/16.
 */

public class ClienteBroker implements MqttCallback {

    private static final String TAG = "MAIN";
    String broker = "tcp://m13.cloudmqtt.com:18768";
    String clientId = "appAndroid";
    String username = "mdzjtvif";
    String password = "snX5gG5TJs8P";
    int qos = 1;
    MemoryPersistence persistence = new MemoryPersistence();

    MqttClient client;
    MqttConnectOptions connOpts;

    Map<String, TopicoCallback> topicos = new HashMap<String, TopicoCallback>();

    public ClienteBroker() {
        this.connOpts = new MqttConnectOptions();
        this.connOpts.setCleanSession(true);
        this.connOpts.setUserName(this.username);
        this.connOpts.setPassword(this.password.toCharArray());

        try {
            this.client = new MqttClient(broker, clientId, persistence);

            this.client.connect(this.connOpts);
            this.client.setCallback(this);
            Log.d(TAG, "conectado ");
        } catch (MqttException e) {
            Log.d(TAG, "excep "  + e);
            e.printStackTrace();
        }

    }

    public void subscribe(TopicoCallback topicoCallback) {
        try {
            this.client.subscribe(topicoCallback.getIdTopico(), this.qos);

            this.topicos.put(topicoCallback.getIdTopico(), topicoCallback);
            Log.d(TAG, "inscrito em: " + topicoCallback.getIdTopico());
        } catch (MqttException e) {
            Log.d(TAG, "excep "  + e);
            e.printStackTrace();
        }
    }

    public void publish(TopicoCallback topicoCallback, String msg) {

        try {
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(this.qos);
            message.setRetained(false);

            this.client.publish(topicoCallback.getIdTopico(), message);

            this.topicos.put(topicoCallback.getIdTopico(), topicoCallback);
            Log.d(TAG, "publicado em: " + topicoCallback.getIdTopico());
        } catch (MqttException e) {
            Log.d(TAG, "excep "  + e);
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Conexão perdida.");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, message.toString());
        Log.d(TAG, topic);
        TopicoCallback topicoCallback = this.topicos.get(topic);
        topicoCallback.doMessageArrived(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}

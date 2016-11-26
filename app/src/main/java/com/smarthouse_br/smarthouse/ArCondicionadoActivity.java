package com.smarthouse_br.smarthouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.ClienteBroker;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoArStatusCallback;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoArTemperaturaCallback;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class ArCondicionadoActivity extends AppCompatActivity {
    public static final String TOPICO_AR_STATUS = "casa/ar/status";

    public ClienteBroker client;

    public static Boolean arLigado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_condicionado);

        this.client = new ClienteBroker();
        client.subscribe(new TopicoArTemperaturaCallback(ArCondicionadoActivity.this));
        client.subscribe(new TopicoArStatusCallback(ArCondicionadoActivity.this));

    }

    public void ligarAr() {
        this.client.publish(new TopicoArStatusCallback(ArCondicionadoActivity.this), "1");

    }
}

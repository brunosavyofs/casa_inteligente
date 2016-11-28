package com.smarthouse_br.smarthouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.ClienteBroker;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoArStatusCallback;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoArTemperaturaCallback;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class ArCondicionadoActivity extends AppCompatActivity {
    private static final String TAG = "Ar";
    public static final String TOPICO_AR_STATUS = "casa/ar/status";

    public ClienteBroker client;

    public static Boolean arLigado = false;

    public Boolean acionamentoAutomatico = true;

    Switch btnLigarAr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_condicionado);

        this.client = new ClienteBroker();
        client.subscribe(new TopicoArTemperaturaCallback(ArCondicionadoActivity.this));
        client.subscribe(new TopicoArStatusCallback(ArCondicionadoActivity.this));

        btnLigarAr = (Switch) findViewById(R.id.btnLigarAr);
        btnLigarAr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "acionei");
                ArCondicionadoActivity.this.acionamentoAutomatico = isChecked;
            }
        });

    }

    public void ligarAr() {
        this.client.publish(new TopicoArStatusCallback(ArCondicionadoActivity.this), TopicoArStatusCallback.STATUS_ON.toString());

    }

    public void desligarAr() {
        this.client.publish(new TopicoArStatusCallback(ArCondicionadoActivity.this), TopicoArStatusCallback.STATUS_OFF.toString());

    }

    public void toggleAcionamentoAutomatico(View view) {
        Log.d(TAG, "acionei");
        Switch btnLigarAr = (Switch) findViewById(R.id.btnLigarAr);
        this.acionamentoAutomatico = btnLigarAr.isChecked();
        btnLigarAr.toggle();
    }
}

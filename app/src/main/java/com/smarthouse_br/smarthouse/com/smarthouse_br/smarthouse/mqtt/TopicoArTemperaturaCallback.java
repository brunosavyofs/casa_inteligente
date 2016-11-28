package com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarthouse_br.smarthouse.ArCondicionadoActivity;
import com.smarthouse_br.smarthouse.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by bruno on 26/11/16.
 */

public class TopicoArTemperaturaCallback extends Activity implements TopicoCallback {
    private static final String TAG = "ArTemperatura";
    private Context context;

    public static final String ID_TOPICO = "casa/ar/temperatura";

    private static final double TEMPERATURA_ACIONAMENTO = 25.0;

    public TopicoArTemperaturaCallback(Context context) {
        this.context = context;
    }

    @Override
    public String getIdTopico() {
        return this.ID_TOPICO;
    }

    @Override
    public void doMessageArrived(MqttMessage message) throws Exception {
        runOnUiThread(new Runnable(){
            public void run() {
                ArCondicionadoActivity contextoAr = ((ArCondicionadoActivity) context);

                TextView txtTemperatura = (TextView) contextoAr.findViewById(R.id.temperatura);

                txtTemperatura.setText(message.toString() + "º C");

                double temperatura = Double.parseDouble(message.toString());
                Log.d(TAG, message.toString());
                Log.d(TAG, contextoAr.acionamentoAutomatico.toString());
                Log.d(TAG, contextoAr.arLigado.toString());
                if (contextoAr.acionamentoAutomatico) {
                    if (temperatura >= TEMPERATURA_ACIONAMENTO && !contextoAr.arLigado) {
                        contextoAr.ligarAr();
                    }
                    if (temperatura < TEMPERATURA_ACIONAMENTO && contextoAr.arLigado) {
                        contextoAr.desligarAr();
                    }
                }
            }
        });
    }
}

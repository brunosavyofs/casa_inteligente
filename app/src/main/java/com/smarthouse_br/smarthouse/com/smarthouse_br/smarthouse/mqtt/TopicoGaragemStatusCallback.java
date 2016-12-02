package com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt;

import android.app.Activity;
import android.content.Context;

import com.smarthouse_br.smarthouse.AlarmeActivity;
import com.smarthouse_br.smarthouse.GaragemActivity;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by bruno on 26/11/16.
 */

public class TopicoGaragemStatusCallback extends Activity implements TopicoCallback {
    private static final String TAG = "GaragemStatus";
    private Context context;

    public static final String ID_TOPICO = "casa/garagem/status";


    public TopicoGaragemStatusCallback(Context context) {
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
                GaragemActivity contextoGaragem = ((GaragemActivity) context);

                Integer novoStatus = Integer.parseInt(message.toString());

                contextoGaragem.ocuparGaragem(novoStatus);
            }
        });
    }
}

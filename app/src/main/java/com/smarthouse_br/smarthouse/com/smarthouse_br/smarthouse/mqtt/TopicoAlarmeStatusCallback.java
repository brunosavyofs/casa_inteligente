package com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.smarthouse_br.smarthouse.AlarmeActivity;
import com.smarthouse_br.smarthouse.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by bruno on 26/11/16.
 */

public class TopicoAlarmeStatusCallback extends Activity implements TopicoCallback {
    private static final String TAG = "AlarmeStatus";
    private Context context;

    public static final String ID_TOPICO = "casa/alarme/status";


    public TopicoAlarmeStatusCallback(Context context) {
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
                AlarmeActivity contextoAlarme = ((AlarmeActivity) context);

                Integer novoStatus = Integer.parseInt(message.toString());

                if (novoStatus == contextoAlarme.ALARME_DISPARADO) {
                    contextoAlarme.dispararAlarme();
                }
            }
        });
    }
}

package com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.smarthouse_br.smarthouse.ArCondicionadoActivity;
import com.smarthouse_br.smarthouse.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by bruno on 26/11/16.
 */

public class TopicoArStatusCallback extends Activity implements TopicoCallback {
    private static final String TAG = "ArStatus";
    private Context context;

    public static final String ID_TOPICO = "casa/ar/status";

    public static final Integer STATUS_ON = Integer.valueOf(1);
    public static final Integer STATUS_OFF = Integer.valueOf(0);


    public TopicoArStatusCallback(Context context) {
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

                Boolean ligar = (Integer.parseInt(message.toString()) == TopicoArStatusCallback.STATUS_ON);
                Log.d(TAG, ligar.toString());
                Log.d(TAG,  contextoAr.arLigado.toString());
                if (ligar != contextoAr.arLigado) {

                    if (ligar) {
                        txtTemperatura.setTextColor(Color.BLUE);
                    } else {
                        txtTemperatura.setTextColor(Color.RED);
                    }
                    contextoAr.arLigado = !contextoAr.arLigado;
                }
            }
        });
    }
}

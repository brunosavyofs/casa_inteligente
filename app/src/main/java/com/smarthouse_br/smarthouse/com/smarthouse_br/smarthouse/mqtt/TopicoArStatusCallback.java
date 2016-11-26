package com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.smarthouse_br.smarthouse.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by bruno on 26/11/16.
 */

public class TopicoArStatusCallback extends Activity implements TopicoCallback {
    private static final String TAG = "ArStatus";
    private Context context;

    public final String ID_TOPICO = "casa/ar/status";

    public static final double STATUS_ON = 1;
    public static final double STATUS_OFF = 0;

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
                TextView txtTemperatura = (TextView) ((Activity) context).findViewById(R.id.temperatura);

                int status = Integer.parseInt(message.toString());

                if (status == TopicoArStatusCallback.STATUS_ON) {
                    txtTemperatura.setTextColor(Color.BLUE);
                } else {
                    txtTemperatura.setTextColor(Color.RED);
                }
            }
        });
    }
}

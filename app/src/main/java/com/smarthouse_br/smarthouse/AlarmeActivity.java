package com.smarthouse_br.smarthouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.ClienteBroker;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoAlarmeStatusCallback;

public class AlarmeActivity extends AppCompatActivity {
    private static final String TAG = "Alarme";

    public static final String DESC_ALARME_DESATIVADO = "DESATIVADO";
    public static final String DESC_ALARME_ACIONADO = "ACIONADO";
    public static final String DESC_ALARME_DISPARADO = "DISPARADO";

    public static final int COLOR_ALARME_DESATIVADO = Color.parseColor("#90a4ae");
    public static final int COLOR_ALARME_ACIONADO = Color.parseColor("#0277bd");
    public static final int COLOR_ALARME_DISPARADO = Color.parseColor("#d50000");

    public static final Integer ALARME_ACIONADO = 0;
    public static final Integer ALARME_DISPARADO = 1;
    public static final Integer ALARME_DESATIVADO = 2;

    public ClienteBroker client;

    Switch btnAcionarAlarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);

        this.client = new ClienteBroker();
        client.subscribe(new TopicoAlarmeStatusCallback(AlarmeActivity.this));

        btnAcionarAlarme = (Switch) findViewById(R.id.btnAcionarAlarme);
        btnAcionarAlarme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer statusAlarme = isChecked ? ALARME_ACIONADO : ALARME_DESATIVADO;

                if (statusAlarme == ALARME_DESATIVADO) {
                    AlarmeActivity.this.desativarAlarme();
                }
                if (statusAlarme == ALARME_ACIONADO) {
                    AlarmeActivity.this.acionarAlarme();
                }
            }
        });
    }

    private void modificarStatusAlarme(String texto, int cor, Integer status) {
        TextView txtStatusAlarme = (TextView) findViewById(R.id.txtStatusAlarme);

        txtStatusAlarme.setText(texto);
        txtStatusAlarme.setTextColor(cor);

        if (status != ALARME_DISPARADO) {
            this.client.publish(new TopicoAlarmeStatusCallback(AlarmeActivity.this), status.toString());
        }
    }

    public void desativarAlarme() {
        modificarStatusAlarme(DESC_ALARME_DESATIVADO, COLOR_ALARME_DESATIVADO,
                ALARME_DESATIVADO);
    }

    public void acionarAlarme() {
        modificarStatusAlarme(DESC_ALARME_ACIONADO, COLOR_ALARME_ACIONADO,
                ALARME_ACIONADO);
    }

    public void dispararAlarme() {
        modificarStatusAlarme(DESC_ALARME_DISPARADO, COLOR_ALARME_DISPARADO,
                ALARME_DISPARADO);
    }
}

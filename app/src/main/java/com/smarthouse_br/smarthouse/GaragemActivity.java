package com.smarthouse_br.smarthouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.ClienteBroker;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoGaragemStatusCallback;

public class GaragemActivity extends AppCompatActivity {
    private static final String TAG = "Garagem";

    public static final String DESC_GARAGEM_DESOCUPADA = "DESOCUPADA";
    public static final String DESC_GARAGEM_OCUPADA = "OCUPADA";

    public static final int COLOR_GARAGEM_DESOCUPADA = Color.parseColor("#2e7d32");
    public static final int COLOR_GARAGEM_OCUPADA = Color.parseColor("#dd2c00");

    public static final Integer GARAGEM_DESOCUPADA = 0;
    public static final Integer GARAGEM_OCUPADA = 1;

    public ClienteBroker client;
    public boolean acionamentoAutomatico = true;

    Switch btnAcionarGaragem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garagem);

        this.client = new ClienteBroker();
        client.subscribe(new TopicoGaragemStatusCallback(GaragemActivity.this));

        btnAcionarGaragem = (Switch) findViewById(R.id.btnStatusGaragem);
        btnAcionarGaragem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                acionamentoAutomatico = isChecked;

                if (!acionamentoAutomatico) {
                    // informar acionamento interrompido

//                    if (status != GARAGEM_OCUPADA) {
//                        this.client.publish(new TopicoAlarmeStatusCallback(GaragemActivity.this), status.toString());
//                    }
                }
            }
        });
    }

    private void modificarStatusGaragem(String texto, int cor, Integer status) {
        TextView txtStatusGaragem = (TextView) findViewById(R.id.txtStatusGaragem);

        txtStatusGaragem.setText(texto);
        txtStatusGaragem.setTextColor(cor);
    }

    public void ocuparGaragem(Integer status) {
        if (status == GARAGEM_OCUPADA) {
            modificarStatusGaragem(DESC_GARAGEM_OCUPADA, COLOR_GARAGEM_OCUPADA, status);
        }
        if (status == GARAGEM_DESOCUPADA) {
            modificarStatusGaragem(DESC_GARAGEM_DESOCUPADA, COLOR_GARAGEM_DESOCUPADA, status);
        }
    }
}

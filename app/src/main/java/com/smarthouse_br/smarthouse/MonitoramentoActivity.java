package com.smarthouse_br.smarthouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MonitoramentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoramento);
    }

    public void abreTelaArCondicionado(View v) {
        // Invoca a tela de monitoramento do ar condicionado
        Intent chamada = new Intent(getApplicationContext(), ArCondicionadoActivity.class);
        startActivity(chamada);
    }

    public void abreTelaAlarme(View v) {
        // Invoca a tela de monitoramento do ar condicionado
        Intent chamada = new Intent(getApplicationContext(), AlarmeActivity.class);
        startActivity(chamada);
    }
}

package com.smarthouse_br.smarthouse;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.ClienteBroker;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoArStatusCallback;
import com.smarthouse_br.smarthouse.com.smarthouse_br.smarthouse.mqtt.TopicoArTemperaturaCallback;

public class ArCondicionadoActivity extends AppCompatActivity {
    private static final String TAG = "Ar";
    private static final String SSID_REDE_CASA = "\"WaynetV2\"";

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

        SeekBar seekTemp = (SeekBar) findViewById(R.id.seekTemperatura);
        TextView tempAcionamento = (TextView) findViewById(R.id.txtTemperaturaAcionamento);
        seekTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tempAcionamento.setText(String.valueOf(progress) + TopicoArTemperaturaCallback.STRING_GRAUS);
                if (progress != TopicoArTemperaturaCallback.temperaturaAcionamento) {
                    TopicoArTemperaturaCallback.temperaturaAcionamento = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekTemp.setProgress((int) Math.round(TopicoArTemperaturaCallback.temperaturaAcionamento));

    }

    public void ligarAr() {
        this.client.publish(new TopicoArStatusCallback(ArCondicionadoActivity.this), TopicoArStatusCallback.STATUS_ON.toString());
    }

    public void desligarAr() {
        this.client.publish(new TopicoArStatusCallback(ArCondicionadoActivity.this), TopicoArStatusCallback.STATUS_OFF.toString());
    }

    public boolean verificarRedeSemFio() {
        WifiManager wifiManager = (WifiManager) ArCondicionadoActivity.this.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getSSID().equals(this.SSID_REDE_CASA);
    }
}

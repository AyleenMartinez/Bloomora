package com.devst.bloomora;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SesionEnfoqueActivity extends AppCompatActivity {

    private TextView tvTemporizador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sesion_enfoque);

        tvTemporizador = findViewById(R.id.tvTemporizador);

        SharedPreferences preferencias = getSharedPreferences(
                "preferenciasBloomora",
                MODE_PRIVATE
        );

        int duracion = preferencias.getInt("duracionEnfoque", 25);

        tvTemporizador.setText(getString(R.string.formato_temporizador, duracion));

    }
}
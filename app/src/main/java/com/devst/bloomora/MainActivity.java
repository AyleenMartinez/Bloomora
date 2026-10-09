package com.devst.bloomora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnEnfocarme;
    private Button btnPersonalizar;
    private TextView tvSaludo;
    private Button btnPorHacer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnEnfocarme = findViewById(R.id.btnEnfocarme);
        btnPersonalizar = findViewById(R.id.btnPersonalizar);
        tvSaludo = findViewById(R.id.tvSaludo);
        btnPorHacer = findViewById(R.id.btnPorHacer);


        btnEnfocarme.setOnClickListener(view -> {
            Intent irAEnfoque = new Intent(MainActivity.this, SesionEnfoqueActivity.class);
            startActivity(irAEnfoque);
        });

        btnPersonalizar.setOnClickListener(view -> {
            Intent irAPersonalizacion = new Intent(MainActivity.this, PersonalizacionActivity.class);
            startActivity(irAPersonalizacion);
        });

        btnPorHacer.setOnClickListener(view -> {

            Intent irAPorHacer = new Intent(MainActivity.this, PorHacerActivity.class);
            startActivity(irAPorHacer);
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferencias = getSharedPreferences("preferenciasBloomora", MODE_PRIVATE);

        String nombre = preferencias.getString("nombreUsuario", "");

        if (nombre.isEmpty()) {
            tvSaludo.setText(R.string.saludo_generico);
        } else {
            tvSaludo.setText(getString(R.string.saludo_personalizado, nombre));
        }
    }
}
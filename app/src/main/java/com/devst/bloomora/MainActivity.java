package com.devst.bloomora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.provider.CalendarContract;
import android.widget.Toast;

import android.content.ContentUris;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    private Button btnEnfocarme;
    private Button btnPersonalizar;
    private TextView tvSaludo;
    private Button btnPorHacer;
    private TextView tvResumenEstado;
    private Button btnJardin;
    private Button btnCalendario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnEnfocarme = findViewById(R.id.btnEnfocarme);
        btnPersonalizar = findViewById(R.id.btnPersonalizar);
        tvSaludo = findViewById(R.id.tvSaludo);
        btnPorHacer = findViewById(R.id.btnPorHacer);
        tvResumenEstado = findViewById(R.id.tvResumenEstado);
        btnJardin = findViewById(R.id.btnJardin);
        btnCalendario = findViewById(R.id.btnCalendario);


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

        btnJardin.setOnClickListener(view -> {

            Intent irAJardin = new Intent(MainActivity.this, MiJardinActivity.class);
            startActivity(irAJardin);
        });

        btnCalendario.setOnClickListener(view -> {

            long ahora = System.currentTimeMillis();

            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, ahora);

            Intent abrirCalendario = new Intent(Intent.ACTION_VIEW);
            abrirCalendario.setData(builder.build());

            try {
                startActivity(abrirCalendario);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.error_sin_calendario, Toast.LENGTH_SHORT).show();
            }
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

        GestorTareas gestorTareas = new GestorTareas(this);
        Tarea tareaActiva = gestorTareas.obtenerTareaActiva();

        if (tareaActiva == null) {
            tvResumenEstado.setText(R.string.resumen_sin_tarea);
        } else {
            tvResumenEstado.setText(getString(R.string.resumen_tarea_activa, tareaActiva.getEstado()));
        }
    }
}
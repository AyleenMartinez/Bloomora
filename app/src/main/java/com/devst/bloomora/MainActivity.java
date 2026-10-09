package com.devst.bloomora;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvSaludo;

    private ImageButton btnAjustes;
    private Button btnEnfocarme;
    private Button btnPorHacer;
    private Button btnJardin;
    private Button btnCalendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvSaludo = findViewById(R.id.tvSaludo);

        btnAjustes = findViewById(R.id.btnAjustes);
        btnEnfocarme = findViewById(R.id.btnEnfocarme);
        btnPorHacer = findViewById(R.id.btnPorHacer);
        btnJardin = findViewById(R.id.btnJardin);
        btnCalendario = findViewById(R.id.btnCalendario);

        btnAjustes.setOnClickListener(view -> {
            Intent irAPersonalizacion = new Intent(MainActivity.this, PersonalizacionActivity.class);
            startActivity(irAPersonalizacion);
        });

        btnEnfocarme.setOnClickListener(view -> {
            Intent irAEnfoque = new Intent(MainActivity.this, SesionEnfoqueActivity.class);
            startActivity(irAEnfoque);
        });

        btnPorHacer.setOnClickListener(view -> {
            Intent irAPorHacer = new Intent(MainActivity.this, PorHacerActivity.class);
            startActivity(irAPorHacer);
        });

        btnJardin.setOnClickListener(view -> {
            Intent irAJardin = new Intent(MainActivity.this, MiJardinActivity.class);
            startActivity(irAJardin);
        });

        btnCalendario.setOnClickListener(view -> abrirCalendario());
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

    private void abrirCalendario() {

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
    }
}
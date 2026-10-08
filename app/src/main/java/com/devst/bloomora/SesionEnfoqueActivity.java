package com.devst.bloomora;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SesionEnfoqueActivity extends AppCompatActivity {

    private TextView tvTemporizador;
    private Button btnLugarEstudio;
    private Button btnMaterialEstudio;
    private Button btnEvidencia;
    private Button btnBluetooth;
    private Button btnAgendarSesion;

    private Uri fotoUri;

    private final ActivityResultLauncher<Intent> resultadoCamara = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultado -> {

        if (resultado.getResultCode() == Activity.RESULT_OK) {
            Toast.makeText(this, R.string.foto_guardada, Toast.LENGTH_SHORT).show();

        } else {

            if (fotoUri != null) {
                getContentResolver().delete(fotoUri, null, null);
                fotoUri = null;
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sesion_enfoque);

        tvTemporizador = findViewById(R.id.tvTemporizador);
        btnLugarEstudio = findViewById(R.id.btnLugarEstudio);
        btnMaterialEstudio = findViewById(R.id.btnMaterialEstudio);
        btnEvidencia = findViewById(R.id.btnEvidencia);
        btnBluetooth = findViewById(R.id.btnBluetooth);
        btnAgendarSesion = findViewById(R.id.btnAgendarSesion);

        SharedPreferences preferencias = getSharedPreferences("preferenciasBloomora", MODE_PRIVATE);

        int duracion = preferencias.getInt("duracionEnfoque", 25);

        tvTemporizador.setText(getString(R.string.formato_temporizador, duracion));


        // Intent implícito #1: Google Maps
        btnLugarEstudio.setOnClickListener(view -> {

            Uri ubicacion = Uri.parse("geo:0,0?q=" + Uri.encode("bibliotecas y lugares para estudiar"));
            Intent abrirMapa = new Intent(Intent.ACTION_VIEW, ubicacion);

            try {
                startActivity(abrirMapa);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.error_sin_mapa, Toast.LENGTH_SHORT).show();
            }
        });


        // Intent implícito #2: Página web
        btnMaterialEstudio.setOnClickListener(view -> {

            Uri pagina = Uri.parse("https://www.ipsantotomas.cl/areas-y-carreras/area-de-informatica/");
            Intent abrirMaterial = new Intent(Intent.ACTION_VIEW, pagina);

            try {
                startActivity(abrirMaterial);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.error_sin_navegador, Toast.LENGTH_SHORT).show();
            }
        });


        // Intent implícito #5: Cámara
        btnEvidencia.setOnClickListener(view -> {

            ContentValues valoresFoto = new ContentValues();

            valoresFoto.put(MediaStore.Images.Media.DISPLAY_NAME, "Bloomora_" + System.currentTimeMillis() + ".jpg");
            valoresFoto.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            fotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valoresFoto);

            if (fotoUri != null) {

                Intent abrirCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                abrirCamara.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                abrirCamara.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                abrirCamara.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    resultadoCamara.launch(abrirCamara);

                } catch (ActivityNotFoundException e) {

                    getContentResolver().delete(fotoUri, null, null);
                    fotoUri = null;

                    Toast.makeText(this, R.string.error_sin_camara, Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Intent implícito #7: Bluetooth
        btnBluetooth.setOnClickListener(view -> {

            Intent abrirBluetooth = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);

            try {
                startActivity(abrirBluetooth);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.error_sin_bluetooth, Toast.LENGTH_SHORT).show();
            }
        });


        // Intent implícito #8: Calendario
        btnAgendarSesion.setOnClickListener(view -> {

            long inicio = System.currentTimeMillis() + (5 * 60 * 1000);
            long fin = inicio + (duracion * 60L * 1000L);

            Intent agendarSesion = new Intent(Intent.ACTION_INSERT);

            agendarSesion.setData(CalendarContract.Events.CONTENT_URI);
            agendarSesion.putExtra(CalendarContract.Events.TITLE, getString(R.string.titulo_evento_calendario));
            agendarSesion.putExtra(CalendarContract.Events.DESCRIPTION, getString(R.string.descripcion_evento_calendario));
            agendarSesion.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicio);
            agendarSesion.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, fin);

            try {
                startActivity(agendarSesion);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.error_sin_calendario, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
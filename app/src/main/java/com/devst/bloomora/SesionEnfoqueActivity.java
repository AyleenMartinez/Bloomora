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

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;

import android.view.View;



public class SesionEnfoqueActivity extends AppCompatActivity {

    private TextView tvTemporizador;
    private TextView tvAsignatura;
    private TextView tvTarea;
    private TextView tvEstado;
    private Button btnLugarEstudio;
    private Button btnMaterialEstudio;
    private Button btnEvidencia;
    private Button btnBluetooth;
    private Button btnAgendarSesion;
    private Button btnIniciarSesion;
    private Button btnVerDetalle;
    private Button btnVolver;

    private Uri fotoUri;

    private final ActivityResultLauncher<Intent>
            resultadoCamara = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultado -> {

        if (resultado.getResultCode() == Activity.RESULT_OK) {
            Toast.makeText(this, R.string.foto_guardada, Toast.LENGTH_SHORT).show();

        } else {

            if (fotoUri != null) {
                getContentResolver().delete(fotoUri, null, null);
                fotoUri = null;
            }
        }
    });
    private final ActivityResultLauncher<String> permisoCamara = registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {

        if (concedido) {
            abrirCamara();
        } else {
            Toast.makeText(this, R.string.permiso_camara_denegado, Toast.LENGTH_SHORT).show();
        }
    });

    private final BroadcastReceiver receptorTemporizador = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long segundosRestantes = intent.getLongExtra(TemporizadorService.EXTRA_SEGUNDOS, 0);

            int minutos = (int) (segundosRestantes / 60);
            int segundos = (int) (segundosRestantes % 60);

            tvTemporizador.setText(getString(R.string.formato_temporizador_segundos, minutos, segundos));
        }
    };

    private final BroadcastReceiver receptorSesionCompletada = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            GestorTareas gestorTareas = new GestorTareas(SesionEnfoqueActivity.this);
            gestorTareas.completarTareaActiva();

            tvTemporizador.setText(R.string.temporizador_finalizado);
            btnIniciarSesion.setText(R.string.iniciar_sesion);

            Toast.makeText(SesionEnfoqueActivity.this, R.string.sesion_finalizada, Toast.LENGTH_SHORT).show();

            cargarTarea();
        }
    };

    private final ActivityResultLauncher<String> permisoNotificaciones = registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {

        if (concedido) {
            iniciarTemporizador();
        } else {
            Toast.makeText(this, R.string.permiso_notificacion_denegado, Toast.LENGTH_SHORT).show();
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
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnVerDetalle = findViewById(R.id.btnVerDetalle);
        btnVolver = findViewById(R.id.btnVolver);
        tvAsignatura = findViewById(R.id.tvAsignatura);
        tvTarea = findViewById(R.id.tvTarea);
        tvEstado = findViewById(R.id.tvEstado);

        btnVolver.setOnClickListener(view ->
                finish()
        );


        SharedPreferences preferencias = getSharedPreferences("preferenciasBloomora", MODE_PRIVATE);
        int duracionSegundos = preferencias.getInt("duracionSegundos", 25 * 60);

        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;

        tvTemporizador.setText(getString(R.string.formato_temporizador_segundos, minutos, segundos));


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

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                abrirCamara();

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                Toast.makeText(this, R.string.explicacion_permiso_camara, Toast.LENGTH_SHORT).show();
                permisoCamara.launch(Manifest.permission.CAMERA);

            } else {

                permisoCamara.launch(Manifest.permission.CAMERA);
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
            long fin = inicio + (duracionSegundos * 60L * 1000L);

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

        btnIniciarSesion.setOnClickListener(view -> {

            if (TemporizadorService.estaActiva()) {

                confirmarDetenerSesion();

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);

            } else {

                iniciarTemporizador();
            }
        });

        // Intent explícito #1: Detalle de tarea
        btnVerDetalle.setOnClickListener(view -> {

            Intent irADetalleTarea = new Intent(SesionEnfoqueActivity.this, DetalleTareaActivity.class);

            irADetalleTarea.putExtra("asignatura", tvAsignatura.getText().toString());
            irADetalleTarea.putExtra("tarea", tvTarea.getText().toString());
            irADetalleTarea.putExtra("estado", tvEstado.getText().toString());

            startActivity(irADetalleTarea);
        });

    }

    private void cargarTarea() {

        GestorTareas gestorTareas = new GestorTareas(this);
        Tarea tareaActiva = gestorTareas.obtenerTareaActiva();

        if (tareaActiva == null) {

            tvAsignatura.setText(R.string.sin_tarea_activa);
            tvTarea.setText(R.string.selecciona_tarea);
            tvEstado.setText(R.string.sin_seleccionar);

            btnVerDetalle.setVisibility(View.GONE);
            btnIniciarSesion.setVisibility(View.GONE);

            return;
        }

        tvAsignatura.setText(tareaActiva.getAsignatura());
        tvTarea.setText(tareaActiva.getNombre());
        tvEstado.setText(tareaActiva.getEstado());

        btnVerDetalle.setVisibility(View.VISIBLE);
        btnIniciarSesion.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTarea();
    }

    private void abrirCamara() {

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
        else {
            Toast.makeText(this, R.string.error_guardar_foto, Toast.LENGTH_SHORT).show();
        }

    }

    private void iniciarTemporizador() {

        SharedPreferences preferencias = getSharedPreferences("preferenciasBloomora", MODE_PRIVATE);
        int duracionSegundos = preferencias.getInt("duracionSegundos", 25 * 60);

        GestorTareas gestorTareas = new GestorTareas(this);
        gestorTareas.marcarTareaActivaCreciendo();
        cargarTarea();

        Intent servicioTemporizador = new Intent(this, TemporizadorService.class);
        servicioTemporizador.putExtra("duracionSegundos", duracionSegundos);

        ContextCompat.startForegroundService(this, servicioTemporizador);

        btnIniciarSesion.setText(R.string.detener_sesion);
    }

    private void detenerTemporizador() {

        Intent detenerServicio = new Intent(this, TemporizadorService.class);
        detenerServicio.setAction(TemporizadorService.ACCION_DETENER);
        startService(detenerServicio);

        SharedPreferences preferencias = getSharedPreferences("preferenciasBloomora", MODE_PRIVATE);
        int duracionSegundos = preferencias.getInt("duracionSegundos", 25 * 60);

        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;

        tvTemporizador.setText(getString(R.string.formato_temporizador_segundos, minutos, segundos));
        btnIniciarSesion.setText(R.string.iniciar_sesion);

        Toast.makeText(this, R.string.sesion_detenida, Toast.LENGTH_SHORT).show();
    }

    private void confirmarDetenerSesion() {

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_detener_sesion)
                .setMessage(R.string.mensaje_detener_sesion)
                .setNegativeButton(R.string.continuar_sesion, null)
                .setPositiveButton(R.string.confirmar_detener, (dialog, which) -> detenerTemporizador())
                .create();

        dialogo.setOnShowListener(dialog -> {

            if (dialogo.getWindow() != null) {
                dialogo.getWindow().setBackgroundDrawableResource(R.drawable.bg_tarjeta);
            }

            dialogo.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.verde_oscuro));
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.verde_oscuro));
        });

        dialogo.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filtroTemporizador = new IntentFilter(TemporizadorService.ACCION_ACTUALIZAR);
        IntentFilter filtroCompletada = new IntentFilter(TemporizadorService.ACCION_COMPLETADA);

        ContextCompat.registerReceiver(this, receptorTemporizador, filtroTemporizador, ContextCompat.RECEIVER_NOT_EXPORTED);
        ContextCompat.registerReceiver(this, receptorSesionCompletada, filtroCompletada, ContextCompat.RECEIVER_NOT_EXPORTED);

        if (TemporizadorService.estaActiva()) {
            btnIniciarSesion.setText(R.string.detener_sesion);
        } else {
            btnIniciarSesion.setText(R.string.iniciar_sesion);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(receptorTemporizador);
        unregisterReceiver(receptorSesionCompletada);
    }

}
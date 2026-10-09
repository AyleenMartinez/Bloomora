package com.devst.bloomora;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

public class TemporizadorService extends Service {

    public static final String ACCION_ACTUALIZAR = "com.devst.bloomora.ACTUALIZAR_TEMPORIZADOR";
    public static final String ACCION_COMPLETADA = "com.devst.bloomora.SESION_COMPLETADA";
    public static final String ACCION_DETENER = "com.devst.bloomora.DETENER_TEMPORIZADOR";

    private static boolean sesionActiva = false;

    public static boolean estaActiva() {
        return sesionActiva;
    }
    public static final String EXTRA_SEGUNDOS = "segundosRestantes";

    private static final String ID_CANAL = "canal_enfoque";
    private static final int ID_NOTIFICACION = 1;

    private Thread hiloTemporizador;
    private NotificationManager notificationManager;


    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = getSystemService(NotificationManager.class);
        crearCanalNotificacion();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACCION_DETENER.equals(intent.getAction())) {

            sesionActiva = false;

            if (hiloTemporizador != null) {
                hiloTemporizador.interrupt();
            }

            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();

            return START_NOT_STICKY;
        }

        if (sesionActiva) {
            return START_NOT_STICKY;
        }

        sesionActiva = true;

        int duracion = intent != null ? intent.getIntExtra("duracion", 25) : 25;
        long segundosTotales = duracion * 60L;

        Notification notificacion = crearNotificacion(segundosTotales);

        int tipoServicio = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            tipoServicio = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
        }

        ServiceCompat.startForeground(this, ID_NOTIFICACION, notificacion, tipoServicio);

        if (hiloTemporizador != null) {
            hiloTemporizador.interrupt();
        }

        hiloTemporizador = new Thread(() -> {

            long segundosRestantes = segundosTotales;

            while (segundosRestantes >= 0 && !Thread.currentThread().isInterrupted()) {

                enviarActualizacion(segundosRestantes);
                notificationManager.notify(ID_NOTIFICACION, crearNotificacion(segundosRestantes));

                if (segundosRestantes == 0) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                segundosRestantes--;
            }

            if (!Thread.currentThread().isInterrupted()) {
                sesionActiva = false;
                enviarSesionCompletada();
                stopForeground(STOP_FOREGROUND_REMOVE);
                stopSelf();
            }
        });

        hiloTemporizador.start();

        return START_NOT_STICKY;
    }


    private void crearCanalNotificacion() {

        NotificationChannel canal = new NotificationChannel(
                ID_CANAL,
                getString(R.string.canal_enfoque),
                NotificationManager.IMPORTANCE_LOW
        );

        notificationManager.createNotificationChannel(canal);
    }


    private Notification crearNotificacion(long segundosRestantes) {

        int minutos = (int) (segundosRestantes / 60);
        int segundos = (int) (segundosRestantes % 60);

        String tiempo = getString(R.string.formato_temporizador_segundos, minutos, segundos);

        return new NotificationCompat.Builder(this, ID_CANAL)
                .setSmallIcon(R.drawable.logo_bloomora)
                .setContentTitle(getString(R.string.notificacion_enfoque_titulo))
                .setContentText(getString(R.string.notificacion_enfoque_tiempo, tiempo))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }


    private void enviarActualizacion(long segundosRestantes) {

        Intent actualizarTemporizador = new Intent(ACCION_ACTUALIZAR);
        actualizarTemporizador.setPackage(getPackageName());
        actualizarTemporizador.putExtra(EXTRA_SEGUNDOS, segundosRestantes);

        sendBroadcast(actualizarTemporizador);
    }


    private void enviarSesionCompletada() {

        Intent sesionCompletada = new Intent(ACCION_COMPLETADA);
        sesionCompletada.setPackage(getPackageName());

        sendBroadcast(sesionCompletada);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        sesionActiva = false;

        if (hiloTemporizador != null) {
            hiloTemporizador.interrupt();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
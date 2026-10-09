package com.devst.bloomora;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GestorTareas {

    private static final String NOMBRE_PREFERENCIAS = "tareasBloomora";
    private static final String CLAVE_LISTA = "listaTareas";
    private static final String CLAVE_ACTIVA = "indiceTareaActiva";

    private final SharedPreferences preferencias;

    public GestorTareas(Context context) {
        preferencias = context.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
    }

    public ArrayList<Tarea> cargarTareas() {

        ArrayList<Tarea> tareas = new ArrayList<>();
        String jsonGuardado = preferencias.getString(CLAVE_LISTA, "[]");

        try {

            JSONArray listaJson = new JSONArray(jsonGuardado);

            for (int i = 0; i < listaJson.length(); i++) {

                JSONObject tareaJson = listaJson.getJSONObject(i);

                String asignatura = tareaJson.getString("asignatura");
                String nombre = tareaJson.getString("nombre");
                String estado = tareaJson.getString("estado");

                tareas.add(new Tarea(asignatura, nombre, estado));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tareas;
    }

    public void guardarTareas(ArrayList<Tarea> tareas) {

        JSONArray listaJson = new JSONArray();

        try {

            for (Tarea tarea : tareas) {

                JSONObject tareaJson = new JSONObject();

                tareaJson.put("asignatura", tarea.getAsignatura());
                tareaJson.put("nombre", tarea.getNombre());
                tareaJson.put("estado", tarea.getEstado());

                listaJson.put(tareaJson);
            }

            preferencias.edit().putString(CLAVE_LISTA, listaJson.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void agregarTarea(Tarea tarea) {

        ArrayList<Tarea> tareas = cargarTareas();
        tareas.add(tarea);
        guardarTareas(tareas);
    }

    public void guardarTareaActiva(int posicion) {
        preferencias.edit().putInt(CLAVE_ACTIVA, posicion).apply();
    }

    public int obtenerIndiceTareaActiva() {
        return preferencias.getInt(CLAVE_ACTIVA, -1);
    }

    public Tarea obtenerTareaActiva() {

        ArrayList<Tarea> tareas = cargarTareas();
        int posicion = obtenerIndiceTareaActiva();

        if (posicion < 0 || posicion >= tareas.size()) {
            return null;
        }

        return tareas.get(posicion);
    }

    public void completarTareaActiva() {

        ArrayList<Tarea> tareas = cargarTareas();
        int posicion = obtenerIndiceTareaActiva();

        if (posicion < 0 || posicion >= tareas.size()) {
            return;
        }

        tareas.get(posicion).setEstado("Florecida");

        guardarTareas(tareas);

        preferencias.edit().putInt(CLAVE_ACTIVA, -1).apply();
    }

    public void marcarTareaActivaCreciendo() {

        ArrayList<Tarea> tareas = cargarTareas();
        int posicion = obtenerIndiceTareaActiva();

        if (posicion < 0 || posicion >= tareas.size()) {
            return;
        }

        Tarea tarea = tareas.get(posicion);

        if (tarea.getEstado().equals("Semilla")) {
            tarea.setEstado("Creciendo");
            guardarTareas(tareas);
        }
    }
}
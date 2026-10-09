package com.devst.bloomora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PorHacerActivity extends AppCompatActivity {

    private EditText etAsignatura;
    private EditText etTarea;
    private Button btnGuardarTarea;
    private Button btnVolver;
    private LinearLayout contenedorTareas;
    private TextView tvSinTareas;

    private GestorTareas gestorTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_por_hacer);

        etAsignatura = findViewById(R.id.etAsignatura);
        etTarea = findViewById(R.id.etTarea);
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea);
        btnVolver = findViewById(R.id.btnVolver);
        contenedorTareas = findViewById(R.id.contenedorTareas);
        tvSinTareas = findViewById(R.id.tvSinTareas);

        gestorTareas = new GestorTareas(this);

        mostrarTareas();

        btnGuardarTarea.setOnClickListener(view -> agregarTarea());
        btnVolver.setOnClickListener(view -> finish());
    }

    private void agregarTarea() {

        String asignatura = etAsignatura.getText().toString().trim();
        String nombre = etTarea.getText().toString().trim();

        if (asignatura.isEmpty()) {
            etAsignatura.setError(getString(R.string.error_asignatura_vacia));
            return;
        }

        if (nombre.isEmpty()) {
            etTarea.setError(getString(R.string.error_tarea_vacia));
            return;
        }

        Tarea nuevaTarea = new Tarea(asignatura, nombre, "Semilla");
        gestorTareas.agregarTarea(nuevaTarea);

        etAsignatura.setText("");
        etTarea.setText("");

        Toast.makeText(this, R.string.tarea_guardada, Toast.LENGTH_SHORT).show();

        mostrarTareas();
    }

    private void mostrarTareas() {

        ArrayList<Tarea> tareas = gestorTareas.cargarTareas();
        int indiceActivo = gestorTareas.obtenerIndiceTareaActiva();

        contenedorTareas.removeAllViews();

        boolean hayPendientes = false;

        for (int i = 0; i < tareas.size(); i++) {

            Tarea tarea = tareas.get(i);

            if (tarea.getEstado().equals("Florecida")) {
                continue;
            }

            hayPendientes = true;

            View item = getLayoutInflater().inflate(R.layout.item_tarea, contenedorTareas, false);

            TextView tvNombre = item.findViewById(R.id.tvItemNombreTarea);
            TextView tvAsignatura = item.findViewById(R.id.tvItemAsignatura);
            TextView tvEstado = item.findViewById(R.id.tvItemEstado);
            Button btnUsar = item.findViewById(R.id.btnUsarTarea);

            tvNombre.setText(tarea.getNombre());
            tvAsignatura.setText(tarea.getAsignatura());
            tvEstado.setText(tarea.getEstado());

            int posicion = i;

            if (posicion == indiceActivo) {
                btnUsar.setText(R.string.tarea_activa);
                btnUsar.setEnabled(false);
            } else {
                btnUsar.setText(R.string.usar_para_enfocarme);

                btnUsar.setOnClickListener(view -> {
                    gestorTareas.guardarTareaActiva(posicion);
                    Toast.makeText(this, R.string.tarea_seleccionada, Toast.LENGTH_SHORT).show();
                    mostrarTareas();
                });
            }

            contenedorTareas.addView(item);
        }

        if (hayPendientes) {
            tvSinTareas.setVisibility(View.GONE);
        } else {
            tvSinTareas.setVisibility(View.VISIBLE);
        }
    }
}
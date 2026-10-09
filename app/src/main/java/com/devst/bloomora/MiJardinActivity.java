package com.devst.bloomora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MiJardinActivity extends AppCompatActivity {

    private LinearLayout contenedorJardin;
    private TextView tvSinFlores;
    private Button btnVolver;

    private GestorTareas gestorTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_jardin);

        contenedorJardin = findViewById(R.id.contenedorJardin);
        tvSinFlores = findViewById(R.id.tvSinFlores);
        btnVolver = findViewById(R.id.btnVolver);

        gestorTareas = new GestorTareas(this);

        mostrarJardin();

        btnVolver.setOnClickListener(view -> finish());
    }

    private void mostrarJardin() {

        ArrayList<Tarea> tareas = gestorTareas.cargarTareas();

        contenedorJardin.removeAllViews();

        boolean hayFlorecidas = false;

        for (Tarea tarea : tareas) {

            if (!tarea.getEstado().equals("Florecida")) {
                continue;
            }

            hayFlorecidas = true;

            View item = getLayoutInflater().inflate(R.layout.item_tarea, contenedorJardin, false);

            TextView tvNombre = item.findViewById(R.id.tvItemNombreTarea);
            TextView tvAsignatura = item.findViewById(R.id.tvItemAsignatura);
            TextView tvEstado = item.findViewById(R.id.tvItemEstado);
            Button btnUsar = item.findViewById(R.id.btnUsarTarea);

            tvNombre.setText(tarea.getNombre());
            tvAsignatura.setText(tarea.getAsignatura());
            tvEstado.setText(tarea.getEstado());

            btnUsar.setVisibility(View.GONE);

            contenedorJardin.addView(item);
        }

        if (hayFlorecidas) {
            tvSinFlores.setVisibility(View.GONE);
        } else {
            tvSinFlores.setVisibility(View.VISIBLE);
        }
    }
}
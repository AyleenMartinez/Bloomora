package com.devst.bloomora;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PorHacerActivity extends AppCompatActivity {

    private EditText etAsignatura;
    private EditText etTarea;
    private Spinner spEstadoTarea;
    private Button btnGuardarTarea;
    private Button btnVolver;
    private SharedPreferences tareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_por_hacer);

        etAsignatura = findViewById(R.id.etAsignatura);
        etTarea = findViewById(R.id.etTarea);
        spEstadoTarea = findViewById(R.id.spEstadoTarea);
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea);
        btnVolver = findViewById(R.id.btnVolver);

        tareas = getSharedPreferences("tareasBloomora", MODE_PRIVATE);

        cargarTarea();

        btnGuardarTarea.setOnClickListener(view -> guardarTarea());
        btnVolver.setOnClickListener(view -> finish());
    }

    private void guardarTarea() {

        String asignatura = etAsignatura.getText().toString().trim();
        String tarea = etTarea.getText().toString().trim();
        String estado = spEstadoTarea.getSelectedItem().toString();

        if (asignatura.isEmpty()) {
            etAsignatura.setError(getString(R.string.error_asignatura_vacia));
            return;
        }

        if (tarea.isEmpty()) {
            etTarea.setError(getString(R.string.error_tarea_vacia));
            return;
        }

        SharedPreferences.Editor editor = tareas.edit();
        editor.putString("asignaturaTarea", asignatura);
        editor.putString("nombreTarea", tarea);
        editor.putString("estadoTarea", estado);
        editor.apply();

        Toast.makeText(this, R.string.tarea_guardada, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void cargarTarea() {

        String asignatura = tareas.getString("asignaturaTarea", "");
        String tarea = tareas.getString("nombreTarea", "");
        String estado = tareas.getString("estadoTarea", "Semilla");

        etAsignatura.setText(asignatura);
        etTarea.setText(tarea);

        if (estado.equals("Creciendo")) {
            spEstadoTarea.setSelection(1);
        } else if (estado.equals("Florecida")) {
            spEstadoTarea.setSelection(2);
        } else {
            spEstadoTarea.setSelection(0);
        }
    }
}
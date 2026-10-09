package com.devst.bloomora;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleTareaActivity extends AppCompatActivity {

    private TextView tvDetalleAsignatura;
    private TextView tvDetalleTarea;
    private TextView tvDetalleEstado;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_tarea);

        tvDetalleAsignatura = findViewById(R.id.tvDetalleAsignatura);
        tvDetalleTarea = findViewById(R.id.tvDetalleTarea);
        tvDetalleEstado = findViewById(R.id.tvDetalleEstado);
        btnVolver = findViewById(R.id.btnVolver);

        String asignatura = getIntent().getStringExtra("asignatura");
        String tarea = getIntent().getStringExtra("tarea");
        String estado = getIntent().getStringExtra("estado");

        tvDetalleAsignatura.setText(asignatura);
        tvDetalleTarea.setText(tarea);
        tvDetalleEstado.setText(estado);

        btnVolver.setOnClickListener(view -> finish());
    }
}
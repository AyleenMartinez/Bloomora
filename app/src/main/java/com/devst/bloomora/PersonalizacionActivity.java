package com.devst.bloomora;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PersonalizacionActivity extends AppCompatActivity {

    private EditText etNombreUsuario;
    private Spinner spDuracion;
    private Button btnGuardarPersonalizacion;
    private Button btnVolver;

    private SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personalizacion);

        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        spDuracion = findViewById(R.id.spDuracion);
        btnGuardarPersonalizacion = findViewById(R.id.btnGuardarPersonalizacion);
        btnVolver = findViewById(R.id.btnVolver);

        preferencias = getSharedPreferences("preferenciasBloomora", MODE_PRIVATE);

        cargarPreferencias();

        btnGuardarPersonalizacion.setOnClickListener(view -> guardarPreferencias());
        btnVolver.setOnClickListener(view -> finish());
    }

    private void guardarPreferencias() {

        String nombre = etNombreUsuario.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreUsuario.setError(getString(R.string.error_nombre_vacio));
            return;
        }

        int duracionSegundos;

        switch (spDuracion.getSelectedItemPosition()) {
            case 0:
                duracionSegundos = 15;
                break;
            case 1:
                duracionSegundos = 15 * 60;
                break;
            case 2:
                duracionSegundos = 25 * 60;
                break;
            case 3:
                duracionSegundos = 45 * 60;
                break;
            default:
                duracionSegundos = 60 * 60;
                break;
        }

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("nombreUsuario", nombre);
        editor.putInt("duracionSegundos", duracionSegundos);
        editor.apply();

        Toast.makeText(this, R.string.personalizacion_guardada, Toast.LENGTH_SHORT).show();

        finish();
    }

    private void cargarPreferencias() {

        String nombreGuardado = preferencias.getString("nombreUsuario", "");
        int duracionSegundos = preferencias.getInt("duracionSegundos", 25 * 60);

        etNombreUsuario.setText(nombreGuardado);

        if (duracionSegundos == 15) {
            spDuracion.setSelection(0);
        } else if (duracionSegundos == 15 * 60) {
            spDuracion.setSelection(1);
        } else if (duracionSegundos == 25 * 60) {
            spDuracion.setSelection(2);
        } else if (duracionSegundos == 45 * 60) {
            spDuracion.setSelection(3);
        } else {
            spDuracion.setSelection(4);
        }
    }
}
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

        preferencias = getSharedPreferences(
                "preferenciasBloomora",
                MODE_PRIVATE
        );

        cargarPreferencias();

        btnGuardarPersonalizacion.setOnClickListener(view -> {
            guardarPreferencias();
        });

        btnVolver.setOnClickListener(view -> {
            finish();
        });
    }

    private void guardarPreferencias() {

        String nombre = etNombreUsuario.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreUsuario.setError(
                    getString(R.string.error_nombre_vacio)
            );
            return;
        }

        int duracion;

        switch (spDuracion.getSelectedItemPosition()) {

            case 0:
                duracion = 15;
                break;

            case 1:
                duracion = 25;
                break;

            case 2:
                duracion = 45;
                break;

            default:
                duracion = 60;
                break;
        }

        SharedPreferences.Editor editor = preferencias.edit();

        editor.putString("nombreUsuario", nombre);
        editor.putInt("duracionEnfoque", duracion);
        editor.apply();

        Toast.makeText(
                this,
                R.string.personalizacion_guardada,
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    private void cargarPreferencias() {

        String nombreGuardado =
                preferencias.getString("nombreUsuario", "");

        int duracionGuardada =
                preferencias.getInt("duracionEnfoque", 25);

        etNombreUsuario.setText(nombreGuardado);

        if (duracionGuardada == 15) {
            spDuracion.setSelection(0);

        } else if (duracionGuardada == 25) {
            spDuracion.setSelection(1);

        } else if (duracionGuardada == 45) {
            spDuracion.setSelection(2);

        } else {
            spDuracion.setSelection(3);
        }
    }
}
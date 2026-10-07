package com.devst.bloomora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnEnfocarme;
    private Button btnPersonalizar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnEnfocarme = findViewById(R.id.btnEnfocarme);
        btnPersonalizar = findViewById(R.id.btnPersonalizar);


        btnEnfocarme.setOnClickListener( view -> {
            Intent irAEnfoque = new Intent(MainActivity.this, SesionEnfoqueActivity.class);
            startActivity(irAEnfoque);
        });

        btnPersonalizar.setOnClickListener(view -> {
            Intent irAPersonalizacion = new Intent(MainActivity.this, PersonalizacionActivity.class);
            startActivity(irAPersonalizacion);
        });



    }
}
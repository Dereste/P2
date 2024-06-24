package com.example.mapcalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {
    private Button btnEmissionsCalculator;
    private Button btnGpsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnGpsApp = findViewById(R.id.btn_gps_app);
        btnEmissionsCalculator = findViewById(R.id.btn_emissions_calculator);

        btnGpsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, GPSApp.class);
                startActivity(intent);
            }
        });

        btnEmissionsCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Calc.class);
                startActivity(intent);
            }
        });
    }
}

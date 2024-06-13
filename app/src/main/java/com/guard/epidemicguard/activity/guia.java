package com.guard.epidemicguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.guard.epidemicguard.R;

public class guia extends AppCompatActivity {

    private ImageView imageVoltarM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.guia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaGuia), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageVoltarM = findViewById(R.id.imageVoltarM);

        imageVoltarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(guia.this, menu.class);
                startActivity(i);
                finish();
            }
        });
    }

}

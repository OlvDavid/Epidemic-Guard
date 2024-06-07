package com.guard.epidemicguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import javax.annotation.Nonnull;

public class mapa extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private ImageView imageVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mapa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaMapa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(@Nonnull GoogleMap googleMap){

    }

    public void voltarMenu(View v){
        Intent i = new Intent(mapa.this, menu.class);
        startActivity(i);
        finish();
    }

    private void iniciarComponentes(){
        imageVoltar = findViewById(R.id.imageBack);

    }

}

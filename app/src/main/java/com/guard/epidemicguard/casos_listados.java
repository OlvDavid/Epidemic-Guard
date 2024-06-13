package com.guard.epidemicguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class casos_listados extends AppCompatActivity {

    private ImageView imageVoltarLista;
    private TextView textTotalCasos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.casos_listados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaListaCasos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iniciarComponentes();

        imageVoltarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(casos_listados.this, menu.class);
                startActivity(i);
                finish();
            }
        });

        totalCasos();

    }
    private void totalCasos(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Casos").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot snapshots = task.getResult();
                if(snapshots != null){
                    int total = snapshots.size();
                    textTotalCasos.setText("Total de casos: " + total);
                }else {
                    textTotalCasos.setText("Total de casos: 0");
                }
            }else{
                textTotalCasos.setText("Erro ao carregar dados");
            }
        });
    }

    private void iniciarComponentes(){
        imageVoltarLista = findViewById(R.id.imageVoltarLista);
        textTotalCasos = findViewById(R.id.textTotalCasos);
    }
}
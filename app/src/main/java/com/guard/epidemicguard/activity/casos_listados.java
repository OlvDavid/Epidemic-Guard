package com.guard.epidemicguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.guard.epidemicguard.R;
import com.guard.epidemicguard.adapter.Adapter;
import com.guard.epidemicguard.model.Casos;

import java.util.ArrayList;
import java.util.List;

public class casos_listados extends AppCompatActivity {

    private ImageView imageVoltarLista;
    private TextView textTotalCasos;
    private RecyclerView recyclerCasos;

    private List<Casos> listaCasos = new ArrayList<>();
    private Adapter adapter;

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
        configurarRecyclerView();
        adicionarCasos();
        totalCasos();

        imageVoltarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(casos_listados.this, menu.class);
                startActivity(i);
                finish();
            }
        });


    }

    private void configurarRecyclerView() {
        adapter = new Adapter(listaCasos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerCasos.setLayoutManager(layoutManager);
        recyclerCasos.setHasFixedSize(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_between_items);
        recyclerCasos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerCasos.setAdapter(adapter);
    }

    public void adicionarCasos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Casos").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore Error", error.getMessage());
                    return;
                }
                if(snapshots != null){
                    listaCasos.clear();
                    for(QueryDocumentSnapshot doc : snapshots){
                        Casos casos = doc.toObject(Casos.class);
                        Log.d("Firestore Data", "Nome: " + casos.getNome());
                        Log.d("Firestore Data", "Endereco: " + casos.getEndereco());
                        Log.d("Firestore Data", "Descricao: " + casos.getDescricao());
                        listaCasos.add(casos);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

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
        recyclerCasos = findViewById(R.id.recyclerCasos);
    }
}
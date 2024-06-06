package com.guard.epidemicguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Document;

public class perfil extends AppCompatActivity {

    private ImageView imageVoltar;

    private TextView textNome, textEmail, textCPF;
    private Button btnSair;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaPerfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicarComponentes();

        imageVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(perfil.this, menu.class);
                startActivity(i);
            }
        });

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(perfil.this, login.class );
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuários").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    textNome.setText(documentSnapshot.getString("nome"));
                    textCPF.setText(documentSnapshot.getString("CPF"));
                    textEmail.setText(email);
                }
            }
        });
    }

    private void inicarComponentes(){
        imageVoltar = findViewById(R.id.imageVoltar);
        textNome = findViewById(R.id.textNome);
        textEmail = findViewById(R.id.textEmail);
        textCPF = findViewById(R.id.textCpf);
        btnSair = findViewById(R.id.btnSair);
    }
}

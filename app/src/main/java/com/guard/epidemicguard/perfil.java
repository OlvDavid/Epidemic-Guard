package com.guard.epidemicguard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class perfil extends AppCompatActivity {

    private ImageView imageVoltar;

    private TextView textNome, textEmail, textCPF;
    private Button btnSair;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;
    ProgressBar progressBar;

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
                finish();
            }
        });

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarSaida();
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
                if (documentSnapshot != null) {
                    textNome.setText(documentSnapshot.getString("nome"));
                    textCPF.setText(documentSnapshot.getString("CPF"));
                    textEmail.setText(email);
                }
            }
        });
    }

    private void sair() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(perfil.this, login.class);
        startActivity(i);
        finish();


    }

    private void inicarComponentes() {
        imageVoltar = findViewById(R.id.imageBack);
        textNome = findViewById(R.id.textNome);
        textEmail = findViewById(R.id.textEmail);
        textCPF = findViewById(R.id.textCpf);
        btnSair = findViewById(R.id.btnSair);
        progressBar = findViewById(R.id.progressBar);
    }

    private void confirmarSaida() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this)
                    .setMessage("Deseja deslogar do aplicativo?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE); // Exibe a barra de progresso
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sair();
                                }
                            }, 3000);
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
        }
    }
}

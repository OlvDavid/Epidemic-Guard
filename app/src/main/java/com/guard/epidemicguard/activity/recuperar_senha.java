package com.guard.epidemicguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.guard.epidemicguard.R;

public class recuperar_senha extends AppCompatActivity {

    private Button btnVoltar, btnRecuperar;
    private TextInputEditText editEmail;
    private ProgressBar progressBar;
    private String email;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.recuperar_senha);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaRecuperar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iniciarComponentes();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(recuperar_senha.this, login.class);
                startActivity(i);
            }
        });

        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(email)){
                    ResetPassword();
                }else{
                    editEmail.setError("Campo Obrigatório");
                }

            }
        });
    }

    private void ResetPassword(){
        progressBar.setVisibility(View.VISIBLE);
        btnRecuperar.setVisibility(View.INVISIBLE);

        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(recuperar_senha.this, "Link de recuperaçao da senha foi enviado para o email registrado", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(recuperar_senha.this, login.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(recuperar_senha.this, "Email não cadastrado", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                btnRecuperar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void iniciarComponentes(){
        btnVoltar = findViewById(R.id.btnVoltarR);
        btnRecuperar = findViewById(R.id.btnRedefinirSenha);
        editEmail = findViewById(R.id.editRecuperarEmail);
        progressBar = findViewById(R.id.progressBar3);
    }
}
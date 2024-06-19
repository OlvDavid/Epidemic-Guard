package com.guard.epidemicguard.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guard.epidemicguard.R;

import io.grpc.okhttp.internal.Util;

public class login extends AppCompatActivity {

    private Button btnCadastrar, btnEntrar;
    private TextInputEditText editEmail, editSenha;
    private TextView textEsqueceuSenha;
    private ProgressBar progressBar;
    private ImageView imageMostrarSenha;

    String[] mensagens = {"Preencha todos os campos",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarComponetes();

        imageMostrarSenha.setImageResource(R.drawable.hide);
        imageMostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editSenha.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageMostrarSenha.setImageResource(R.drawable.hide);
                }else{
                    editSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageMostrarSenha.setImageResource(R.drawable.view);
                }
            }
        });


        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    autenticarUsuario(v);
                }
            }
        });

        textEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login.this, recuperar_senha.class);
                startActivity(i);
            }
        });
    }

    private void autenticarUsuario(View v){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    btnEntrar.setVisibility(View.INVISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            telaPrincipal();
                        }
                    }, 3000);
                }else{
                    String erro;
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        erro = "Erro ao logar usuário";
                        progressBar.setVisibility(View.INVISIBLE);
                        btnEntrar.setVisibility(View.VISIBLE);
                    }
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if(usuarioAtual != null){
            telaPrincipal();
        }
    }

    private void telaPrincipal(){
        Intent i = new Intent(this, menu.class);
        startActivity(i);
        finish();
    }

    public void cadastrar(View v){
        Intent i = new Intent(this, cadastro.class);
        startActivity(i);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this)
                    .setMessage("Deseja realmente sair do aplicativo?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
        }
    }

    private void iniciarComponetes(){
        btnCadastrar = findViewById(R.id.btnCadastrar);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        progressBar = findViewById(R.id.progressBar);
        imageMostrarSenha = findViewById(R.id.imageMostrarSenha);
        textEsqueceuSenha = findViewById(R.id.textEsqueciSenha);
    }
}


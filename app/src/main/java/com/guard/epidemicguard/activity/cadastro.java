package com.guard.epidemicguard.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guard.epidemicguard.R;

import java.util.HashMap;
import java.util.Map;

public class cadastro extends AppCompatActivity {

    private TextInputEditText editEmail, editSenha, editNome, editCPF, editConfirmeSenha;
    private Button btnCadastrar, btnVoltar;
    private ImageView imageMostrarSenha, imageMostrarConfirmeSenha;
    FirebaseAuth usuario = FirebaseAuth.getInstance();

    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};
    String usuarioID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cadastro);
        FirebaseApp.initializeApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaCadastro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarComponentes();

        imageMostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editSenha.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageMostrarSenha.setImageResource(R.drawable.hide);
                } else {
                    editSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageMostrarSenha.setImageResource(R.drawable.view);
                }
            }
        });

        imageMostrarConfirmeSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editConfirmeSenha.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editConfirmeSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageMostrarConfirmeSenha.setImageResource(R.drawable.hide);
                } else {
                    editConfirmeSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageMostrarConfirmeSenha.setImageResource(R.drawable.view);
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(cadastro.this, login.class);
                startActivity(i);
                finish();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editNome.getText().toString();
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                String confirmeSenha = editConfirmeSenha.getText().toString();
                String cpf = editCPF.getText().toString();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || cpf.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                } else if (!senha.equals(confirmeSenha)) {
                    Snackbar snackbar = Snackbar.make(v, "As senhas não coincidem", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    cadastrarUsuario(v);
                }
            }
        });
    }

    private void cadastrarUsuario(View v) {

        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        String cpf = editCPF.getText().toString();

        if (!validarCPF(cpf)) {
            Snackbar snackbar = Snackbar.make(v, "CPF inválido", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
            return;
        }

        usuario.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    salvarDadosUsuario();

                    FirebaseAuth.getInstance().signOut();

                    Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            Intent intent = new Intent(cadastro.this, login.class);
                            startActivity(intent);
                            finish();

                        }

                    });
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no mínimo 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esta conta já esta cadastrada";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Digite um e-mail válido";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";
                    }
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }
            }
        });
    }

    private void salvarDadosUsuario() {
        String nome = editNome.getText().toString();
        String cpf = editCPF.getText().toString();

        cpf = formatarCPF(cpf);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("CPF", cpf);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuários").document(usuarioID);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Sucesso ao salvar os dados");
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error", "Erro ao salvar os dados" + e.toString());
                    }
                });

    }


    private void iniciarComponentes() {
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        editCPF = findViewById(R.id.editCpf);
        editConfirmeSenha = findViewById(R.id.editConfirmeSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnVoltar = findViewById(R.id.btnVoltar);
        imageMostrarSenha = findViewById(R.id.imageMostrarSenhaC);
        imageMostrarConfirmeSenha = findViewById(R.id.imageMostrarConfirmeSenha);
    }


    private boolean validarCPF(String cpf) {
        // Remover caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Verificar se o CPF possui 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verificar se todos os dígitos são iguais (caso especial de CPF inválido)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcular dígitos verificadores
        int[] multiplicadoresPrimeiroDigito = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] multiplicadoresSegundoDigito = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;
        int resto;

        // Verificação do primeiro dígito verificador
        for (int i = 0; i < 9; i++) {
            soma += Integer.parseInt(String.valueOf(cpf.charAt(i))) * multiplicadoresPrimeiroDigito[i];
        }

        resto = soma % 11;
        int digitoVerificador1 = resto < 2 ? 0 : 11 - resto;
        if (digitoVerificador1 != Integer.parseInt(String.valueOf(cpf.charAt(9)))) {
            return false;
        }

        // Verificação do segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Integer.parseInt(String.valueOf(cpf.charAt(i))) * multiplicadoresSegundoDigito[i];
        }

        resto = soma % 11;
        int digitoVerificador2 = resto < 2 ? 0 : 11 - resto;
        if (digitoVerificador2 != Integer.parseInt(String.valueOf(cpf.charAt(10)))) {
            return false;
        }

        return true;
    }

    private String formatarCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Formata o CPF com os caracteres especiais
        return cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}


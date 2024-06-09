package com.guard.epidemicguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class novo_caso extends AppCompatActivity {

    private TextInputEditText editNomeCompleto, editEndereco;
    private Button btnNovoCaso;
    private ImageView imageVoltarCaso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.novo_caso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaNovoCaso), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarComponetes();

        btnNovoCaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeCompleto = editNomeCompleto.getText().toString();
                String endereco = editEndereco.getText().toString();
                if(nomeCompleto.isEmpty() || endereco.isEmpty()) {
                    Toast.makeText(novo_caso.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(novo_caso.this, "Caso cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    salvarDadosCaso();
                }
            }
        });

        imageVoltarCaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(novo_caso.this, menu.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void salvarDadosCaso(){
        String nomeCompleto = editNomeCompleto.getText().toString();
        String endereco = editEndereco.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> dadosCaso = new HashMap<>();
        dadosCaso.put("Nome Completo", nomeCompleto);
        dadosCaso.put("Endereço", endereco);
        db.collection("Casos").add(dadosCaso);
        finish();

    }

    private void iniciarComponetes(){
        editNomeCompleto = findViewById(R.id.editNomeCompleto);
        editEndereco = findViewById(R.id.editEndereco);
        btnNovoCaso = findViewById(R.id.btnNovoCaso);
        imageVoltarCaso = findViewById(R.id.imageVoltarCaso);

    }
}
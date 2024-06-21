package com.guard.epidemicguard.activity;

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
import com.guard.epidemicguard.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class novo_caso extends AppCompatActivity {

    private TextInputEditText editNomeCompleto, editRua, editNumero, editBairro, editCidade, editEstado, editDescricao;
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
                String bairro = editBairro.getText().toString();
                String rua = editRua.getText().toString();
                String numero = editNumero.getText().toString();
                String cidade = editCidade.getText().toString();
                String estado = editEstado.getText().toString();
                String descricao = editDescricao.getText().toString();
                if(nomeCompleto.isEmpty() || bairro.isEmpty() || rua.isEmpty() || numero.isEmpty() || cidade.isEmpty() || estado.isEmpty() || descricao.isEmpty()) {
                    Toast.makeText(novo_caso.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    // Valida o endereço
                    validarEndereco(rua, numero, bairro, cidade, estado, new EnderecoValidationCallback() {
                        @Override
                        public void onValidationSuccess() {
                            Toast.makeText(novo_caso.this, "Caso cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                            btnNovoCaso.setVisibility(View.INVISIBLE);
                            salvarDadosCaso();
                        }

                        @Override
                        public void onValidationFailure() {
                            Toast.makeText(novo_caso.this, "Endereço inválido", Toast.LENGTH_SHORT).show();
                        }
                    });
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
        String bairro = editBairro.getText().toString();
        String rua = editRua.getText().toString();
        String numero = editNumero.getText().toString();
        String cidade = editCidade.getText().toString();
        String estado = editEstado.getText().toString();
        String descricao = editDescricao.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> dadosCaso = new HashMap<>();
        dadosCaso.put("nome", nomeCompleto);
        dadosCaso.put("endereco", rua + " ," + numero + " - " + bairro + ", " + cidade + " - " + estado);
        dadosCaso.put("descricao", descricao);
        db.collection("Casos").add(dadosCaso).addOnSuccessListener(documentReference ->{
            finish();
        });
    }

    private void iniciarComponetes(){
        editNomeCompleto = findViewById(R.id.editNomeCompleto);
        editBairro = findViewById(R.id.editBairro);
        editCidade = findViewById(R.id.editCidade);
        editEstado = findViewById(R.id.editEstado);
        editRua = findViewById(R.id.editRua);
        editNumero = findViewById(R.id.editNumero);
        btnNovoCaso = findViewById(R.id.btnNovoCaso);
        imageVoltarCaso = findViewById(R.id.imageVoltarCaso);
        editDescricao  = findViewById(R.id.editDescricao);
    }

    private void validarEndereco(String rua, String numero, String bairro, String cidade, String estado, EnderecoValidationCallback callback) {
        new Thread(() -> {
            try {
                String endereco = rua + " " + numero + " " + bairro + " " + cidade + " " + estado;
                String apiKey = "AIzaSyCNafzrtvA8mZzWMTJHx4MGFxfm1DL7mfA";
                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + endereco + "&key=" + apiKey;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                conn.disconnect();

                JSONObject json = new JSONObject(content.toString());
                JSONArray results = json.getJSONArray("results");
                if (results.length() > 0) {
                    runOnUiThread(callback::onValidationSuccess);
                } else {
                    runOnUiThread(callback::onValidationFailure);
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(callback::onValidationFailure);
            }
        }).start();
    }

    interface EnderecoValidationCallback {
        void onValidationSuccess();
        void onValidationFailure();
    }
}

package com.guard.epidemicguard.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.guard.epidemicguard.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

public class mapa extends AppCompatActivity implements OnMapReadyCallback {

    // Declarações de variáveis
    private GoogleMap map;
    private SearchView mapSearchView;
    private ListView suggestionsListView;
    private ArrayAdapter<String> suggestionsAdapter;
    private static final int FINE_PERMISSION_REQUEST_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    PlacesClient placesClient;
    private boolean permissionDenied = false;
    FirebaseFirestore db;
    SupportMapFragment mapFragment;
    private ExecutorService executorService;

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

        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor(); // Inicialização do ExecutorService


        // Inicialização do Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCNafzrtvA8mZzWMTJHx4MGFxfm1DL7mfA");
        }
        placesClient = Places.createClient(this);

        // Inicialização do Location Provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        // Inicialização dos componentes da interface
        iniciarComponentes();

        // Configuração da barra de pesquisa
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarEnderecos(query);

                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null){

                    Geocoder geocoder = new Geocoder(mapa.this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    } else {
                        Toast.makeText(mapa.this, "Endereço não encontrado: " + location, Toast.LENGTH_SHORT).show();
                    }
                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                buscarEnderecos(newText);
                return false;
            }
        });

    }

    // Método para buscar sugestões de endereços
    private void buscarEnderecos(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setSessionToken(token)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            List<String> sugestoes = new ArrayList<>();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                sugestoes.add(prediction.getFullText(null).toString());
            }

            if (sugestoes.isEmpty()) {
                suggestionsListView.setVisibility(View.GONE);
            } else {
                suggestionsAdapter.clear();
                suggestionsAdapter.addAll(sugestoes);
                suggestionsListView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(mapa.this, "Erro ao buscar sugestões: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Método para obter a última localização conhecida do dispositivo
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(mapa.this);
                    }
                } else {
                    Toast.makeText(mapa.this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Método chamado quando o mapa está pronto para ser usado
    @Override
    public void onMapReady(@Nonnull GoogleMap googleMap) {
        map = googleMap;
        if (currentLocation != null) {
            LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(myLocation).title("Minha Localização"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        } else {
            Toast.makeText(this, "Localização atual não disponível", Toast.LENGTH_SHORT).show();
        }
        carregarDados();
    }


    private void carregarDados(){
        db.collection("Casos").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document : task.getResult()){
                    String endereco = document.getString("endereco");
                    String nome = document.getString("nome");

                    executorService.execute(() -> {
                        LatLng latLng = geocodeAddress(endereco);
                        runOnUiThread(() -> {
                            if (latLng != null) {
                                map.addMarker(new MarkerOptions().position(latLng).title(endereco).icon(BitmapDescriptorFactory.fromResource(R.drawable.mosquito)));
                            }
                        });
                    });
                }
            }
        });
    }

    // Método chamado quando o resultado da solicitação de permissão é recebido
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Permissão de localização Negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para inicializar os componentes da interface
    private void iniciarComponentes() {
        mapSearchView = findViewById(R.id.mapSearch);
        suggestionsListView = findViewById(R.id.listView);
        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        suggestionsListView.setAdapter(suggestionsAdapter);

        suggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSuggestion = (String) parent.getItemAtPosition(position);
                mapSearchView.setQuery(selectedSuggestion, true);

            }
        });

    }

    private LatLng geocodeAddress(String address) {
        try {
            String apiKey = "AIzaSyCNafzrtvA8mZzWMTJHx4MGFxfm1DL7mfA";
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    URLEncoder.encode(address, "UTF-8") + "&key=" + apiKey;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                return new LatLng(lat, lng);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}

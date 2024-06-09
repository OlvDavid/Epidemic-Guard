package com.guard.epidemicguard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class mapa extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private SearchView mapSearchView;
    private ImageView imageVoltar;
    private ListView suggestionsListView;
    private ArrayAdapter<String> suggestionsAdapter;

    private static final int FINE_PERMISSION_REQUEST_CODE = 1;
    Location currentLocation;

    FusedLocationProviderClient fusedLocationProviderClient;
    PlacesClient placesClient;
    private boolean permissionDenied = false;

    SupportMapFragment mapFragment;

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
        
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCNafzrtvA8mZzWMTJHx4MGFxfm1DL7mfA");
        }
        placesClient = Places.createClient(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        iniciarComponentes();

        imageVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mapa.this, menu.class);
                startActivity(i);
                finish();
            }
        });

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

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                buscarEnderecos(newText);
                return false;
            }
        });
    }

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
                suggestionsAdapter.clear(); //
                suggestionsAdapter.addAll(sugestoes);
                suggestionsListView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(mapa.this, "Erro ao buscar sugestões: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

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
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
                    mapFragment.getMapAsync(mapa.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@Nonnull GoogleMap googleMap) {
        map = googleMap;
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(myLocation).title("Minha Localização"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
    }

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

    private void iniciarComponentes() {
        imageVoltar = findViewById(R.id.imageBack);
        mapSearchView = findViewById(R.id.mapSearch);
        suggestionsListView = findViewById(R.id.listView);
        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        suggestionsListView.setAdapter(suggestionsAdapter);
    }
}

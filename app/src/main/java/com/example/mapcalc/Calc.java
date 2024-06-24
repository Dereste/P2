package com.example.mapcalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calc extends AppCompatActivity {
    private AutoCompleteTextView sourceAutocompleteTextView;
    private AutoCompleteTextView destinationAutocompleteTextView;
    private SupportMapFragment mapFragment;
    private EmissionsCalculator calculator;
    private Spinner vehicleTypeSpinner;
    private EditText distanceKmEditText;
    private Button calculateButton;
    private TextView resultTextView;
    private EditText editTextS;
    private EditText editTextD;
    private Button button;
    private GoogleMap mMap;
    private RequestQueue requestQueue;
    private final String TAG = "Calc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_main);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
        }

        AutocompleteSupportFragment autocompleteFragmentSource = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_source);
        autocompleteFragmentSource.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Get info about the selected place.
                editTextS.setText(place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error.
                Toast.makeText(Calc.this, "An error occurred: " + status, Toast.LENGTH_SHORT).show();
            }
        });

        AutocompleteSupportFragment autocompleteFragmentDestination = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_destination);
        autocompleteFragmentDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Get info about the selected place.
                editTextD.setText(place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error.
                Toast.makeText(Calc.this, "An error occurred: " + status, Toast.LENGTH_SHORT).show();
            }
        });

        String[] vehicleTypes = getResources().getStringArray(R.array.vehicle_types);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //GPS
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    // Do something with the GoogleMap object
                    mMap = googleMap;

                    /*button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String source = editTextS.getText().toString();
                            String destination = editTextD.getText().toString();
                            if (source.isEmpty() || destination.isEmpty()) {
                                Toast.makeText(Calc.this, "Please enter both source and destination", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Uri uri = Uri.parse("https://www.google.com/maps/dir/" + source + "/" + destination);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setPackage("com.google.android.apps.maps");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });*/

                    calculator = new EmissionsCalculator();
                    editTextS = findViewById(R.id.source);
                    editTextD = findViewById(R.id.destination);
                    button = findViewById(R.id.button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String source = editTextS.getText().toString();
                            String destination = editTextD.getText().toString();
                            if (source.isEmpty() || destination.isEmpty()) {
                                Toast.makeText(Calc.this, "Please enter both source and destination", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String apiKey = "@string/google_maps_api_key";
                            String directionsUrl = "https://maps.googleapis.com/maps/api/directions/json" +
                                    "?origin=" + source +
                                    "&destination=" + destination +
                                    "&key=" + apiKey;

                            RequestQueue queue = Volley.newRequestQueue(Calc.this);
                            JsonObjectRequest directionsRequest = new JsonObjectRequest
                                    (Request.Method.GET, directionsUrl, null, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONArray routesArray = response.getJSONArray("routes");
                                                if (routesArray.length() > 0) {
                                                    JSONObject route = routesArray.getJSONObject(0);
                                                    JSONObject polyline = route.getJSONObject("overview_polyline");
                                                    String points = polyline.getString("points");

                                                    // Draw the route on the map
                                                    List<LatLng> decodedPath = decodePolyline(points);
                                                    mMap.clear();
                                                    mMap.addPolyline(new PolylineOptions().addAll(decodedPath));

                                                    // Display distance and duration in your UI
                                                    JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                                                    String distance = leg.getJSONObject("distance").getString("text");
                                                    String duration = leg.getJSONObject("duration").getString("text");
                                                    Toast.makeText(Calc.this, "Distance: " + distance + ", Duration: " + duration, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Calc.this, "No routes found", Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(Calc.this, "Error parsing directions JSON", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(Calc.this, "Error fetching directions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            queue.add(directionsRequest);
                        }
                    });


                    //Calculator
                    distanceKmEditText = findViewById(R.id.distance_km_edit_text);
                    vehicleTypeSpinner = findViewById(R.id.vehicle_type_spinner);
                    calculateButton = findViewById(R.id.calculate_button);
                    resultTextView = findViewById(R.id.result_text_view);

                    vehicleTypeSpinner.setAdapter(adapter);

                    calculateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String vehicleType = vehicleTypeSpinner.getSelectedItem().toString();
                            if (!distanceKmEditText.getText().toString().isEmpty()) {
                                double distanceKm = Double.parseDouble(distanceKmEditText.getText().toString());
                                double emissions = calculator.calculateEmissions(vehicleType, distanceKm);
                                resultTextView.setText("Total emissions for " + distanceKm + " km: " + emissions + " grams of CO2");
                            } else {
                                Toast.makeText(Calc.this, "Please enter distance", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                private List<LatLng> decodePolyline(String encoded) {
                    List<LatLng> poly = new ArrayList<>();
                    int index = 0, len = encoded.length();
                    int lat = 0, lng = 0;

                    while (index < len) {
                        int b, shift = 0, result = 0;
                        do {
                            b = encoded.charAt(index++) - 63;
                            result |= (b & 0x1f) << shift;
                            shift += 5;
                        } while (b >= 0x20);
                        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                        lat += dlat;

                        shift = 0;
                        result = 0;
                        do {
                            b = encoded.charAt(index++) - 63;
                            result |= (b & 0x1f) << shift;
                            shift += 5;
                        } while (b >= 0x20);
                        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                        lng += dlng;

                        LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
                        poly.add(p);
                    }

                    return poly;
                }
            });
    }
}
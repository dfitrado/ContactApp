package com.example.uts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class AddContact extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Context context;
    DBHandler dbHandler;

    Button btnAdd, btnCari, btnGo;
    EditText edtTxtName, edtTxtNumber, edtTxtEmail, edtTxtLat, edtTxtLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;
        dbHandler = new DBHandler(context);

        edtTxtName = (EditText)findViewById(R.id.edtTxtName);
        edtTxtNumber = (EditText)findViewById(R.id.edtTxtNumber);
        edtTxtEmail = (EditText)findViewById(R.id.edtTxtEmail);
        edtTxtLat = (EditText)findViewById(R.id.edtTxtLat);
        edtTxtLng = (EditText)findViewById(R.id.edtTxtLng);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnCari = (Button)findViewById(R.id.btnCari);
        btnGo = (Button)findViewById(R.id.btnGo);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtTxtName.getText().toString();
                String number = edtTxtNumber.getText().toString();
                String email = edtTxtEmail.getText().toString();
                String lat = edtTxtLat.getText().toString();
                String lng = edtTxtLng.getText().toString();

                if(name.trim().length() > 0 && number.trim().length() > 0 && email.trim().length() > 0){
                    Contact contact = new Contact(name, number, email, lat, lng);
                    dbHandler.addContact(contact);
                    startActivity(new Intent(context, MapsActivity.class));
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Please fill all the fields")
                    .setNegativeButton("OK", null)
                    .show();
                }
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLokasi();
            }
        });

        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sembunyikanKeyboard(view);
                goCari();
            }
        });

    }

    private void gotoLokasi(){
        EditText edtTxtLat = (EditText)findViewById(R.id.edtTxtLat);
        EditText edtTxtLng = (EditText)findViewById(R.id.edtTxtLng);
        int zoom = 10;

        Double dbllat = Double.parseDouble(edtTxtLat.getText().toString());
        Double dbllng = Double.parseDouble(edtTxtLng.getText().toString());

        Toast.makeText(this, "Mote to Lat: " + dbllat + " Long: " + dbllng, Toast.LENGTH_LONG).show();
        gotoPeta(dbllat, dbllng, zoom);
    }

    private void goCari() {
        EditText edtTxtDaerah = (EditText) findViewById(R.id.edtTxtDaerah);
        Geocoder g = new Geocoder(getBaseContext());
        try {
            List<Address> daftar = g.getFromLocationName(edtTxtDaerah.getText().toString(),1);
            Address alamat = daftar.get(0);

            String nemuAlamat = alamat.getAddressLine(0);
            Double lintang = alamat.getLatitude();
            Double bujur = alamat.getLongitude();

            Toast.makeText(getBaseContext(),"Ketemu " + nemuAlamat, Toast.LENGTH_LONG).show();
            String zoom = "10";
            Float dblzoom = Float.parseFloat(zoom.toString());
            Toast.makeText(this, "Move to " + nemuAlamat + " Lat:" + lintang + " Long;" + bujur, Toast.LENGTH_LONG).show();
            gotoPeta(lintang, bujur, dblzoom);

            EditText edtTxtLat = (EditText) findViewById(R.id.edtTxtLat);
            EditText edtTxtLng = (EditText) findViewById(R.id.edtTxtLng);

            Double dbllat = Double.parseDouble(edtTxtLat.getText().toString());
            Double dbllng = Double.parseDouble(edtTxtLng.getText().toString());

            edtTxtLat.setText(lintang.toString());
            edtTxtLng.setText(bujur.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sembunyikanKeyboard(View v){
        InputMethodManager a = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        a.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void gotoPeta(double lat, double lng, float z){
        LatLng Lokasibaru = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(Lokasibaru).title("Marker in " + lat + ":" + lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lokasibaru, z));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ITS = new LatLng(-7.2819705, 112.795323);
        mMap.addMarker(new MarkerOptions().position(ITS).title("Marker in ITS"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ITS,15));

        edtTxtLat.setText(String.valueOf(ITS.latitude));
        edtTxtLng.setText(String.valueOf(ITS.longitude));
    }
}
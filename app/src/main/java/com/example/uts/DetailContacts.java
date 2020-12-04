package com.example.uts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class DetailContacts extends FragmentActivity implements OnMapReadyCallback {

    private LocationManager lm;
    private LocationListener ll;

    RelativeLayout parent;

    private GoogleMap mMap;
    Double dbllat, dbllng, latAsal, lngAsal;

    Context context;
    DBHandler dbHandler;

    String ID;

    Button btnUpdate, btnCari, btnGo, btnDelete, btnJarak;
    EditText edtTxtName, edtTxtNumber, edtTxtEmail, edtTxtLat, edtTxtLng;
    TextView txtProfileName;

    String txtLat;
    String txtLong;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contacts);

        lm = (LocationManager) getSystemService
                (Context.LOCATION_SERVICE);
        ll = new lokasiListener();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 1000, 1,
                        ll);

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
        txtProfileName = (TextView)findViewById(R.id.txtProfileName);

        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnCari = (Button)findViewById(R.id.btnCari);
        btnGo = (Button)findViewById(R.id.btnGo);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnJarak = (Button)findViewById(R.id.btnJarak);

        ID = getIntent().getStringExtra(MapsActivity.ID_CONTACT);

//        Contact contact = dbHandler.getContact(Integer.parseInt(ID));

        final Contact contact = (Contact)getIntent().getSerializableExtra("contact");

        txtProfileName.setText(contact.getName() + "'s Profile");

        edtTxtName.setText(contact.getName());
        edtTxtNumber.setText(contact.getNumber());
        edtTxtEmail.setText(contact.getEmail());
        edtTxtLat.setText(contact.getLat());
        edtTxtLng.setText(contact.getLng());

        dbllat = Double.parseDouble(edtTxtLat.getText().toString());
        dbllng = Double.parseDouble(edtTxtLng.getText().toString());

        parent = (RelativeLayout)findViewById(R.id.parent);

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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtTxtName.getText().toString();
                String number = edtTxtNumber.getText().toString();
                String email = edtTxtEmail.getText().toString();
                String lat = edtTxtLat.getText().toString();
                String lng = edtTxtLng.getText().toString();

                if(name.trim().length() > 0 && number.trim().length() > 0 && email.trim().length() > 0){
                    Contact contact_update = new Contact(contact.getId(), name, number, email, lat, lng);
                    dbHandler.updateContact(contact_update);
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

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtTxtName.getText().toString();
                String number = edtTxtNumber.getText().toString();
                String email = edtTxtEmail.getText().toString();
                String lat = edtTxtLat.getText().toString();
                String lng = edtTxtLng.getText().toString();

                Contact contact_delete = new Contact(contact.getId(), name, number, email, lat, lng);
                dbHandler.deleteContact(contact_delete);
                startActivity(new Intent(context, MapsActivity.class));
            }
        });

        btnJarak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitungJarak(dbllat, dbllng, latAsal, lngAsal);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ITS = new LatLng(dbllat, dbllng);
        mMap.addMarker(new MarkerOptions().position(ITS).title("Marker in ITS"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ITS,15));
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

            dbllat = Double.parseDouble(edtTxtLat.getText().toString());
            dbllng = Double.parseDouble(edtTxtLng.getText().toString());

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

    private class lokasiListener implements LocationListener{

        @Override
        public void onLocationChanged(@NonNull Location location) {
            txtLat = String.valueOf(location.getLatitude());
            txtLong = String.valueOf(location.getLongitude());

            latAsal = Double.parseDouble(txtLat);
            lngAsal = Double.parseDouble(txtLong);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    private void hitungJarak(double latAsal, double lngAsal, double latTujuan, double lngTujuan){
        Location asal = new Location("asal");
        Location tujuan = new Location("tujuan");
        tujuan.setLatitude(latTujuan);
        tujuan.setLongitude(lngTujuan);
        asal.setLatitude(latAsal);
        asal.setLongitude(lngAsal);
        float jarak = (float) asal.distanceTo(tujuan)/1000;
        String jaraknya = String.valueOf(jarak);
        Snackbar.make(parent, "Jarak dari Posisi device anda ke Alamat contact ini adalah " + jaraknya + " km", Snackbar.LENGTH_LONG).show();
    }

}
package pomza.export.pomexyerler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.jar.Pack200;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    public LocationManager locationManager;
    public LocationListener locationListener;
    private SQLiteDatabase database;
    private String namee="aaa";
    private ArrayList<String> names=new ArrayList<>();
    private ArrayList<LatLng> locations;
    private LatLng ll;
    private Date date;
    private Calendar calendar;
    private final String filename="Pomex Yerler";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.clear();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }else{
            mMap.setMyLocationEnabled(true);
        }

        Intent intent=getIntent();
        String info;
        info=intent.getStringExtra("info");

        if (info!=null){
            if (info.matches("list1")){
                mMap.clear();
                int position=intent.getIntExtra("position",0);
                LatLng locat=new LatLng(Search.locations.get(position).latitude,Search.locations.get(position).longitude);
                String name=Search.names.get(position);
                mMap.addMarker(new MarkerOptions().title(name).position(locat));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locat,15));
            }else if (info.matches("list2")){
                mMap.clear();
                int position=intent.getIntExtra("position",0);
                LatLng locat=new LatLng(RcAdapter.loclist.get(position).latitude,RcAdapter.loclist.get(position).longitude);
                String name=RcAdapter.noktalist.get(position);
                mMap.addMarker(new MarkerOptions().title(name).position(locat));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locat,15));
            }

        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.82039,29.34866),6));
            locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener=new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    SharedPreferences sharedPreferences=MapsActivity.this.getSharedPreferences("pomza.export.pomexyerler",MODE_PRIVATE);
                    boolean fcheck =sharedPreferences.getBoolean("notfirst",false);
                    if (!fcheck){
                        LatLng guncel=new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncel,15));
                        sharedPreferences.edit().putBoolean("notfirst",true).apply();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,1,locationListener);
                Location lastlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastlocation!=null){
                    LatLng lastuserlocation=new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastuserlocation,15),5000,null);
                }
            }
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));
                Log.e("long","tiklandi");
                try {
                    final Double l1=latLng.latitude;
                    final Double l2=latLng.longitude;
                        AlertDialog.Builder builder=new AlertDialog.Builder(MapsActivity.this);
                        builder.setTitle("Nokta İsmi");
                        final EditText et=new EditText(MapsActivity.this);
                        builder.setView(et);
                        builder.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                namee=et.getText().toString();
                                String coord1=l1.toString();
                                String coord2=l2.toString();
                                database=getApplicationContext().openOrCreateDatabase("PomexNoktalar",MODE_PRIVATE,null);
                                database.execSQL("CREATE TABLE IF NOT EXISTS noktalar (nokta_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR," +
                                        " latitude VARCHAR, longitude VARCHAR, aciklama VARCHAR, tarih DATETIME DEFAULT CURRENT_TIMESTAMP, malzeme BLOB, analiz BLOB, malzeme_path VARCHAR)");
                                String toComplie="INSERT INTO noktalar (name,latitude,longitude,tarih) VALUES (?,?,?,datetime('now','localtime'))";
                                SQLiteStatement sqLiteStatement=database.compileStatement(toComplie);
                                sqLiteStatement.bindString(1,namee);
                                sqLiteStatement.bindString(2,coord1);
                                sqLiteStatement.bindString(3,coord2);
                                sqLiteStatement.execute();
                            }
                        });
                        builder.show();


                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0){
            if (requestCode==1){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,locationListener);
                    Location lastlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mMap.setMyLocationEnabled(true);
                    if (lastlocation!=null){
                        LatLng lastuserlocation=new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserlocation,15));
                    }
                }
            }else if (requestCode==3){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    try {
                        File imageroot=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),filename);
                        if (!imageroot.exists()){
                            imageroot.mkdirs();
                            Toast.makeText(this,"Galeride PomexYerler Klasörü Oluşturuldu",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

package pomza.export.pomexyerler;

import android.app.ActionBar;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.BoolRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class NavigationDrawerFragment extends Fragment {

    private DrawerLayout mDrawerLayout;
    private TextView listele, goster, bul;
    private Boolean durum;
    private SQLiteDatabase database;
    private ArrayList<String> names=new ArrayList<>();
    private ArrayList<LatLng> locations=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_navigation_drawer_layout,container,false);
        listele=v.findViewById(R.id.textView1);
        goster=v.findViewById(R.id.textView222);
        bul=v.findViewById(R.id.textView3);
        durum=false;
        listele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ListActivity.class);
                startActivity(intent);
            }
        });
        goster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(durum==false){
                    MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.82039,29.34866),6));
                    yerisaretleri();
                    yerinekoy();
                    durum=true;
                }else if(durum==true){
                    MapsActivity.mMap.clear();
                    durum=false;
                }
            }
        });
        bul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), Search.class);
                startActivity(intent);
            }
        });
        return v;
    }

    public void yerisaretleri(){
        try {
            database=getActivity().openOrCreateDatabase("PomexNoktalar",Context.MODE_PRIVATE,null);
            Cursor c=database.rawQuery("SELECT * FROM noktalar",null);

            locations=new ArrayList<>();

            int nameIx=c.getColumnIndex("name");
            int latitudeIx=c.getColumnIndex("latitude");
            int longitudeIx=c.getColumnIndex("longitude");

            while(c.moveToNext()){
                String namedao=c.getString(nameIx);
                String latdao=c.getString(latitudeIx);
                String longdao=c.getString(longitudeIx);

                names.add(namedao);
                Double l1=Double.parseDouble(latdao);
                Double l2=Double.parseDouble(longdao);
                LatLng latLngdao=new LatLng(l1,l2);
                locations.add(latLngdao);
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void yerinekoy(){
        for (int i=0;i<names.size();i++){
            LatLng latLng=new LatLng(locations.get(i).latitude,locations.get(i).longitude);
            MapsActivity.mMap.addMarker(new MarkerOptions().title(names.get(i)).position(latLng));
            Log.e("noktalar",String.valueOf(latLng));
        }
        names.clear();
        locations.clear();
    }
}

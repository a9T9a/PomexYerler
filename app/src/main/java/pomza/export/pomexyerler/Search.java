package pomza.export.pomexyerler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    SQLiteDatabase database;
    private ArrayList<Integer> id=new ArrayList<>();
    static ArrayList<String> names=new ArrayList<String>();
    static ArrayList<LatLng> locations=new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private EditText textSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Yer Bul");

        listView=findViewById(R.id.listviewsearch);
        textSearch=findViewById(R.id.editTextsearch);

        id.clear();
        names.clear();
        locations.clear();

        try {
            database=getApplicationContext().openOrCreateDatabase("PomexNoktalar",MODE_PRIVATE,null);
            Cursor c=database.rawQuery("SELECT * FROM noktalar",null);

            int idIx=c.getColumnIndex("nokta_id");
            int nameIx=c.getColumnIndex("name");
            int latitudeIx=c.getColumnIndex("latitude");
            int longitudeIx=c.getColumnIndex("longitude");

            while(c.moveToNext()){
                int iddao=c.getInt(idIx);
                String namedao=c.getString(nameIx);
                String latdao=c.getString(latitudeIx);
                String longdao=c.getString(longitudeIx);
                Log.e("names",namedao);
                id.add(iddao);
                names.add(namedao);
                Double l1=Double.parseDouble(latdao);
                Double l2=Double.parseDouble(longdao);
                LatLng latLngdao=new LatLng(l1,l2);
                locations.add(latLngdao);
            }
            c.close();
        }catch(Exception e){
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        listView.setAdapter(adapter);

        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                id.clear();
                names.clear();
                locations.clear();

                try {
                    database=getApplicationContext().openOrCreateDatabase("PomexNoktalar",MODE_PRIVATE,null);
                    Cursor c=database.rawQuery("SELECT * FROM noktalar WHERE name like '%"+editable+"%'",null);

                    int idIx=c.getColumnIndex("nokta_id");
                    int nameIx=c.getColumnIndex("name");
                    int latitudeIx=c.getColumnIndex("latitude");
                    int longitudeIx=c.getColumnIndex("longitude");

                    while(c.moveToNext()){
                        int iddao=c.getInt(idIx);
                        String namedao=c.getString(nameIx);
                        String latdao=c.getString(latitudeIx);
                        String longdao=c.getString(longitudeIx);
                        Log.e("names",namedao);
                        id.add(iddao);
                        names.add(namedao);
                        Double l1=Double.parseDouble(latdao);
                        Double l2=Double.parseDouble(longdao);
                        LatLng latLngdao=new LatLng(l1,l2);
                        locations.add(latLngdao);
                    }
                    c.close();
                }catch(Exception e){
                    Toast.makeText(Search.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                adapter=new ArrayAdapter<String>(Search.this,android.R.layout.simple_list_item_1,names);
                listView.setAdapter(adapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("locations",locations.get(i));
                intent.putExtra("position",i);
                intent.putExtra("info","list1");
                startActivity(intent);
            }
        });

    }
}

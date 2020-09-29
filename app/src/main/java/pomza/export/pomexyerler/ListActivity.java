package pomza.export.pomexyerler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    SQLiteDatabase database;
    private ArrayList<Integer> id=new ArrayList<>();
    private ArrayList<String> names=new ArrayList<String>();
    private ArrayList<LatLng> locations=new ArrayList<>();
    private ArrayList<byte[]> images=new ArrayList<>();
    private RecyclerView Rv;
    private RcAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //id.clear();
        //names.clear();
        //locations.clear();
        //images.clear();

        Toast.makeText(this,"Nokta Bilgileri İçin Uzun Basın",Toast.LENGTH_LONG).show();
        Rv=findViewById(R.id.rcview);
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this));

        try {
            database=getApplicationContext().openOrCreateDatabase("PomexNoktalar",MODE_PRIVATE,null);
            Cursor c=database.rawQuery("SELECT * FROM noktalar",null);

            int idIx=c.getColumnIndex("nokta_id");
            int nameIx=c.getColumnIndex("name");
            int latitudeIx=c.getColumnIndex("latitude");
            int longitudeIx=c.getColumnIndex("longitude");
            int imageIx=c.getColumnIndex("malzeme");

            while(c.moveToNext()){
                int iddao=c.getInt(idIx);
                String namedao=c.getString(nameIx);
                String latdao=c.getString(latitudeIx);
                String longdao=c.getString(longitudeIx);
                byte[] imagedao=c.getBlob(imageIx);

                id.add(iddao);
                names.add(namedao);
                Log.e("names",namedao);
                Double l1=Double.parseDouble(latdao);
                Double l2=Double.parseDouble(longdao);
                LatLng latLngdao=new LatLng(l1,l2);
                locations.add(latLngdao);
                if (imagedao!=null){
                    images.add(imagedao);
                }else{
                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bluedot);
                    bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
                    byte[] bitmapdata=stream.toByteArray();
                    images.add(bitmapdata);
                }
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        adapter=new RcAdapter(this,id,names,locations,images);
        Rv.setAdapter(adapter);
    }
}

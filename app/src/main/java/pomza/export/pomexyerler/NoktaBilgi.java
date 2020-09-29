package pomza.export.pomexyerler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.DatabaseMetaData;

public class NoktaBilgi extends AppCompatActivity {

    private ImageView malzeme,analiz;
    private EditText nokta,aciklama;
    private SQLiteDatabase database;
    private int id;
    private byte[] malzemebmp,analizbmp;
    private String namedao,aciklamadao,imagepath;
    private Bitmap newmalemebitmap,newanalizbitmap,bmpmalzeme,bmpanaliz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokta_bilgi);

        malzeme=findViewById(R.id.imageView_malzeme);
        analiz=findViewById(R.id.imageView_analiz);
        nokta=findViewById(R.id.editText_noktaisim);
        aciklama=findViewById(R.id.editText_aciklama);
        id=getIntent().getIntExtra("id",0);
        Log.e("gelenid",String.valueOf(id));

        ContentResolver contentResolver=getContentResolver();


        try {
            database=getApplicationContext().openOrCreateDatabase("PomexNoktalar",MODE_PRIVATE,null);
            Cursor c=database.rawQuery("SELECT * FROM noktalar WHERE nokta_id="+id,null);

            int nameIx=c.getColumnIndex("name");
            int aciklamaIx=c.getColumnIndex("aciklama");
            int malzemeIx=c.getColumnIndex("malzeme");
            int analizIx=c.getColumnIndex("analiz");

            while (c.moveToNext()){
                namedao=c.getString(nameIx);
                nokta.setText(namedao);
                aciklamadao=c.getString(aciklamaIx);
                aciklama.setText(aciklamadao);
                malzemebmp=c.getBlob(malzemeIx);
                bmpmalzeme= BitmapFactory.decodeByteArray(malzemebmp,0,malzemebmp.length);
                malzeme.setImageBitmap(bmpmalzeme);
                analizbmp=c.getBlob(analizIx);
                bmpanaliz=BitmapFactory.decodeByteArray(analizbmp,0,analizbmp.length);
                analiz.setImageBitmap(bmpanaliz);
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }


        malzeme.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mResim();
                return false;
            }
        });

        analiz.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                aResim();
                return false;
            }
        });

    }

    public void mResim(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    public void aResim(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},11);
        }else{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if(grantResults.length>0){
                    if (requestCode==1){
                        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                        {
                            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,2);
                        }
                    }else if(requestCode==11){
                        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                        {
                            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,3);
                        }
                    }
                }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==RESULT_OK && data!=null) {
            Uri uri = data.getData();
           imagepath =String.valueOf(uri.getPath());
            try {
                newmalemebitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                malzeme.setImageBitmap(newmalemebitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (requestCode==3 && resultCode==RESULT_OK && data!=null){
            Uri uri = data.getData();
            try {
                newanalizbitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                analiz.setImageBitmap(newanalizbitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void KayitGuncelle (View view){
        database = getApplicationContext().openOrCreateDatabase("PomexNoktalar", MODE_PRIVATE, null);
        String toComplie="UPDATE noktalar SET name=?, aciklama=?, malzeme=?, analiz=?, malzeme_path=? WHERE nokta_id=?";
        SQLiteStatement sqLiteStatement=database.compileStatement(toComplie);
        sqLiteStatement.bindString(1,nokta.getText().toString());
        sqLiteStatement.bindString(2,aciklama.getText().toString());
        if (newmalemebitmap!=null){
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            newmalemebitmap.compress(Bitmap.CompressFormat.PNG,5,outputStream);
            byte[] bmpbyte=outputStream.toByteArray();
            sqLiteStatement.bindBlob(3,bmpbyte);
            sqLiteStatement.bindString(6,imagepath);
        }else{
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            bmpmalzeme.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            byte[] bmpbyte=outputStream.toByteArray();
            sqLiteStatement.bindBlob(3,bmpbyte);
        }
        if (newanalizbitmap!=null){
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            newanalizbitmap.compress(Bitmap.CompressFormat.PNG,5,outputStream);
            byte[] bmpbyte=outputStream.toByteArray();
            sqLiteStatement.bindBlob(4,bmpbyte);
        }else{
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            bmpanaliz.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            byte[] bmpbyte=outputStream.toByteArray();
            sqLiteStatement.bindBlob(4,bmpbyte);
        }
        sqLiteStatement.bindString(5,String.valueOf(id));
        sqLiteStatement.execute();
        database.close();



        Intent intent=new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}

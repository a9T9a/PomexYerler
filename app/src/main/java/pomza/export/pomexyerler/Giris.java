package pomza.export.pomexyerler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.io.File;

public class Giris extends AppCompatActivity {
    private Button buttonGiris, buttonKayit;
    private EditText editTextName, editTextPass;
    private CheckBox cbox;
    private SharedPreferences mPrefs;
    private static final String PREFS_NAME="PrefsFile";
    private File ff;
    private Integer izn_kont1;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        getSupportActionBar().hide();

        mPrefs=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        editTextName=findViewById(R.id.editTextName);
        editTextPass=findViewById(R.id.editTextPass);
        buttonGiris=findViewById(R.id.buttonGiris);
        buttonKayit=findViewById(R.id.buttonKayit);
        cbox=findViewById(R.id.checkBoxHatirla);


        ff=new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        buttonKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    database=getApplicationContext().openOrCreateDatabase("PomexNoktalar",MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, password VARCHAR)");
                    String toComplie="INSERT INTO users (name,password) VALUES (?,?)";
                    SQLiteStatement sqLiteStatement=database.compileStatement(toComplie);
                    sqLiteStatement.bindString(1,editTextName.getText().toString());
                    sqLiteStatement.bindString(2,editTextPass.getText().toString());
                    sqLiteStatement.execute();
                    Toast.makeText(Giris.this,"Kayıt Edildi",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(Giris.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbox.isChecked()){
                    Boolean bchecked=cbox.isChecked();
                    SharedPreferences.Editor spe=mPrefs.edit();
                    spe.putString("pref_name",editTextName.getText().toString());
                    spe.putString("pref_pass",editTextPass.getText().toString());
                    spe.putBoolean("pref_check",bchecked);
                    spe.apply();

                }else{
                    mPrefs.edit().clear().apply();
                }

                database=getApplicationContext().openOrCreateDatabase("PomexNoktalar",MODE_PRIVATE,null);
                Cursor c=database.rawQuery("SELECT * FROM users WHERE name=? and password=?",new String[]{editTextName.getText().toString(),editTextPass.getText().toString()});
                if(c.getCount()>0){
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"Kullanıcı Bulunamadı",Toast.LENGTH_SHORT).show();
                }
            }
        });
        getPreferences();
    }

    private void getPreferences() {
        SharedPreferences sp= getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if(sp.contains("pref_name")){
            String u=sp.getString("pref_name","nothing");
            editTextName.setText(u.toString());
        }
        if (sp.contains("pref_pass")){
            String u=sp.getString("pref_pass","nothing");
            editTextPass.setText(u.toString());
        }
        if (sp.contains("pref_check")){
            Boolean b=sp.getBoolean("pref_check",false);
            cbox.setChecked(b);
        }
    }

}

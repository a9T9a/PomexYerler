package pomza.export.pomexyerler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class RcAdapter extends RecyclerView.Adapter<RcAdapter.NesneTutucu> {
    private Context context;
    private ArrayList<Integer> idlist;
    private ArrayList<String> aciklamalist;
    static ArrayList<String> noktalist;
    static ArrayList<LatLng> loclist;
    private ArrayList<byte[]> images;

    public RcAdapter(Context context, ArrayList<Integer> idlist, ArrayList<String> noktalist, ArrayList<LatLng> latLngs, ArrayList<byte[]> images) {
        this.context = context;
        this.idlist = idlist;
        this.noktalist = noktalist;
        this.loclist = latLngs;
        this.images=images;
    }

    public class NesneTutucu extends RecyclerView.ViewHolder{
        public ImageView resim;
        public TextView satir;
        public TextView aciklama;
        public TextView koordinat;

        public NesneTutucu(View view){
            super(view);
            resim=view.findViewById(R.id.imageview1);
            satir=view.findViewById(R.id.tv_noktaismi);
            aciklama=view.findViewById(R.id.tv_aciklama);
            koordinat=view.findViewById(R.id.tv_koordinat);
        }
    }

    @NonNull
    @Override
    public NesneTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview=LayoutInflater.from(parent.getContext()).inflate(R.layout.kart_tasarimi,parent,false);
        return new NesneTutucu(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull NesneTutucu holder, final int position) {
        String text=noktalist.get(position);
        holder.satir.setText(text);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent=new Intent(context, NoktaBilgi.class);
                intent.putExtra("id",idlist.get(position));
                Log.e("id",String.valueOf(idlist.get(position)));
                context.startActivity(intent);
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,MapsActivity.class);
                intent.putExtra("info","list2");
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });

        Bitmap bmp = BitmapFactory.decodeByteArray(images.get(position),0,images.get(position).length);
        holder.resim.setImageBitmap(bmp);
        holder.koordinat.setText(loclist.get(position).latitude+"\n"+loclist.get(position).longitude);

    }

    @Override
    public int getItemCount() {
        return noktalist.size();
    }

}

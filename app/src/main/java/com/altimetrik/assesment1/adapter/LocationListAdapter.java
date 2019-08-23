package com.altimetrik.assesment1.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.altimetrik.assesment1.R;
import com.altimetrik.assesment1.realm.LocationDBObject;

import java.util.ArrayList;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    Context context;
    ArrayList<LocationDBObject> locationList;

    public LocationListAdapter(Context context, ArrayList<LocationDBObject> locationList)
    {
        this.context=context;
        this.locationList=locationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_location, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        LocationDBObject object=locationList.get(i);
        viewHolder.txtLatitude.setText(object.getLatitude());
        viewHolder.txtLongitude.setText(object.getLongitude());
        viewHolder.txtTime.setText(object.getLastUpdatedTime());

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtLatitude,txtLongitude,txtTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLatitude=(TextView)itemView.findViewById(R.id.txtLatitude);
            txtLongitude=(TextView)itemView.findViewById(R.id.txtLongitude);
            txtTime=(TextView)itemView.findViewById(R.id.txtTime);

        }
    }
}

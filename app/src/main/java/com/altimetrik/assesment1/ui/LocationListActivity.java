package com.altimetrik.assesment1.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.altimetrik.assesment1.R;
import com.altimetrik.assesment1.adapter.LocationListAdapter;
import com.altimetrik.assesment1.realm.LocationDBObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public class LocationListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LocationListAdapter locationListAdapter;;
    ArrayList<LocationDBObject> arrayList = new ArrayList<LocationDBObject>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setAdapter();

    }

    private void setAdapter() {

        Realm realm = Realm.getDefaultInstance();
        if(!realm.isInTransaction())
        realm.beginTransaction();

        List<LocationDBObject> realmResults = realm.where(LocationDBObject.class).findAll();


        for (LocationDBObject results : realmResults) {

            LocationDBObject locationObject = new LocationDBObject();
            locationObject.lastUpdatedTime = results.lastUpdatedTime;
            locationObject.latitude = results.latitude;
            locationObject.longitude = results.longitude;

            arrayList.add(locationObject);
        }

        locationListAdapter = new LocationListAdapter(this, arrayList);
        recyclerView.setAdapter(locationListAdapter);
    }



}

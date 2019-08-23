package com.altimetrik.assesment1.realm;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {

    private static RealmController instance;
    private static  Realm realm;

    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with() {

        if (instance == null) {
            instance = new RealmController();
        }
        return instance;
    }


    public static RealmController getInstance() {

        return instance;
    }

    public static Realm getRealm() {

        return realm;
    }

    //Refresh the realm instance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from LocationDBObject.class
    public void clearAll() {

        realm.beginTransaction();
        realm.delete(LocationDBObject.class);
        realm.commitTransaction();
    }

    //find all objects in the LocationDBObject.class
    public RealmResults<LocationDBObject> getLocations() {

        realm.beginTransaction();
        RealmResults<LocationDBObject> realmResults = realm.where(LocationDBObject.class).findAll();

        realm.commitTransaction();
        return realmResults;
    }


    //query a single item with the given id
    public LocationDBObject getLocation(int id) {
        realm.beginTransaction();
        LocationDBObject contact = realm.where(LocationDBObject.class).equalTo("id", id).findFirst();
        realm.commitTransaction();
        return contact;
    }

    //check if LocationDBObject.class is empty
    public boolean hasContacts() {

        return !realm.isEmpty();
    }

}

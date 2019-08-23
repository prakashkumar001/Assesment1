package com.altimetrik.assesment1;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class GlobalClass extends Application {
    public static GlobalClass instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Realm.init(instance);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("userlocation")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }


}

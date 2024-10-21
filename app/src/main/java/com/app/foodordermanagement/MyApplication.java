package com.app.foodordermanagement;

import android.app.Application;
import android.content.Context;

import com.app.foodordermanagement.prefs.DataStoreManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    private static final String FIREBASE_URL = "https://cafemanagement-d3310-default-rtdb.firebaseio.com";
    private FirebaseDatabase mFirebaseDatabase;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        DataStoreManager.init(getApplicationContext());
    }

    public DatabaseReference getVoucherDatabaseReference() {
        return mFirebaseDatabase.getReference("voucher");
    }

    public DatabaseReference getAddressDatabaseReference() {
        return mFirebaseDatabase.getReference("address");
    }

    public DatabaseReference getPaymentMethodDatabaseReference() {
        return mFirebaseDatabase.getReference("payment");
    }

    public DatabaseReference getCategoryDatabaseReference() {
        return mFirebaseDatabase.getReference("category");
    }

    public DatabaseReference getFoodDatabaseReference() {
        return mFirebaseDatabase.getReference("food");
    }

    public DatabaseReference getFoodDetailDatabaseReference(int foodId) {
        return mFirebaseDatabase.getReference("food/" + foodId);
    }

    public DatabaseReference getToppingDatabaseReference() {
        return mFirebaseDatabase.getReference("topping");
    }

    public DatabaseReference getFeedbackDatabaseReference() {
        return mFirebaseDatabase.getReference("/feedback");
    }

    public DatabaseReference getOrderDatabaseReference() {
        return mFirebaseDatabase.getReference("order");
    }

    public DatabaseReference getRatingFoodDatabaseReference(String foodId) {
        return mFirebaseDatabase.getReference("/food/" + foodId + "/rating");
    }

    public DatabaseReference getOrderDetailDatabaseReference(long orderId) {
        return mFirebaseDatabase.getReference("order/" + orderId);
    }
}

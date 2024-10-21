package com.app.foodordermanagement.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.foodordermanagement.R;
import com.app.foodordermanagement.adapter.ContactAdapter;
import com.app.foodordermanagement.model.Contact;
import com.app.foodordermanagement.utils.GlobalFunction;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends BaseActivity {


    private TextView tvAboutUsTitle, tvAboutUsContent, tvAboutUsWebsite;
    private LinearLayout layoutWebsite;
    private RecyclerView rcvData;

    private ContactAdapter mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initToolbar();
        initUi();
        initData();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.contact));
    }

    private void initUi() {
        rcvData = findViewById(R.id.rcvData);
    }

    private void initData() {

        mContactAdapter = new ContactAdapter(this, getListContact(),
                () -> GlobalFunction.callPhoneNumber(this));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rcvData.setNestedScrollingEnabled(false);
        rcvData.setFocusable(false);
        rcvData.setLayoutManager(layoutManager);
        rcvData.setAdapter(mContactAdapter);
    }


    public List<Contact> getListContact() {
        List<Contact> contactArrayList = new ArrayList<>();
        contactArrayList.add(new Contact(Contact.FACEBOOK, R.drawable.ic_facebook));
        contactArrayList.add(new Contact(Contact.GMAIL, R.drawable.ic_gmail));

        return contactArrayList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactAdapter.release();
    }
}

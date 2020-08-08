package com.example.myecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import static com.example.myecommerce.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressesActivity extends AppCompatActivity {

    private RecyclerView myAddressesRecyclerView;
    private Button deliverHereBtn;
    private static AddressesAdapter addressesAdapter;// here static is used used because we are going to access AddressesAdapter in public static refreshItem

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myAddressesRecyclerView = findViewById(R.id.addresses_recyclerview);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(layoutManager);

        List<AddressesModel> addressesModelList = new ArrayList<>();
        addressesModelList.add(new AddressesModel("John Cena","Pacific Ocean","98723984",true));
        addressesModelList.add(new AddressesModel("John Rai","Pacific Ocean","3434",false));
        addressesModelList.add(new AddressesModel("Sam Cena","Pacific Ocean","343234",false));
        addressesModelList.add(new AddressesModel("Bikram Cena","Pacific Ocean","565",false));
        addressesModelList.add(new AddressesModel("John Rim","Pacific Ocean","456456",false));
        addressesModelList.add(new AddressesModel("Sita Cena","Pacific Ocean","678768",false));
        addressesModelList.add(new AddressesModel("Simiti Cena","Pacific Ocean","235235",false));

        int mode = getIntent().getIntExtra("MODE", -1);
        if (mode == SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else {
            deliverHereBtn.setVisibility(View.INVISIBLE);
        }
        addressesAdapter = new AddressesAdapter(addressesModelList, mode);
        myAddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);// it disable the fade animation while refreshing the items in the page
        addressesAdapter.notifyDataSetChanged();// it refreshes whole list
    }

    public static void refreshItem(int deselect, int select){
        addressesAdapter.notifyItemChanged(deselect);// it refreshes the item or provided layout
        addressesAdapter.notifyItemChanged(select);// it refreshes the item or provided layout
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
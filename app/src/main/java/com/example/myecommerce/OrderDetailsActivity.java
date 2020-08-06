package com.example.myecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

public class OrderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);// for title set
        getSupportActionBar().setTitle("Order details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// for back arrow
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // for back arrow function
        if (item.getItemId() == android.R.id.home){
            finish(); // finishes the activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
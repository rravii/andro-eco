package com.example.myecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Deals of the Day");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        gridView = findViewById(R.id.grid_view);

        int layout_code = getIntent().getIntExtra("layout_code",-1);

        if(layout_code == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            List<WishlistModel> wishlistModelList = new ArrayList<>();
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 1, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 0, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 2, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 4, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 0, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 0, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 0, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 0, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));
            wishlistModelList.add(new WishlistModel(R.mipmap.mobile, "Oppo awf", 0, "3", 123, "Rs.4999/-", "Rs.5999/-", "Cash on delivery"));

            WishlistAdapter adapter = new WishlistAdapter(wishlistModelList, false);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }else if (layout_code == 1) {

            gridView.setVisibility(View.VISIBLE);

            List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();


            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(horizontalProductScrollModelList);
            gridView.setAdapter(gridProductLayoutAdapter);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
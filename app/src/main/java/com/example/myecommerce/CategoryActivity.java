package com.example.myecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// back arrow will be shown

        categoryRecyclerView = findViewById(R.id.category_recyclerview);

        //////////////// Banner Slider
        List<SliderModel>sliderModelList = new ArrayList<SliderModel>();

        sliderModelList.add(new SliderModel(R.mipmap.home_icon,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.bell,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.black_email,"#077AE4"));

        sliderModelList.add(new SliderModel(R.mipmap.blue_email,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.ic_launcher,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.app_icon,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.black_cart,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.profile_placeholder,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.home_icon,"#077AE4"));

        sliderModelList.add(new SliderModel(R.mipmap.bell,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.black_email,"#077AE4"));
        sliderModelList.add(new SliderModel(R.mipmap.blue_email,"#077AE4"));

        //////////////// Banner Slider


        ////////////// Horizontal Product Layout

        List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_icon,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_round_icon,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.close_cross,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_icon,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_icon,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_icon,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_icon,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_icon,"Redmi 5A","SD ......","Rs 1000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.app_icon,"Redmi 5A","SD ......","Rs 1000"));

        ////////////// Horizontal Product Layout


        //////////////////////////////////////////////

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManager);

        List<HomePageModel> homePageModelList = new ArrayList<>();
        homePageModelList.add(new HomePageModel(0,sliderModelList));
        homePageModelList.add(new HomePageModel(1,R.mipmap.error,"#000000"));
        homePageModelList.add(new HomePageModel(2,"Deals of the Day",horizontalProductScrollModelList));
        homePageModelList.add(new HomePageModel(3,"Deals of the Day",horizontalProductScrollModelList));
        homePageModelList.add(new HomePageModel(1,R.mipmap.banner,"#ffff00"));
        homePageModelList.add(new HomePageModel(3,"Deals of the Day",horizontalProductScrollModelList));
        homePageModelList.add(new HomePageModel(2,"Deals of the Day",horizontalProductScrollModelList));
        homePageModelList.add(new HomePageModel(1,R.mipmap.black_cart,"#ff0000"));

        HomePageAdapter adapter = new HomePageAdapter(homePageModelList);
        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //////////////////////////////////////////////
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here.The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if(id == R.id.main_search_icon){
            ///todo: search
            return true;
        }else if(id == android.R.id.home){ // back arrow key id when entered in category
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
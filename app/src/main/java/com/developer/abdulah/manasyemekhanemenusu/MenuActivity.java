package com.developer.abdulah.manasyemekhanemenusu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.abdulah.manasyemekhanemenusu.models.Menu;
import com.squareup.picasso.Picasso;

public class MenuActivity extends AppCompatActivity {

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        menu = getIntent().getParcelableExtra("menu");

        setTitle(menu.date);

        Picasso.with(this)
                .load("https://i.nefisyemektarifleri.com/2015/08/05/tas-kebabi-500x333.jpeg")
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .into((ImageView) findViewById(R.id.first_meal_image_view));

        //https://www.googleapis.com/customsearch/v1?q=cacik&searchType=image&cx=008494605432352896394:-kofdxnaoa4&key=AIzaSyAM_wlpyq5aEeFbZht7K4-X60Gz0BEbr_g

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

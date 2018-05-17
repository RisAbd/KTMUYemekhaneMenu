package com.developer.abdulah.manasyemekhanemenusu;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.developer.abdulah.manasyemekhanemenusu.db.Database;
import com.developer.abdulah.manasyemekhanemenusu.models.Meal;
import com.developer.abdulah.manasyemekhanemenusu.models.Menu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MenuAdapter.MealClickListener {

    public static final String TAG = "MainActivity";

    private static final String MENU_URL = "http://bis.manas.edu.kg/menu/";

    private ArrayList<Menu> menus = new ArrayList<>();
    private MenuAdapter menuAdapter;
    private ListView listView;

    private ProgressBar spinner;

    public void setMenus(ArrayList<Menu> menus) {

        int todaysPosition = todaysMenuIndex(menus, 0);

        this.menus = menus;

        menuAdapter = new MenuAdapter(this, getLayoutInflater(), menus, todaysPosition);

        listView.setAdapter(menuAdapter);

        menuAdapter.setMealClickListener(this);

        listView.setSelection(todaysPosition);
//        listView.smoothScrollToPosition(todaysPosition);

        spinner.setVisibility(View.INVISIBLE);
    }

    class MenuFetcher implements Runnable {

        void fetch() {
            ArrayList<Menu> menus = Database.getInstance(MainActivity.this).getMenus();

            if (menus == null) {
                new Thread(this).start();
            } else {
                setMenus(menus);
            }
        }

        @Override
        public void run() {

            URL url;
            try {
                url = new URL(MENU_URL);
            } catch (MalformedURLException e) {
                Log.e(TAG, "onCreate: ", e);
                return;
            }

            try {
                Document document = Jsoup.parse(url, 10000);

                Element tableBody = document.select("table").get(0).select("tbody").get(0);

                final ArrayList<Menu> menus = new ArrayList<>();

                Elements rows = tableBody.select("tr");
                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    menus.add(Menu.fromJsoupTableRowElement(row));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Database.getInstance(MainActivity.this).saveMenus(menus);
                        setMenus(menus);
                    }
                });

            } catch (IOException e) {
                Log.e(TAG, "onCreate: ", e);
            }
        }
    }

    int todaysMenuIndex(ArrayList<Menu> menus, int defaultIndex) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = df.format(new Date());
        for (int i = 0; i < menus.size(); i++) {
            if (todayDate.equals(menus.get(i).date)) {
                return i;
            }
        }
        return defaultIndex;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.menus_listview);

        listView.setOnItemClickListener(this);

        spinner = (ProgressBar) findViewById(R.id.progressbar);

        new MenuFetcher().fetch();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        Intent intent = new Intent(this, MenuActivity.class);
//        intent.putExtra("menu", menus.get(position));
//
//        startActivity(intent);
    }

    @Override
    public void onMealClick(Meal m) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getBrousertURL()));
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        } else {
            Toast.makeText(this, "No browsers installed on device", Toast.LENGTH_SHORT).show();
        }
    }
}

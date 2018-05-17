package com.developer.abdulah.manasyemekhanemenusu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.abdulah.manasyemekhanemenusu.models.Menu;
import com.developer.abdulah.manasyemekhanemenusu.models.Meal;

import java.util.List;
import java.util.Locale;

/**
 * Created by dev on 1/5/18.
 */

public class MenuAdapter extends BaseAdapter implements View.OnClickListener {

    public interface MealClickListener {
        void onMealClick(Meal m);
    }

    private static final String TAG = "MenuAdapter";

    private List<Menu> menus;
    private LayoutInflater inflater;
    private int todaysMenuPosition = 0;

    private MealClickListener mealClickListener;

    private int todaysMenuColor;
    private int normalMenuColor;

    public MenuAdapter(Context context, LayoutInflater inflater, List<Menu> menus, int todaysMenuPosition) {

        this.todaysMenuColor = context.getResources().getColor(R.color.today_menu_background);
        this.normalMenuColor = context.getResources().getColor(R.color.normal_menu_background);

        this.inflater = inflater;
        this.menus = menus;
        this.todaysMenuPosition = todaysMenuPosition;
    }

    public void setMealClickListener(MealClickListener mealClickListener) {
        this.mealClickListener = mealClickListener;
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Menu getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) convertView;
        if (view == null) {
            view = (ViewGroup) inflater.inflate(R.layout.menu_listview_item, parent, false);
        }

        Menu menu = getItem(position);

        TextView dateTV = (TextView) view.findViewById(R.id.date_textview);

        dateTV.setText(menu.date);
        ((TextView) view.findViewById(R.id.calories_sum)).setText(String.format(Locale.getDefault(), "%d kkal", menu.getTotalCalories()));

        dateTV.setTextColor(position == todaysMenuPosition ? todaysMenuColor : normalMenuColor);

        LinearLayout mealsContainer = ((LinearLayout) view.findViewById(R.id.meals_container));

        int mi = 0;
        for (int i = 0; i < mealsContainer.getChildCount(); i++) {
            View ch = mealsContainer.getChildAt(i);


            Object chTag = ch.getTag();
            if (chTag != null && (chTag.equals("meal") || chTag instanceof Meal)) {

                Meal meal = menu.meals[mi];
                mi += 1;

                ViewGroup ll = (LinearLayout) ch;

                TextView mealNameTV = (TextView) ll.getChildAt(0);
                TextView mealCaloryTV = (TextView) ll.getChildAt(1);

                mealNameTV.setText(meal.name);
                mealCaloryTV.setText(String.format(Locale.getDefault(), "%d", meal.calories));

                ch.setOnClickListener(this);
                ch.setTag(meal);
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Meal meal = (Meal) v.getTag();
        if (mealClickListener != null) {
            mealClickListener.onMealClick(meal);
        }
    }
}

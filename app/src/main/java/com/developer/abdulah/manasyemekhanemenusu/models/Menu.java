package com.developer.abdulah.manasyemekhanemenusu.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Locale;

/**
 * Created by dev on 1/5/18.
 */

public class Menu implements Parcelable {

    public final String date;
    public final Meal[] meals;

    private int dbId;

    public static Menu fromJsoupTableRowElement(Element tableRow) {
        Elements cols = tableRow.select("td");
        String date = cols.get(0).text();

        Meal[] meals = new Meal[(cols.size()-2) / 2];
        for (int i = 1; i < cols.size()-1; i += 2) {
            meals[(i-1)/2] = Meal.fromJsoupElements(cols.get(i), cols.get(i+1));
        }

        return new Menu(date, meals, -1);
    }

    public Menu(String date, Meal[] meals, int dbId) {
        this.date = date;
        this.meals = meals;
        this.dbId = dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public int getTotalCalories() {
        int out = 0;
        for (int i = 0; i < meals.length; i++) {
            Meal m = meals[i];
            if (m.name.startsWith("Ekmek")) { continue; }

            out += m.calories;
        }
        return out;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeTypedArray(meals, flags);
        dest.writeInt(dbId);
    }


    public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel source) {
            return new Menu(source.readString(), source.createTypedArray(Meal.CREATOR), source.readInt());
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < meals.length; i++) {
            Meal m = meals[i];
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(m.toString());
        }
        return String.format(Locale.getDefault(), "Menu(id: %d, date: %s, meals: [%s])", dbId, date, sb.toString());
    }
}

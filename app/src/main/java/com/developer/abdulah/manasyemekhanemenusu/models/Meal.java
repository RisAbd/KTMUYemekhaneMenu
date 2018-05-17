package com.developer.abdulah.manasyemekhanemenusu.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.nodes.Element;

import java.util.Locale;

/**
 * Created by dev on 1/5/18.
 */

public class Meal implements Parcelable {

    private static final String SEARCH_ENGINE_ID = "008494605432352896394:-kofdxnaoa4";
    private static final String API_KEY = "AIzaSyAM_wlpyq5aEeFbZht7K4-X60Gz0BEbr_g";

    private static final String SEARCH_URL = "https://www.googleapis.com/customsearch/v1?q=%s&imgSize=large&cr=countryTR&hl=kg&gl=kg&searchType=image&cx="+SEARCH_ENGINE_ID+"&key="+API_KEY;

    private static final String BROWSER_URL_TEMPLATE = "https://www.google.com.tr/search?um=1&hl=tr&rls=tr&channel=suggest&tbm=isch&sa=1&q=%s";

    public final String name;
    public final int calories;

    private int dbId;

    public static Meal fromJsoupElements(Element nameElement, Element caloriesElement) {
        String name = nameElement.select("font").get(0).text();
        int calories = Integer.valueOf(caloriesElement.select("font").get(0).text());
        return new Meal(name, calories, -1);
    }

    public static Meal fromCursor(Cursor c, String idColumn) {
        String name = c.getString(c.getColumnIndex("name"));
        int cal = c.getInt(c.getColumnIndex("calory"));
        int id = c.getInt(c.getColumnIndex(idColumn));
        return new Meal(name, cal, id);
    }

    public Meal(String name, int calories, int dbId) {
        this.name = name;
        this.calories = calories;
        this.dbId = dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public String getCustomSearchURL() {
        return String.format(SEARCH_URL, name);
    }

    public String getBrousertURL() {
        return String.format(BROWSER_URL_TEMPLATE, name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(calories);
        dest.writeInt(dbId);
    }

    public static final Parcelable.Creator<Meal> CREATOR = new Parcelable.Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel source) {
            return new Meal(source.readString(), source.readInt(), source.readInt());
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Meal(id: %d, \"%s\", %d cal)", dbId, name, calories);
    }
}

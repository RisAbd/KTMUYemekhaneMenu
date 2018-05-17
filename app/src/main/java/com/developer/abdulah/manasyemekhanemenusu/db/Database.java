package com.developer.abdulah.manasyemekhanemenusu.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import com.developer.abdulah.manasyemekhanemenusu.models.Meal;
import com.developer.abdulah.manasyemekhanemenusu.models.Menu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dev on 4/12/18.
 */

public class Database extends SQLiteOpenHelper {

    private static Database instance = null;

    public static Database getInstance(Context ctx) {
        if (instance == null) {
            instance = new Database(ctx);
        }
        return instance;
    }

    public static final String NAME = "manas_yemekhane.sqlite";
    public static final int VERSION = 2;

    private class Tables {
        static final String MENUS = "menus";
        static final String MEALS = "meals";
        static final String MENU_MEALS = "menu_meals";

    }

    private Database(Context ctx) {
        super(ctx, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+Tables.MEALS+" (id integer primary key, name text, calory int)");
        db.execSQL("create table if not exists "+Tables.MENUS+" (id integer primary key, date text unique)");
        db.execSQL("create table if not exists "+Tables.MENU_MEALS+" (menu int, pos int, meal int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL("drop table if exists "+Tables.MENU_MEALS);
            db.execSQL("create table if not exists "+Tables.MENU_MEALS+" (menu int, pos int, meal int)");
        }
    }

    public @Nullable ArrayList<Menu> getMenus() {
        return getMenusForMonth(new Date());
    }

    public @Nullable ArrayList<Menu> getMenusForMonth(Date d) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-%", Locale.getDefault());
        String dateString = df.format(d);

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "select m.id as menu_id, m.date, me.id as meal_id, me.name, me.calory from "+Tables.MENUS+" m " +
                        "inner join "+Tables.MENU_MEALS+" mm on m.id = mm.menu " +
                        "inner join "+Tables.MEALS+" me on mm.meal = me.id " +
                        "where m.date like ? " +
                        "order by m.id, mm.pos ",
                new String[] {dateString});

        try {

            if (c.moveToFirst()) {
                ArrayList<Menu> menus = new ArrayList<>(31);
                do {
                    String date = c.getString(c.getColumnIndex("date"));
                    int id = c.getInt(c.getColumnIndex("menu_id"));
                    Meal[] meals = new Meal[4];
                    menus.add(new Menu(date, meals, id));

                    for (int i = 0; i < 4; i++) {
                        meals[i] = Meal.fromCursor(c, "meal_id");
                        c.moveToNext();
                    }

                } while (c.moveToNext());

                return menus;
            } else {
                return null;
            }
        } finally {
            c.close();
            db.close();
        }
    }

    public void saveMenus(ArrayList<Menu> menus) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        SQLiteStatement insertMenu = db.compileStatement("insert into "+Tables.MENUS+" (date) values (?)");

        for (Menu m: menus) {
            insertMenu.bindString(1, m.date);
            int menuId = (int) insertMenu.executeInsert();
            m.setDbId(menuId);
        }

        SQLiteStatement insertMeal = db.compileStatement("insert into "+Tables.MEALS+" (name, calory) values (?, ?)");

        SQLiteStatement insertMenuMeal = db.compileStatement("insert into "+Tables.MENU_MEALS+" (menu, pos, meal) values (?, ?, ?)");

        for (Menu m: menus) {
            insertMenuMeal.bindLong(1, m.getDbId());
            for (int i = 0; i < m.meals.length; i++) {
                Meal meal = m.meals[i];

                insertMeal.bindString(1, meal.name);
                insertMeal.bindLong(2, meal.calories);

                int mealId = (int) insertMeal.executeInsert();
                meal.setDbId(mealId);

                insertMenuMeal.bindLong(2, i);
                insertMenuMeal.bindLong(3, mealId);
                insertMenuMeal.executeInsert();
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }
}

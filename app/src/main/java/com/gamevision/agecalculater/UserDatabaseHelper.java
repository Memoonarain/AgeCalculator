package com.gamevision.agecalculater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "birthday_app.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_USERS = "users";

    public UserDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "gender TEXT, " +
                "birthdate TEXT, " +
                "special_date TEXT,"+
                "birthtime TEXT," +
                "special_time TEXT," +
                "category TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(String name, String gender, String birthdate, String specialDate,String birthtime, String specialTime, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("gender", gender);
        values.put("birthdate", birthdate);
        values.put("special_date", specialDate);
        values.put("birthtime", birthtime);
        values.put("special_time", specialTime);
        values.put("category", category);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                users.add(new UserModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }
    public List<UserModel> getUsersByCategory(String category) {
        List<UserModel> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "category = ?";
        String[] selectionArgs = { category };

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract data from cursor and create UserModel objects accordingly
                // Add each UserModel to userList
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return userList;
    }

}

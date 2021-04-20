package com.hv.briskybake.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import androidx.annotation.Nullable;

import com.hv.briskybake.Model.Order;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME="BriskybakeDB.db";
    private static final int DB_VER=1;
    public Database(@Nullable Context context) {
        super(context, DB_NAME,null,DB_VER);
    }

    public List<Order> getCarts()
    {
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb =new SQLiteQueryBuilder();

        String[] sqlSelect={"ProductName","ProductId","Quantity","Price","Discount"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result=new ArrayList<>();
        if(c.moveToFirst()){
            do {
                result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

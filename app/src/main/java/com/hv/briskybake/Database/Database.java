package com.hv.briskybake.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hv.briskybake.Model.Order;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME="BriskybakeDB.db";  //Database name
    private static final int DB_VER=5;  // Database Version
    private static final String TABLE_NAME = "OrderDetail";   // Table Name

    private static final String UID="ID"; // Column I (Primary Key)
    private static final String ProductNAME = "ProductName";
    private static final String ProductID = "ProductId";
    private static final String QUANTITY= "Quantity";
    private static final String PRICE= "Price";
    private static final String DISCOUNT= "Discount";
    private static final String IMAGE="Image";
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+ " ( "+UID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+ProductID+" TEXT, "+ProductNAME+" TEXT, "+QUANTITY+" TEXT, "+PRICE+" TEXT, "+DISCOUNT+" TEXT, "+IMAGE+" );";
    private static final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;
    private Context context;

    public Database(@Nullable Context context) {
        super(context, DB_NAME,null,DB_VER);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Toast.makeText(context, "On Upgrade", Toast.LENGTH_SHORT).show();
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public List<Order> getCarts()
    {
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb =new SQLiteQueryBuilder();

        String[] sqlSelect={"ID","ProductName","ProductId","Quantity","Price","Discount","Image"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result=new ArrayList<>();
        if(c.moveToFirst()){
            do {
                result.add(new Order(
                        c.getInt(c.getColumnIndex("ID")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount,Image)VALUES('%s','%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
    }
    public void cleanCart(){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }


    //Favorites
    public void addToFavorites(String foodId)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO Favorites(FoodId) Values('%s');",foodId);
        db.execSQL(query);
    }

    public void removeFromFavorites(String foodId)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM Favorites WHERE FoodId='%s';",foodId);
        db.execSQL(query);
    }

    public boolean isFavorites(String foodId)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT * FROM Favorites WHERE FoodId='%s';",foodId);
        Cursor cursor=db.rawQuery(query,null);
        if (cursor.getCount()<=0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getCountCarts() {
        int count=0;
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do{
                count=cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("UPDATE OrderDetail SET Quantity= %s WHERE ID= %d",order.getQuantity(),order.getID());
        db.execSQL(query);
    }

    public void removeFromCart(String productId, String phoneNumber) {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId='%s'",phoneNumber,productId);
        db.execSQL(query);
    }
}

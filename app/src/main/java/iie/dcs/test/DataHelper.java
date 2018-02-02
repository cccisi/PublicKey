package iie.dcs.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite实现公钥的管理
 */

public class DataHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "contact.db";

    public DataHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE contact(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,pubkey TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertContact(String name, String pubkey) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("pubkey", pubkey);
        contentValues.put("name", name);
        SQLiteDatabase db = this.getWritableDatabase();
        long rowId = db.insert("contact", null, contentValues);

        db.close();
        if (rowId > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteContact(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        long rowId = db.delete("contact","name = ?", new String[] { name });
        db.close();
        if (rowId > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteAllContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contact",null,null);
        db.close();
    }

    public boolean updateContact(String name, String pubkey) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("pubkey", pubkey);
        contentValues.put("name", name);
        SQLiteDatabase db = this.getWritableDatabase();
        long rowId = db.update("contact", contentValues, "name=?", new String[]{name});
        db.close();
        if (rowId > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateContact(String name, String pubkey, String id) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("pubkey", pubkey);
        contentValues.put("name", name);
        SQLiteDatabase db = this.getWritableDatabase();
        long rowId = db.update("contact", contentValues, "id=?", new String[]{id});
        db.close();
        if (rowId > 0) {
            return true;
        } else {
            return false;
        }
    }


    public List<Contact> getAllContact() {
        List<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from contact", null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setPubKey(cursor.getString(2));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

}

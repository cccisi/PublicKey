package iie.dcs.test;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    ListView listview;
    ListAdapter adapter;
    DataHelper db;
    FloatingActionButton fab;
    Contact contact;
    List<Contact> list;

    //更新sqlite
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    Cursor cursor_listactivity;
    int counter;

    //onCreate要更新
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);
        listview = (ListView) findViewById(R.id.listview);//R.id.listview在activity_pubkey_list
        fab = (FloatingActionButton) findViewById(R.id.fab);//浮动添加按钮
        db = new DataHelper(this);

        //更新SQLite数据库
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("正在更新通讯录...");
        pDialog.setCancelable(false);
        pDialog.show();

        updateBarHandler =new Handler();

        // 子线程更新
        new Thread(new Runnable() {

            @Override
            public void run() {
                queryContact();
            }

        }).start();

//
//        //更新list界面
//        list = new ArrayList<>();
//        list.addAll(db.getAllContact());//list从SQLite中读取
//        adapter = new ListAdapter(this, R.layout.list_item, list);//list的显示，R.layout.list_item是单个联系人
//        listview.setAdapter(adapter);
//
//        Log.d("List count", String.valueOf(list.size()));
//

        //点击添加联系人公钥
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                contact = adapter.getItem(i);

                Intent intent = new Intent(ContactListActivity.this, UpdateContactActivity.class);
                intent.putExtra("contact", contact);
                startActivity(intent);
            }
        });
    }



    public void queryContact() {
        list = new ArrayList<>();
        //清空联系人列表
        db.deleteAllContact();

        //筛选有备注的联系人，且开头04
        String noteString = null;
        String name = null;
        //引用路径
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String id = ContactsContract.Contacts._ID;

        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        cursor_listactivity = contentResolver.query(CONTENT_URI, null,null, null, null);

        Log.d("联系人总数量",String.valueOf(cursor_listactivity.getCount()));

        // Iterate every contact in the phone
        if (cursor_listactivity.getCount() > 0) {

            counter = 0;
            cursor_listactivity.moveToFirst();

            while (cursor_listactivity.moveToNext()) {
                output = new StringBuffer();

                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("读取进度:"+ counter++ +"/"+ cursor_listactivity.getCount());
                    }
                });

                id = cursor_listactivity.getString(cursor_listactivity.getColumnIndex( ContactsContract.Contacts._ID ));

                name = cursor_listactivity.getString(cursor_listactivity.getColumnIndex( DISPLAY_NAME ));


                if (name != null) {

                    ContentResolver noteResolver = getContentResolver();
                    Cursor phoneCursor = noteResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?", new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE}, null);
                    if(phoneCursor.moveToFirst()){

                        noteString = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        //04开头而且有数值
                        if (noteString != null && noteString.startsWith("04")) {
                            output.append("\n Note:" + name + noteString);
                            Log.e("输出" ,output.toString());
                            //更新SQL
                            db.insertContact(name, noteString);

                        }
                    }

                }

            }

            // ListView在UI线程更新
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //UI
                    //更新list界面

                    list.addAll(db.getAllContact());//list从SQLite中读取
                    adapter = new ListAdapter(ContactListActivity.this, R.layout.list_item, list);//list的显示，R.layout.list_item是单个联系人
                    listview.setAdapter(adapter);

                    Log.d("List count", String.valueOf(list.size()));


                }
            });


            // 0.5秒不响应
            updateBarHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }


    }



}

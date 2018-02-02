package iie.dcs.test;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {
    EditText name, contact;
    private DataHelper not;
    private Button add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        not = new DataHelper(this);
        add = (Button) findViewById(R.id.add);
        name = (EditText) findViewById(R.id.name);
        contact = (EditText) findViewById(R.id.pubkey);

        try {
        Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.ACTION_VIEW.equals(action)){

                String ab_path = intent.getDataString();
                String path = ab_path.substring(7);
                System.out.println("文件为：执行"  + path);

                File file = new File(path);//定义一个file对象，用来初始化FileReader
                FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
                BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
                StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
                String s = "";
                while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
                    System.out.println(s);
                }
                bReader.close();
                String str = sb.toString();
                contact.setText(sb.toString());
                System.out.println("文件为：" + sb.toString());


             }
        }catch (Exception e){
            //没有安装第三方的软件会提示
            Toast toast = Toast.makeText(AddContactActivity.this, "请复制粘贴公钥", Toast.LENGTH_SHORT);
            toast.show();
        }



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty()&& !contact.getText().toString().isEmpty()) {

                        //手机本地通讯录增加一条信息
                        if (not.insertContact(name.getText().toString(), contact.getText().toString())) {
                            updateContact(name.getText().toString(), contact.getText().toString());
                            Toast.makeText(getBaseContext(), "公钥已添加至" + name.getText().toString() + "通讯录备注", Toast.LENGTH_SHORT).show();
                            name.getText().clear();
                            contact.getText().clear();
                        } else {
                            Toast.makeText(getBaseContext(), "添加不成功", Toast.LENGTH_SHORT).show();
                        }


                } else {
                    //手机本地通讯录删除
                    //SQLite删除

                    name.setError("联系人不能为空");
                    contact.setError("新建公钥不能为空");
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddContactActivity.this, ContactListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void insertContact(String name,String pubkey){

        Cursor cursor;

        Uri ROW_CONTENT_URI = ContactsContract.RawContacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;
//        String id;
        int rawContactId = 0;

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ROW_CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, name)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, name)
                .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID,rawContactId)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID,rawContactId)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, pubkey)
                .build());

        //insert名字
        try {

            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
//        ContentResolver contentResolver = getContentResolver();
//        cursor_listactivity = contentResolver.query(CONTENT_URI, null,DISPLAY_NAME + "=? ", new String[]{name}, null);
//        cursor_listactivity.moveToFirst();
//        id = cursor_listactivity.getString(cursor_listactivity.getColumnIndex( ContactsContract.Contacts._ID ));


//        ArrayList<ContentProviderOperation> ops_note = new ArrayList<ContentProviderOperation>();
//        ops_note.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ?", new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, pubkey)
//                .build());
//
//
//        //insert
//        try {
//
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops_note);
//
//        } catch (RemoteException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (OperationApplicationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }

    public void deleteContact(String name){
        Cursor cursor;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String id;

        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null,DISPLAY_NAME + "=? ", new String[]{name}, null);
        cursor.moveToFirst();
        id = cursor.getString(cursor.getColumnIndex( ContactsContract.Contacts._ID ));

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ?", new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, null)
                .build());
        //delete
        try {

            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateContact(String name,String pubkey){
        //找到名字对应的ID
        Cursor cursor;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String id;

        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null,DISPLAY_NAME + "=? ", new String[]{name}, null);
        if(cursor.getCount() == 0)
        {
            insertContact(name,pubkey);

        }else {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ?", new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, pubkey)
                    .build());

            //update
            try {

                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }



}

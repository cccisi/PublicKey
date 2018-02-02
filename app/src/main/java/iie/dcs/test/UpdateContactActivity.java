package iie.dcs.test;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * 更改联系人的公钥
 */

public class UpdateContactActivity extends AppCompatActivity {
    private TextView header;
    private EditText name;
    private EditText pubkey;
    private Button updateContact;
    private DataHelper db;
    private Contact contact;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initView();

        pubkey.setHint("公钥清空即可删除公钥信息");
        updateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty()) {
                    if (!pubkey.getText().toString().isEmpty()) {
                        //手机本地通讯录增加一条信息
                        if (db.insertContact(name.getText().toString(), pubkey.getText().toString())) {
                            updateContact(name.getText().toString(),pubkey.getText().toString());
                            Toast.makeText(getBaseContext(), "公钥已添加至" + name.getText().toString() + "通讯录备注", Toast.LENGTH_SHORT).show();
                            name.getText().clear();
                            pubkey.getText().clear();
                        } else {
                            Toast.makeText(getBaseContext(), "添加不成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        if (db.deleteContact(name.getText().toString())) {
                            deleteContact(name.getText().toString());
                            Toast.makeText(getBaseContext(), name.getText().toString() + "公钥已删除", Toast.LENGTH_SHORT).show();
                            name.getText().clear();
                            pubkey.getText().clear();
                        } else {
                            Toast.makeText(getBaseContext(), "删除不成功", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                else {
                    name.setError("联系人不能为空");
                }
            }
        });
    }



    public void deleteContact(String name){
        Cursor cursor;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String id;

        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null,DISPLAY_NAME + "=? ", new String[]{name}, null);
        if(cursor.getCount() == 0)
        {
            pubkey.setError("该联系人公钥已经删除");

        }else {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

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
    }

    public void updateContact(String name,String pubkey_raw){
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
            pubkey.setError("请勿修改联系人姓名");

        }else {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ?", new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, pubkey_raw)
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


    private void initView() {
        header = (TextView) findViewById(R.id.contact);
        name = (EditText) findViewById(R.id.name);
        pubkey = (EditText) findViewById(R.id.pubkey);
        updateContact = (Button) findViewById(R.id.add);
        db = new DataHelper(this);
        Contact contact = new Contact();
        contact = getIntent().getParcelableExtra("contact");
        name.setText(contact.getName());
        pubkey.setText(contact.getPubKey());
        header.setText("变更公钥");

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(UpdateContactActivity.this, ContactListActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }
}

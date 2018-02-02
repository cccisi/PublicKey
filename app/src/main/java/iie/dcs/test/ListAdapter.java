package iie.dcs.test;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * List适配器
 */

public class ListAdapter extends ArrayAdapter<Contact> {

    Context context;
    int resource;
    List<Contact> contactList;

    public ListAdapter(@NonNull Context context, int resource, @NonNull List<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        contactList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContactHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new ContactHolder();
            holder.name = (TextView) row.findViewById(R.id.item_name);
            holder.pubkey = (TextView) row.findViewById(R.id.item_pubkey);

            row.setTag(holder);
        } else {
            holder = (ContactHolder) row.getTag();
        }

        Contact contact = getItem(position);
        holder.name.setText(contact.getName());
        holder.pubkey.setText(contact.getPubKey());

        return row;
    }

    static class ContactHolder {
        TextView name, pubkey;
    }
}

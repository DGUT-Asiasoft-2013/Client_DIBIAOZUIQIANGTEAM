package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Contact;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.util.JudgeLevel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ContactListAdapter extends BaseAdapter {

    Context context;
    List<Contact> mContact;

    public ContactListAdapter(Context context, List<Contact> mContact) {
        this.context = context;
        this.mContact = mContact;
    }

    @Override
    public int getCount() {
        return mContact == null ? 0 : mContact.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }



    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.list_contact_item, null);
        } else {
            view = convertView;
        }
        Contact contact = mContact.get(i);
        TextView nameText = (TextView) view.findViewById(R.id.tv_name);
        TextView phoneText = (TextView) view.findViewById(R.id.tv_phone);
        TextView schoolText = (TextView) view.findViewById(R.id.tv_school);
        TextView susheText = (TextView) view.findViewById(R.id.tv_sushe);

        nameText.setText(contact.getName());
        phoneText.setText(contact.getPhone());
        schoolText.setText(contact.getSchool());
        susheText.setText(contact.getSushe());




        return view;
    }


}

package com.dgut.collegemarket.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.orders.OrdresCreateActivity;
import com.dgut.collegemarket.adapter.ContactListAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Contact;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/*
* 收货地址
* */
public class ContactListActivity extends Activity {
    ListView listView;
    RelativeLayout AddContactRL;
    List<Contact> mContacts = new ArrayList<>();
    Page<Contact> contactPage;
    List selectItem = new ArrayList<>();
    ContactListAdapter adapter = new ContactListAdapter(this, mContacts, selectItem);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        listView = (ListView) findViewById(R.id.listView);
        AddContactRL = (RelativeLayout) findViewById(R.id.rl_contact_add);

        AddContactRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, ContactAddActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.none);
            }
        });


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectItem.add(0, i);
                adapter.notifyDataSetInvalidated();
                Contact contact=mContacts.get(i);
                Intent intent = new Intent();
                intent.putExtra("contact",contact);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.none, R.anim.slide_out_left);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContactListData();
    }

    private void loadContactListData() {
        //联系方式最多10个
        final Request request = Server.requestBuilderWithApi("contact/my/" + 0)
                .get()
                .build();


        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ContactListActivity.this, "网络连接失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                contactPage = mapper.readValue(result,
                        new TypeReference<Page<Contact>>() {
                        });
                final List<Contact> datas = contactPage.getContent();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mContacts.clear();
                        mContacts.addAll(datas);
                        adapter.notifyDataSetInvalidated();
                    }

                });

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.slide_out_left);
    }
}

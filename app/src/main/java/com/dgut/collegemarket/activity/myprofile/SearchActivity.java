package com.dgut.collegemarket.activity.myprofile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.CommonUtils;
import com.dgut.collegemarket.util.JudgeLevel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;


//查找界面—实现查找人功能
public class SearchActivity extends Activity {

    View view;
    ListView listView;
    List<User> data;
    TextView editText;
    ImageView imageView, imageView_turnBack;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imageView_turnBack = (ImageView) findViewById(R.id.imageView_turnBack);
        editText = (TextView) findViewById(R.id.edit_search);
        imageView = (ImageView) findViewById(R.id.image_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {
                    hideSoftInputView();
                    search();

                }
            }
        });

        imageView_turnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });


    }

    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(R.layout.list_search_item, null);
            } else {
                view = convertView;
            }

//            TextView contentContent = (TextView) view.findViewById(R.id.text_content);
//            TextView contentTitle = (TextView) view.findViewById(R.id.text_title);
            TextView contentName = (TextView) view.findViewById(R.id.name);
            TextView contentLv = (TextView) view.findViewById(R.id.level);
            TextView textDate = (TextView) view.findViewById(R.id.createtime);
            AvatarView avatar = (AvatarView) view.findViewById(R.id.image_avatar);
            User user = data.get(position);

            avatar.load(user);
            contentLv.setText("Lv:" + JudgeLevel.judege(user.getXp()));
            contentName.setText(user.getName().toString());
            String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", user.getCreateDate()).toString();
            textDate.setText(dateStr);

            return view;
        }
    };


    void search() {

        if (!editText.getText().toString().equals("")) {

            String keyword = editText.getText().toString();

            MultipartBody body = new MultipartBody.Builder()
                    .addFormDataPart("keyword", keyword)
                    .build();

            Request request = Server.requestBuilderWithApi("rec/search/" + keyword)
                    .post(body)
                    .build();

            Server.getSharedClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call arg0, Response arg1) throws IOException {
                    try {
                        final Page<User> data = new ObjectMapper()
                                .readValue(arg1.body().string(),
                                        new TypeReference<Page<User>>() {
                                        });
                        if (data.equals("")) {
                            Toast.makeText(SearchActivity.this, "查无此人", Toast.LENGTH_LONG).show();
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    SearchActivity.this.page = data.getNumber();
                                    SearchActivity.this.data = data.getContent();
                                    listAdapter.notifyDataSetInvalidated();
                                }
                            });
                        }

                    } catch (final Exception e) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(SearchActivity.this)
                                        .setMessage("查无此人"+e.getMessage())
                                        .show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call arg0, final IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(SearchActivity.this)
                                    .setMessage("服务器异常")
                                    .show();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(SearchActivity.this, "请输入昵称", Toast.LENGTH_LONG).show();
        }
    }

    void onItemClicked(int position) {

        User user = data.get(position);

        Intent itnt = new Intent(this, ContentSearchActivity.class);
        itnt.putExtra("user", user);
        startActivity(itnt);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}


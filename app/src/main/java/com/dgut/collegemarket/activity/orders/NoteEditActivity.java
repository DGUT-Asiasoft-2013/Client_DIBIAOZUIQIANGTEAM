package com.dgut.collegemarket.activity.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dgut.collegemarket.R;

public class NoteEditActivity extends Activity {
    ImageView  finishBtn;
    EditText contentEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        finishBtn= (ImageView) findViewById(R.id.btn_checkmark);
        contentEdit= (EditText) findViewById(R.id.et_content);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("content",contentEdit.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.none,R.anim.slide_out_left);
    }
}

package com.example.android.customchoicelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;


public class IntermediateActivity extends Activity {

    Button atten,pending;
    Intent intent;
    ArrayList<String> resp;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intermediate);
        atten=(Button)findViewById(R.id.button2);
        pending=(Button)findViewById(R.id.button3);
        intent=getIntent();
        resp=intent.getStringArrayListExtra("resp");
        userType=resp.get(resp.size() - 1);
        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IntermediateActivity.this, PendingWork.class);
                i.putExtra("userType", userType);
                startActivity(i);
            }
        });
        atten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IntermediateActivity.this, StudentListActivity.class);
                i.putExtra("resp", resp);
                startActivity(i);
            }
        });
    }

}

package com.mattcramblett.picsule;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    /**
     ******************************
     *  ACTIVITY LIFECYCLE LOGIC  *
     ******************************
     */

    @Override
    protected void onStart() {
        System.out.println("onStart method for MenuActivity being called");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart method for MenuActivity being called");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause method for MenuActivity being called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        System.out.println("onResume method for MenuActivity being called");
        super.onResume();
    }

    @Override
    protected void onStop() {
        System.out.println("onStop method for MenuActivity being called");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        System.out.println("onDestroy method for MenuActivity being called");
        super.onDestroy();
    }
}

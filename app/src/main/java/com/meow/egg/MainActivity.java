package com.meow.egg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.meow.egg.ui.EggTimerFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new EggTimerFragment())
                    .commitNow();
        }
    }
}

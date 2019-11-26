package com.example.notatnik;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static boolean isEncrypted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("com.example.notatnik", Activity.MODE_PRIVATE);

        if (preferences.getString("salt", "").equals("")) startActivity(new Intent(getApplicationContext(), SetPassActivity.class));
        else startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}

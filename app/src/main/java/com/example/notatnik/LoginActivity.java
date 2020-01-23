package com.example.notatnik;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Iterator;


public class LoginActivity extends AppCompatActivity {

    Button login_button;
    EditText pass_field;
    private Encryption encryption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_login);

        login_button = (Button) findViewById(R.id.login_button);
        pass_field = (EditText) findViewById(R.id.pass_field) ;

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("com.example.notatnik", Activity.MODE_PRIVATE);
                encryption = Encryption.getDefault(pass_field.getText().toString(), SetPassActivity.SALT , new byte[16]);
                String password = encryption.decryptOrNull(preferences.getString("password", ""));

                if(pass_field.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                else if(password!=null) {

                    HashSet<String> set = (HashSet<String>) preferences.getStringSet("notatki", null);

                    if(set != null) {
                        HashSet<String> setDecrypted = new HashSet<>();
                        Iterator<String> i = set.iterator();
                        while (i.hasNext()) {
                            setDecrypted.add(encryption.decryptOrNull(i.next()));
                        }
                        //////////////sprawdzenie
                        Iterator<String> j = setDecrypted.iterator();
                        while (j.hasNext()) {
                            System.out.println(j.next());
                        }
                        /////////////
                        preferences.edit().remove("notatki").apply();
                        preferences.edit().putStringSet("notatki", setDecrypted).apply();
                        Toast.makeText(getApplicationContext(), "Notes decrypted!", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                    startActivity(intent);
                }
                else Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(encryption!=null) {
            SharedPreferences preferences = getSharedPreferences("com.example.notatnik", Activity.MODE_PRIVATE);
            HashSet<String> set = (HashSet<String>) preferences.getStringSet("notatki", null);
            HashSet<String> setEncrypted = new HashSet<>();
            if(set != null) {
                Iterator<String> i = set.iterator();
                while (i.hasNext()) {
                    setEncrypted.add(encryption.encryptOrNull(i.next()));
                }
                //////////////sprawdzenie
                Iterator<String> j = setEncrypted.iterator();
                while (j.hasNext()) {
                    System.out.println(j.next());
                }
                /////////////
                preferences.edit().remove("notatki").apply();
                preferences.edit().putStringSet("notatki", setEncrypted).apply();
            }
            Toast.makeText(getApplicationContext(),"Encrypted notes!", Toast.LENGTH_SHORT).show();
        }
    }
}


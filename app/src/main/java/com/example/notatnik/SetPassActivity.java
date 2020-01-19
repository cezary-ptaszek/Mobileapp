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


public class SetPassActivity extends AppCompatActivity {

    EditText pass_field_1, pass_field_2;
    Button save_button;
    public static final String SALT = "J78yh8W";
    private Encryption encryption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pass);

        pass_field_1 = (EditText) findViewById(R.id.pass_first_field);
        pass_field_2 = (EditText) findViewById(R.id.pass_second_field);
        save_button = (Button) findViewById(R.id.save_button);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pass_field_1.getText().toString().equals("") || pass_field_2.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                else if(!pass_field_1.getText().toString().equals(pass_field_2.getText().toString())) Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                else {
                    String password = pass_field_1.getText().toString();

                    encryption = Encryption.getDefault(password, SALT, new byte[16]);
                    SharedPreferences preferences = getSharedPreferences("com.example.notatnik", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("salt");
                    editor.putString("salt", SALT);
                    editor.remove("password");
                    editor.putString("password", encryption.encryptOrNull(password));
                    editor.apply();

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
                        editor.remove("notatki").apply();
                        editor.putStringSet("notatki", setDecrypted).apply();
                        Toast.makeText(getApplicationContext(), "Notes decrypted!", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), "Password has been set", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(encryption!=null ) {
            SharedPreferences preferences = getSharedPreferences("com.example.notatnik", Activity.MODE_PRIVATE);
            HashSet<String> set = (HashSet<String>) preferences.getStringSet("notatki", null);
            if(set != null) {
                HashSet<String> setEncrypted = new HashSet<>();
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

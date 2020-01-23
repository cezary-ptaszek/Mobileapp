package com.example.notatnik;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.crypto.SecretKey;

public class NoteActivity extends AppCompatActivity {

    private Toolbar toolbarView;
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    private Encryption encryption = null;
    private String SALT = "vndsjf";
    private String KEY_NAME = "AndroidKey";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.add_note){
            Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
            startActivity(intent);
            return true;
        }
        if(item.getItemId() == R.id.change_pass){
            Intent intent = new Intent(getApplicationContext(), SetPassActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        toolbarView = (Toolbar) findViewById(R.id.toolbarView);
        setSupportActionBar(toolbarView);

        ListView listView = (ListView) findViewById(R.id.listView);


        //sprawdzenie zapisanych
        SharedPreferences preferences = getSharedPreferences("com.example.notatnik", Activity.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) preferences.getStringSet("notatki", null);

        if(FingerprintHandler.isFingerprint) {
            KeyStore keyStore = null;
            SecretKey key;
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            try {
                assert keyStore != null;
                key = (SecretKey) keyStore.getKey(KEY_NAME, null);
                encryption = Encryption.getDefault(key.toString(), SALT, new byte[16]);

                if (set != null) {
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
            } catch (UnrecoverableKeyException
                    | NoSuchAlgorithmException
                    | KeyStoreException e) {
                e.printStackTrace();
            }
        }

        //przywracanie listy z zapisanych
        if(set == null) {
            notes.add("Przykładowa notatka");
        }
        else {
            notes = new ArrayList<>(set);
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                intent.putExtra("noteId", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(NoteActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to delete the note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                notes.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(encryption!=null) {
            SharedPreferences preferences = getSharedPreferences("com.example.notatnik", Activity.MODE_PRIVATE);
            preferences.edit().remove("notatki").apply();
            HashSet<String> set = new HashSet<>(notes);
            /////////sprawdzenie
            Iterator<String> i = set.iterator();
            while (i.hasNext()) {
                System.out.println(i.next());
            }
            /////////////
            preferences.edit().putStringSet("notatki", set).apply();
            Toast.makeText(getApplicationContext(),"Encrypted notes!", Toast.LENGTH_SHORT).show();
        }
    }
}

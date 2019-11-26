package com.example.notatnik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.HashSet;

public class EditorActivity extends AppCompatActivity {

    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        EditText editText = (EditText) findViewById(R.id.editorText);

        Intent intent = getIntent();

        //sprawdzenie czy napewno ta notatka
        noteId = intent.getIntExtra("noteId", -1);

        if(noteId != -1){
            editText.setText(NoteActivity.notes.get(noteId));
        }
        else {
            NoteActivity.notes.add("");
            noteId = NoteActivity.notes.size() -1;
            NoteActivity.arrayAdapter.notifyDataSetChanged();
        }

        //TODO: anulowanie edycji notatki


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                NoteActivity.notes.set(noteId, String.valueOf(s));
                NoteActivity.arrayAdapter.notifyDataSetChanged();

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("com.example.notatnik", Context.MODE_PRIVATE);

                HashSet<String> set = new HashSet(NoteActivity.notes);
                preferences.edit().putStringSet("notatki", set).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

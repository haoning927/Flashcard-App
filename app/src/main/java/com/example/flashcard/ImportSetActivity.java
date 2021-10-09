package com.example.flashcard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ImportSetActivity extends AppCompatActivity {
    private int userId;
    ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_set);

//        userId = this.getIntent().getIntExtra("User Id", -1);
        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId", 0);

        String[] files = this.fileList();
        ArrayList<String> fileSets = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".set")) {
                fileSets.add(files[i]);
            }
        }

        ListView listView = findViewById(R.id.set_list_view);
        mAdapter = new ArrayAdapter<String>(ImportSetActivity.this, R.layout.set_item, R.id.text_view_set,
                fileSets);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.text_view_set);
                String importedSet = textView.getText().toString();
                int setId;
                try {
                    setId = importSet(importedSet);
                } catch (IOException e) {
                    Toast.makeText(ImportSetActivity.this, "IO exception", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(ImportSetActivity.this, "JSON error", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(ImportSetActivity.this, "Imported " + importedSet, Toast.LENGTH_LONG).show();
                ImportSetActivity.this.finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(ImportSetActivity.this)
                        .setMessage("Do you want to delete this file?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView textView = view.findViewById(R.id.text_view_set);
                                String fileName = textView.getText().toString();
                                File toDelete = new File(ImportSetActivity.this.getFilesDir(), fileName);
                                if (toDelete.delete()) {
                                    Toast.makeText(ImportSetActivity.this, "Deleted file", Toast.LENGTH_SHORT).show();
                                    ImportSetActivity.this.updateSetListView();
                                } else {
                                    Toast.makeText(ImportSetActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return true;
            }
        });
    }

    public void updateSetListView() {
        String[] files = ImportSetActivity.this.fileList();
        ArrayList<String> fileSets = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".set")) {
                fileSets.add(files[i]);
            }
        }
        if (mAdapter == null) {
            ListView listView = findViewById(R.id.set_list_view);
            mAdapter = new ArrayAdapter<String>(ImportSetActivity.this, R.layout.set_item, R.id.text_view_set,
                    fileSets);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(fileSets);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId", 0);
    }

    public int importSet(String fileName) throws IOException, JSONException {
        FileInputStream fis = this.openFileInput(fileName);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line).append('\n');
            line = reader.readLine();
        }
        String contents = stringBuilder.toString();

        Pair<String, ArrayList<Word>> pair = deserializeCardSet(contents);
        CardSet cardSet = new CardSet(-1, userId, pair.first);
        FlashCardDbHelper dbHelper = new FlashCardDbHelper(this);
        int setId = (int) dbHelper.addSet(cardSet);
        ArrayList<Word> words = pair.second;
        for (int i = 0; i < words.size(); i++) {
            Word word = new Word(setId, words.get(i).getWordTitle(), words.get(i).getWordDes());
            dbHelper.addWord(word);
        }

        return setId;
    }

    private Pair<String, ArrayList<Word>> deserializeCardSet(String s) throws JSONException {
        String setName;
        ArrayList<Word> words = new ArrayList<>();

        JSONObject setObject = new JSONObject(s);
        setName = setObject.getString("Set name");

        JSONArray jsonArray = setObject.getJSONArray("Words");
        for (int i = 0; i < jsonArray.length(); i++) {
            Word word = new Word();
            word.setWordTitle(jsonArray.getJSONObject(i).getString("Title"));
            word.setWordDes(jsonArray.getJSONObject(i).getString("Description"));
            words.add(word);
        }
        return new Pair<String, ArrayList<Word>>(setName, words);
    }
}
package com.example.flashcard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.SetInfoContract;
import com.example.flashcard.dbclass.Word;
import com.example.flashcard.dbclass.WordInfoContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ManageSetActivity extends AppCompatActivity {
    ArrayAdapter<CardSet> mAdapter;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_set);

        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);

        userId = sharedPreferences.getInt("UserId",0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Card set management");
        }

        FlashCardDbHelper dbHelper = new FlashCardDbHelper(ManageSetActivity.this);

        ArrayList<CardSet> cardSets = dbHelper.getSets(userId);

        Button addSetButton = findViewById(R.id.add_set_button);
        addSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageSetActivity.this, ManageWordActivity.class);
                intent.putExtra("User ID", userId);
                startActivity(intent);
            }
        });

        Button importButton = findViewById(R.id.button_import);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageSetActivity.this, ImportSetActivity.class);
                intent.putExtra("User Id", userId);
                startActivity(intent);
            }
        });
        ListView listView = findViewById(R.id.set_list_view);
        mAdapter = new ArrayAdapter<CardSet>(ManageSetActivity.this, R.layout.set_item, R.id.text_view_set,
                cardSets);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CardSet cardSet = (CardSet) parent.getItemAtPosition(position);
                int setId = cardSet.getSetId();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ManageSetActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(ManageSetActivity.this);
                View v = layoutInflater.inflate(R.layout.set_action_dialog, null);
                dialogBuilder.setTitle("Set Action");
                dialogBuilder.setView(v);
                final AlertDialog dialog = dialogBuilder.create();

                Button editSetButton = v.findViewById(R.id.edit_set_button);
                Button deleteSetButton = v.findViewById(R.id.delete_set_button);
                Button exportSetButton = v.findViewById(R.id.export_set_button);
                Button cancelSetButton = v.findViewById(R.id.cancel_set_button);

                editSetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ManageSetActivity.this, ManageWordActivity.class);
                        intent.putExtra("Set ID", setId);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                deleteSetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        new AlertDialog.Builder(ManageSetActivity.this)
                                .setMessage("Do you want to delete this set?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FlashCardDbHelper dbHelper = new FlashCardDbHelper(ManageSetActivity.this);

                                        dbHelper.removeWordsBySet(setId);
                                        dbHelper.removeSet(setId);
                                        Toast.makeText(ManageSetActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                        updateSetListView();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                });

                exportSetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileName = null;
                        try {
                            fileName = exportSet(setId);
                        } catch (JSONException e) {
//                            e.printStackTrace();
                            Toast.makeText(ManageSetActivity.this, "Json export error!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(ManageSetActivity.this, "IO exception!", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(ManageSetActivity.this, "Exported to " + fileName, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                cancelSetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId", 0);
        updateSetListView();
    }

    public void updateSetListView() {
        FlashCardDbHelper dbHelper = new FlashCardDbHelper(ManageSetActivity.this);
        ArrayList<CardSet> cardSets = dbHelper.getSets(userId);
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<CardSet>(ManageSetActivity.this, R.layout.set_item, R.id.text_view_set,
                    cardSets);
            ListView listView = findViewById(R.id.set_list_view);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(cardSets);
            mAdapter.notifyDataSetChanged();
        }
    }

    public String exportSet(int setId) throws JSONException, IOException {
        FlashCardDbHelper dbHelper = new FlashCardDbHelper(ManageSetActivity.this);
        ArrayList<Word> words = dbHelper.getWords(setId);
        String setName = dbHelper.getSet(setId).getSetName();

        String jsonString = serializeCardSet(setName, words);

        // write to file
        setName = setName.replaceAll(" ", "_");
        String fileName = setName + ".set";
        String[] files = this.fileList();
        boolean exist = false;
        for (int i = 0; i < files.length; i++) {
            if (files[i].equals(fileName)) {
                exist = true;
                break;
            }
        }
        int count = 0;
        while (exist) {
            count++;
            fileName = setName + "_" + count + ".set";
            exist = false;
            for (int i = 0; i < files.length; i++) {
                if (files[i].equals(fileName)) {
                    exist = true;
                    break;
                }
            }
        }

        File file = new File(this.getFilesDir(), fileName);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(fileName, Context.MODE_PRIVATE));
        outputStreamWriter.write(jsonString);
        outputStreamWriter.close();

        return fileName;
    }

    private String serializeCardSet(String setName, ArrayList<Word> words) throws JSONException {
        JSONObject setObject = new JSONObject();
        setObject.put("Set name", setName);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < words.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Title", words.get(i).getWordTitle());
            jsonObject.put("Description", words.get(i).getWordDes());
            jsonArray.put(jsonObject);
        }
        setObject.put("Words", jsonArray);
        return setObject.toString();
    }
}
package com.example.flashcard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.Word;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ManageWordActivity extends AppCompatActivity {

    private int setId;
    private int userId;
    public String setName;
    private ArrayAdapter<Word> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_word);
        FlashCardDbHelper dbHelper = new FlashCardDbHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId", 0);

        setId = getIntent().getIntExtra("Set ID", -1);
        if (setId == -1) {
            // new set
            setName = "new set";
            CardSet newSet = new CardSet(-1, userId, setName);
            setId = (int) dbHelper.addSet(newSet);
        } else {
            // existing set
            CardSet cardSet = dbHelper.getSet(setId);
            setName = cardSet.getSetName();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.edit_title_layout);
            View actionBarView = actionBar.getCustomView();
            TextView titleView = actionBarView.findViewById(R.id.edit_title_view);
            titleView.setText(setName);

            titleView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                        // When user clicks "enter" or "done", lose focus
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(titleView.getWindowToken(), 0);
                        v.clearFocus();
                        return true;
                    }
                    return false;
                }
            });

            titleView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // when lost focus, we should update the set's title
                        TextView thisView = (TextView) v;
                        if (!thisView.getText().toString().equals(ManageWordActivity.this.setName)) {
                            setName = thisView.getText().toString();
                            FlashCardDbHelper dbHelper = new FlashCardDbHelper(ManageWordActivity.this);
                            CardSet newSet = dbHelper.getSet(setId);
                            newSet.setSetName(setName);
                            dbHelper.updateSet(newSet);
                            Toast.makeText(ManageWordActivity.this, "Set name changed", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }

        ArrayList<Word> words = dbHelper.getWords(setId);
        Button addWord = findViewById(R.id.add_word_button);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageWordActivity.this, EditWordActivity.class);
                intent.putExtra("Set Id", setId);
                startActivity(intent);
            }
        });
        ListView listView = findViewById(R.id.list_view_word);
        mAdapter = new ArrayAdapter<Word>(ManageWordActivity.this, R.layout.word_item,
                R.id.text_view_word, words);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = (Word) parent.getItemAtPosition(position);
                int wordId = word.getWordId();
                String wordName = word.getWordTitle();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ManageWordActivity.this);

                LayoutInflater layoutInflater = LayoutInflater.from(ManageWordActivity.this);
                View v = layoutInflater.inflate(R.layout.word_action_dialog, null);
                dialogBuilder.setTitle("Word Action");
                dialogBuilder.setView(v);
                final AlertDialog dialog = dialogBuilder.create();

                Button editSetButton = v.findViewById(R.id.edit_word_button);
                Button deleteSetButton = v.findViewById(R.id.delete_word_button);
                Button cancelSetButton = v.findViewById(R.id.cancel_word_button);

                editSetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ManageWordActivity.this, EditWordActivity.class);
                        intent.putExtra("Word Id", wordId);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                deleteSetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        new AlertDialog.Builder(ManageWordActivity.this)
                                .setMessage("Do you want to delete this word?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FlashCardDbHelper dbHelper = new FlashCardDbHelper(ManageWordActivity.this);
                                        dbHelper.removeWord(wordId);
                                        Toast.makeText(ManageWordActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                        updateWordListView();
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
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    // When user clicks outside of an edit text view, lose its focus
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId", 0);
        updateWordListView();
    }

    public void updateWordListView() {
        FlashCardDbHelper dbHelper = new FlashCardDbHelper(ManageWordActivity.this);
        ArrayList<Word> words = dbHelper.getWords(setId);
        if (mAdapter == null) {
            ListView listView = findViewById(R.id.list_view_word);
            mAdapter = new ArrayAdapter<Word>(ManageWordActivity.this, R.layout.word_item,
                    R.id.text_view_word, words);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(words);
            mAdapter.notifyDataSetChanged();
        }
    }

}
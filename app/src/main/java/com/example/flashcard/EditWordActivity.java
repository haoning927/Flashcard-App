package com.example.flashcard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.Word;

public class EditWordActivity extends AppCompatActivity {
    private boolean isNewWord;
    private int wordId;
    private Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);
        TextView wordTitle = findViewById(R.id.edit_word_title);
        TextView wordDescription = findViewById(R.id.edit_word_description);
        Button wordDone = findViewById(R.id.edit_word_done_button);
        Button wordCancel = findViewById(R.id.edit_word_cancel_button);

        FlashCardDbHelper dbHelper = new FlashCardDbHelper(EditWordActivity.this);

        wordId = getIntent().getIntExtra("Word Id", -1);

        if (wordId == -1) {
            int setId = getIntent().getIntExtra("Set Id", -1);
            word = new Word(setId, "", "");
            isNewWord = true;
        } else {
            word = dbHelper.getWord(wordId);
            isNewWord = false;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit Word");
        }

        if (!isNewWord) {
            wordTitle.setText(word.getWordTitle());
            wordDescription.setText(word.getWordDes());
        }

        wordDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = wordTitle.getText().toString();
                String des = wordDescription.getText().toString();

                word.setWordTitle(title);
                word.setWordDes(des);

                FlashCardDbHelper dbHelper = new FlashCardDbHelper(EditWordActivity.this);
                if (isNewWord) {
                    dbHelper.addWord(word);
                } else {
                    dbHelper.updateWord(word);
                }

                EditWordActivity.this.finish();
            }
        });

        wordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditWordActivity.this.finish();
            }
        });


    }
}
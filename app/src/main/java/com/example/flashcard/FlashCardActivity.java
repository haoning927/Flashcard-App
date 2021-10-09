package com.example.flashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;

import com.example.flashcard.dbclass.CardSet;
import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.Word;
import com.example.flashcard.ui.main.FlashCardFragment;
import com.example.flashcard.ui.main.FlashCardViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class FlashCardActivity extends AppCompatActivity {

    private String TAG = "FlashCardActivity";
    public int end = 0;
    private int userId;
    private int setId;
    private Queue<Word> wordQ;
    private ArrayList<Word> init_words;
    private ArrayList<Word> words;
    private Map<Integer, Set<Word>> resultMap;
    public final int TYPE_REMEMBER = 0;
    public final int TYPE_PRACTICE = 1;
    public final int TYPE_FORGOT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flash_card_activity);
        initSet();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FlashCardFragment.newInstance(""))
                    .commitNow();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(end == 1){
            super.onBackPressed();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(FlashCardActivity.this);
        builder.setTitle("Warning");
        builder.setMessage("You are going back to the main page. Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FlashCardActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("FlashCardActivity","Click No");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void initSet(){

        SharedPreferences sharedPreferences = getSharedPreferences("SaveUserId",
                Activity.MODE_PRIVATE);
        userId = sharedPreferences.getInt("UserId",0);
        setId = getIntent().getIntExtra("Set Id", -1);
        Log.d(TAG, userId + " " + setId);

        FlashCardDbHelper dbHelper = new FlashCardDbHelper(FlashCardActivity.this);

        init_words = dbHelper.getWords(setId);
        words = (ArrayList) init_words.clone();
        Collections.shuffle(words, new Random(new Random().nextInt(50)));
        wordQ = new LinkedList<>(words);
        resultMap = new HashMap<Integer, Set<Word>>();
        resultMap.put(0, new HashSet<Word>());
        resultMap.put(1, new HashSet<Word>());
        resultMap.put(2, new HashSet<Word>());
    }

    public void updateResult(Word word, int type){
        /*
        * Type value:
        *   0: remember
        *   1: need practice
        *   2: forgot
        *
        */
        if(resultMap.get(1).contains(word) && type == 0)
            return;
        for(int i = 0; i < 3; i++){
            if(i == type){
                resultMap.get(i).add(word);
            }
            else{
                resultMap.get(i).remove(word);
            }
        }

    }

    public int getDefault(){
        final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
        int user_id = sp.getInt("current_user", -1);
        final String default_method_key = "default_method"+Integer.toString(user_id);
        return sp.getInt(default_method_key, -1);
    }

    public Map<Integer, Set<Word>> getResultMap() {
        return resultMap;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public Queue<Word> getWordQ() {
        return wordQ;
    }

    public Word pollWord() {
        return wordQ.poll();
    }

    public boolean offerWord(Word word){
        return wordQ.offer(word);
    }

    public Set<Word> getChoices(Word word){
        Set<Word> set = new HashSet<>();
        while(set.size()<3){
            Word select = words.get(new Random().nextInt(words.size()));
            if(select.getWordTitle() != word.getWordTitle()){
                set.add(select);
            }
        }
        return set;
    }

    public void addGPS(){
        final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        if(sp.getBoolean("record_GPS_status",false)){
            FlashCardDbHelper dbHelper = new FlashCardDbHelper(FlashCardActivity.this);
            dbHelper.addGPS(this, userId, setId);
        };
    }
    public void finishCardActivity(){
        this.finish();
    }
    public void setEnd(int end){
        this.end = end;
    }
}
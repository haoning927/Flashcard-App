package com.example.flashcard.ui.main;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.FlashCardActivity;
import com.example.flashcard.R;
import com.example.flashcard.dbclass.Word;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChoiceFragment extends Fragment {


    private Word word;
    private String mParam1;
    private String mParam2;

    private TextView card;
    private RadioGroup choice_answer;
    private RadioButton radio1, radio2, radio3, radio4, answer, checked;
    private Button submit, next;
    private ImageView correct, wrong;
    private RadioButton[] allRadio;

    private FlashCardActivity mActivity;

    private Map<Word, Integer> map = new HashMap<>();
    private String answer_str = "test";

    public ChoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChoiceFragment newInstance(String param1, String param2) {
        ChoiceFragment fragment = new ChoiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = (FlashCardActivity)getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choice, container, false);
        card = view.findViewById(R.id.choice_card);
        choice_answer = view.findViewById(R.id.choice_radio);
        submit = view.findViewById(R.id.choice_submit);
        next = view.findViewById(R.id.choice_next);
        correct = view.findViewById(R.id.check_icon_choice);
        wrong = view.findViewById(R.id.x_icon_choice);

        radio1 = view.findViewById(R.id.radio1);
        radio2 = view.findViewById(R.id.radio2);
        radio3 = view.findViewById(R.id.radio3);
        radio4 = view.findViewById(R.id.radio4);

        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupClick(radio1);
            }
        });
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupClick(radio2);
            }
        });
        radio3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupClick(radio3);
            }
        });
        radio4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupClick(radio4);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radio1.isChecked() || radio2.isChecked() || radio3.isChecked() || radio4.isChecked()){
                    if(answer.isChecked()){ // correct
                        correct.setVisibility(View.VISIBLE);
                        mActivity.updateResult(word, mActivity.TYPE_REMEMBER);
                    }
                    else{ // wrong
                        wrong.setVisibility(View.VISIBLE);
                        checked.setTextColor(Color.RED);
                        if(map.getOrDefault(word, 0) < 2){
                            mActivity.updateResult(word, mActivity.TYPE_PRACTICE);
                        }
                        else{
                            mActivity.updateResult(word, mActivity.TYPE_FORGOT);
                        }
                        if(map.getOrDefault(word,0) < 3){
                            mActivity.offerWord(word);
                            map.put(word, map.getOrDefault(word, 0)+1);
                        }
                    }
                    answer.setTextColor(Color.GREEN);
                }
                else{
                    Toast.makeText(getActivity(), "Please select a answer to continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                submit.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);

                radio1.setEnabled(false);
                radio2.setEnabled(false);
                radio3.setEnabled(false);
                radio4.setEnabled(false);
            }
        });

        correct.setVisibility(View.INVISIBLE);
        wrong.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.addGPS();
                if(mActivity.getWordQ().isEmpty()){
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, ResultFragment.newInstance("","")).commitNow();
                }
                else{
                    updateData();
                    first();
                }
            }
        });
        allRadio = new RadioButton[4];
        allRadio[0] = radio1;
        allRadio[1] = radio2;
        allRadio[2] = radio3;
        allRadio[3] = radio4;

        updateData();
        first();
        return view;
    }



    public void first(){

        correct.setVisibility(View.INVISIBLE);
        wrong.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.VISIBLE);
        for(int i = 0; i < allRadio.length; i++){
            allRadio[i].setEnabled(true);
            allRadio[i].setTextColor(Color.BLACK);
            allRadio[i].setChecked(false);
        }

    }

    public void updateData(){
        word = mActivity.pollWord();
        card.setText(word.getWordDes());
        Set<Word> set = mActivity.getChoices(word);
        int correctPosition = new Random().nextInt(4);
        Iterator<Word> it = set.iterator();
        for(int i = 0; i < 4; i++){
            if(i == correctPosition){
                allRadio[i].setText(word.getWordTitle());
            }
            else{
                if(it.hasNext()){
                    allRadio[i].setText(it.next().getWordTitle());
                }
            }
        }
        answer = allRadio[correctPosition];
    }


    public void radioGroupClick(RadioButton btn){
        radio1.setChecked(btn == radio1);
        radio2.setChecked(btn == radio2);
        radio3.setChecked(btn == radio3);
        radio4.setChecked(btn == radio4);
        if(btn == radio1){
            checked = radio1;
        }
        if(btn == radio2){
            checked = radio2;
        }
        if(btn == radio3){
            checked = radio3;
        }
        if(btn == radio4){
            checked = radio4;
        }
    }
}
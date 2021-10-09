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
import android.widget.TextView;

import com.example.flashcard.FlashCardActivity;
import com.example.flashcard.R;
import com.example.flashcard.dbclass.Word;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TypingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TypingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Word word;
    private TextView card, answer, answer_label;
    private EditText typing_answer;
    private Button submit, next;
    private ImageView correct, wrong;

    private FlashCardActivity mActivity;
    private Map<Word, Integer> map = new HashMap<>();
    private String answer_str;

    public TypingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment typingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TypingFragment newInstance(String param1, String param2) {
        TypingFragment fragment = new TypingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FlashCardActivity)getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_typing, container, false);
        card = view.findViewById(R.id.typing_card);
        typing_answer = view.findViewById(R.id.typing_answer);
        submit = view.findViewById(R.id.typing_submit);
        next = view.findViewById(R.id.typing_next);
        answer_label = view.findViewById(R.id.typing_answer_label);
        answer = view.findViewById(R.id.answer);
        correct = view.findViewById(R.id.check_icon_typing);
        wrong = view.findViewById(R.id.x_icon_typing);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setVisibility(View.INVISIBLE);
                typing_answer.setEnabled(false);
                typing_answer.clearFocus();
                if(typing_answer.getText().toString().equals(answer_str)){ // correct
                    typing_answer.setTextColor(Color.GREEN);
                    correct.setVisibility(View.VISIBLE);
                    mActivity.updateResult(word, mActivity.TYPE_REMEMBER);
                }
                else{// wrong
                    typing_answer.setTextColor(Color.RED);
                    wrong.setVisibility(View.VISIBLE);
                    answer_label.setVisibility(View.VISIBLE);
                    answer.setVisibility(View.VISIBLE);

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
                next.setVisibility(View.VISIBLE);
            }
        });

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
        first();
        updateData();
        return view;
    }

    public void first(){
        typing_answer.setEnabled(true);
        typing_answer.setText("");
        submit.setVisibility(View.VISIBLE);
        typing_answer.setTextColor(Color.BLACK);
        answer_label.setVisibility(View.INVISIBLE);
        answer.setVisibility(View.INVISIBLE);
        correct.setVisibility(View.INVISIBLE);
        wrong.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
    }

    public void updateData(){
        word = mActivity.pollWord();
        card.setText(word.getWordDes());
        answer_str = word.getWordTitle();
        answer.setText(answer_str);
    }
}
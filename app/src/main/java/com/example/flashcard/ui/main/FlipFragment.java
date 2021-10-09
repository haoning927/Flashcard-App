package com.example.flashcard.ui.main;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.FlashCardActivity;
import com.example.flashcard.R;
import com.example.flashcard.dbclass.Word;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FlipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlipFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Word word;
    private TextView card;
    private RadioGroup flip_group;
    private RadioButton remember, forgot, not_sure;
    private Button flip,flip_back, next;

    private FlashCardActivity mActivity;
    private Map<Word, Integer> map = new HashMap<>();
    private String answer_str = "test";

    public FlipFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FlipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FlipFragment newInstance(String param1, String param2) {
        FlipFragment fragment = new FlipFragment();
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

        View view = inflater.inflate(R.layout.fragment_flip, container, false);
        card = view.findViewById(R.id.flip_card);
        flip_group = view.findViewById(R.id.flip_radioGroup);
        flip = view.findViewById(R.id.flip_button);
        flip_back = view.findViewById(R.id.flip_back);
        next = view.findViewById(R.id.flip_next);

        remember = view.findViewById(R.id.flip_remember);
        forgot = view.findViewById(R.id.flip_forgot);
        not_sure = view.findViewById(R.id.flip_notsure);


        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setText(word.getWordTitle());
                flip.setVisibility(View.INVISIBLE);
                flip_back.setVisibility(View.VISIBLE);
                flip_group.setVisibility(View.VISIBLE);
                if(next.getVisibility() == View.INVISIBLE)
                    next.setVisibility(View.VISIBLE);
            }
        });
        flip_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setText(word.getWordDes());
                flip_back.setVisibility(View.INVISIBLE);
                flip.setVisibility(View.VISIBLE);
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flip.getVisibility() == View.VISIBLE){
                    flip.performClick();
                }
                else{
                    flip_back.performClick();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked_id = flip_group.getCheckedRadioButtonId();
                if(checked_id == -1){
                    Toast.makeText(getActivity(), "Please select an option to continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(checked_id == R.id.flip_remember){
                    mActivity.updateResult(word, mActivity.TYPE_REMEMBER);
                }
                else{
                    if(checked_id == R.id.flip_notsure){
                        mActivity.updateResult(word, mActivity.TYPE_PRACTICE);
                    }
                    else{
                        if(map.getOrDefault(word, 0) < 2){
                            mActivity.updateResult(word, mActivity.TYPE_PRACTICE);
                        }
                        else{
                            mActivity.updateResult(word, mActivity.TYPE_FORGOT);
                        }
                    }

                    if(map.getOrDefault(word,0) < 3){
                        mActivity.offerWord(word);
                        map.put(word, map.getOrDefault(word, 0)+1);
                    }
                }
                remember.setChecked(false);
                not_sure.setChecked(false);
                forgot.setChecked(false);

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

        updateData();
        first();
        return view;
    }


    public void first(){

        flip_back.setVisibility(View.INVISIBLE);
        flip.setVisibility(View.VISIBLE);
        flip_back.setVisibility(View.INVISIBLE);
        flip_group.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
    }

    public void updateData(){
        word = mActivity.pollWord();
        card.setText(word.getWordDes());
    }
}
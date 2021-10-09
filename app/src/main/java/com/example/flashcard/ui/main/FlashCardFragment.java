package com.example.flashcard.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.flashcard.FlashCardActivity;
import com.example.flashcard.R;

public class FlashCardFragment extends Fragment {

    private FlashCardViewModel mViewModel;
    private String mParam;
    private FlashCardActivity mActivity;
    private Button submit;
    private RadioGroup radioGroup;
    private RadioButton typing, choice, flipping;
    private int selected;

    @Override
    public void onAttach(@NonNull Context context) {
        mActivity = (FlashCardActivity) context;
        mParam = getArguments().getString("set");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static FlashCardFragment newInstance(String str) {
        Bundle args = new Bundle();
        args.putString("set", str);
        FlashCardFragment fragment = new FlashCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flashcard_fragment, container, false);
        submit = (Button) view.findViewById(R.id.select_submit);
        radioGroup = (RadioGroup) view.findViewById(R.id.select);
        typing = (RadioButton) view.findViewById(R.id.typing);
        choice = (RadioButton) view.findViewById(R.id.single_choice);
        flipping = (RadioButton) view.findViewById(R.id.flipping);

        //TODO: preset option by user setting

        int def_method = mActivity.getDefault();
        if(def_method == 0){
            typing.setChecked(true);
        }
        else if(def_method == 1){
            choice.setChecked(true);
        }
        else if(def_method == 2){
            flipping.setChecked(true);
        }

        // disable choice option if word count in the set is less than 4;
        if(((FlashCardActivity)getActivity()).getWords().size()>3){
            choice.setEnabled(true);
        }
        else{
            choice.setEnabled(false);
            choice.setChecked(false);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == -1) {
                    // No item selected
                    Toast.makeText(mActivity, "Please select a method to continue", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(checkedRadioButtonId == typing.getId()){
                        selected = 0;
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, TypingFragment.newInstance("","")).commitNow();
                    }

                    else if(checkedRadioButtonId == choice.getId()){
                        selected = 1;
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, ChoiceFragment.newInstance("","")).commitNow();
                    }
                    else if(checkedRadioButtonId == flipping.getId()){
                        selected = 2;
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, FlipFragment.newInstance("","")).commitNow();
                    }
                }
            }
        });
        
        
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FlashCardViewModel.class);
        // TODO: Use the ViewModel
    }


}
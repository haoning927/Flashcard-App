package com.example.flashcard.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.flashcard.FlashCardActivity;
import com.example.flashcard.R;
import com.example.flashcard.dbclass.Word;

import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button finish;
    private TextView rememberCount, practiceCount, forgotCount;
    private FlashCardActivity mActivity;


    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        finish = view.findViewById(R.id.finish_button);
        rememberCount = view.findViewById(R.id.remember_count);
        practiceCount = view.findViewById(R.id.practice_count);
        forgotCount = view.findViewById(R.id.forgot_count);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        ((FlashCardActivity)getActivity()).setEnd(1);
        mActivity = (FlashCardActivity) getActivity();
        initData();
        return view;
    }

    public void initData(){
        Map<Integer, Set<Word>> resultMap = mActivity.getResultMap();
        rememberCount.setText(resultMap.get(0).size() + "");
        practiceCount.setText(resultMap.get(1).size() + "");
        forgotCount.setText(resultMap.get(2).size() + "");

    }


}
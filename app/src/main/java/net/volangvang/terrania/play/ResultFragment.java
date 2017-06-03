package net.volangvang.terrania.play;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jinatonic.confetti.CommonConfetti;

import net.volangvang.terrania.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SCORE = "param1";

    @BindView(R.id.score)
    TextView scoreText;
    @BindView(R.id.btn_ok)
    Button btnOK;
    @BindView(R.id.container)
    LinearLayout container;

    private int score;
    private Activity activity;


    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must be an Activity");
        }
    }

    public static ResultFragment newInstance(int score) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt(SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = getArguments().getInt(SCORE);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, view);
        scoreText.setText(Integer.toString(score));
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        CommonConfetti.rainingConfetti(container, new int[] {Color.GREEN, Color.RED, Color.YELLOW}).infinite();
        return view;
    }


}

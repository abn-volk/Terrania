package net.volangvang.terrania.play;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.volangvang.terrania.R;
import net.volangvang.terrania.play.data.Question;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


public class Flag2CountryFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String QUESTION = "question";
    private static final String CHOICE1 = "choice1";
    private static final String CHOICE2 = "choice2";
    private static final String CHOICE3 = "choice3";
    private static final String CHOICE4 = "choice4";
    private static final String LOCAL = "local";

    private String question;
    List<String> choices;
    private boolean local;

    @BindViews({R.id.answer_1, R.id.answer_2, R.id.answer_3, R.id.answer_4})
    List<CardView> answers;
    @BindViews({R.id.answer_1_txt, R.id.answer_2_txt, R.id.answer_3_txt, R.id.answer_4_txt})
    List<TextView> answerTxts;
    @BindView(R.id.question_flag)
    ImageView flag;
    @BindView(R.id.btn_next)
    FloatingActionButton btnNext;

    private GameActivity activity;

    public Flag2CountryFragment() {
        // Required empty public constructor
    }

    public static Flag2CountryFragment newInstance(String question, String choice1, String choice2, String choice3, String choice4, boolean local) {
        Flag2CountryFragment fragment = new Flag2CountryFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        args.putString(CHOICE1, choice1);
        args.putString(CHOICE2, choice2);
        args.putString(CHOICE3, choice3);
        args.putString(CHOICE4, choice4);
        args.putBoolean(LOCAL, local);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString(QUESTION);
            String choice1 = getArguments().getString(CHOICE1);
            String choice2 = getArguments().getString(CHOICE2);
            String choice3 = getArguments().getString(CHOICE3);
            String choice4 = getArguments().getString(CHOICE4);
            choices = new ArrayList<>(4);
            choices.add(choice1);
            choices.add(choice2);
            choices.add(choice3);
            choices.add(choice4);
            local = getArguments().getBoolean(LOCAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flag2_country, container, false);
        ButterKnife.bind(this, view);

        if (local) {
            Picasso.with(getContext())
                    .load(getResources().getIdentifier("country_" + question.toLowerCase(), "drawable", getContext().getPackageName()))
                    .into(flag);
        }
        else {
            byte[] decodedString = Base64.decode(question, Base64.DEFAULT);
            Bitmap decodedBytes = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            flag.setImageBitmap(decodedBytes);
        }

        for (int i=0; i<4; i++) {
            answerTxts.get(i).setText(choices.get(i));
            final int finalI = i;
            answers.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (CardView cv : answers) {
                        cv.setClickable(false);
                    }
                    final ColorStateList originalColor = answers.get(finalI).getCardBackgroundColor();
                    answers.get(finalI).setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorSelected));
                    activity.answer(finalI).subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NonNull Integer rightAnswer) {
                            if (rightAnswer != finalI)
                                answers.get(finalI).setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWrong));
                                answers.get(rightAnswer).setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRight));
                                btnNext.setVisibility(View.VISIBLE);
                                btnNext.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btnNext.setEnabled(false);
                                        activity.nextQuestion().subscribe(new SingleObserver<Question>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(@NonNull Question question) {
                                                if (question.getQuestion() == null)
                                                    btnNext.setEnabled(true);
                                                activity.displayQuestion(question);
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {

                                            }
                                        });
                                }
                            });
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Toast.makeText(getContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                            for (CardView cv : answers) {
                                cv.setClickable(true);
                            }
                            answers.get(finalI).setCardBackgroundColor(originalColor);
                        }
                    });
                }
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameActivity) {
            activity = (GameActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must be GameActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

}

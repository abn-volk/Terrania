package net.volangvang.terrania.play;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.volangvang.terrania.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameModeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameModeFragment extends Fragment {
    private static final String ARG_MODE = "param1";
    private static final String ARG_VALUE = "param2";
    private static final String ARG_DESCRIPTION = "desc";

    private String gameMode;
    private String gameModeValue;
    private String gameModeDescription;
    @BindView(R.id.game_mode_label) TextView label;
    @BindView(R.id.game_mode_desc) TextView description;
    @BindView(R.id.game_mode_img) ImageView img;
    @BindView(R.id.spinner_continent) Spinner continentSpinner;
    @BindView(R.id.spinner_number) Spinner numberSpinner;
    @BindView(R.id.btn_go) Button btnGo;


    public GameModeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameModeFragment.
     */
    public static GameModeFragment newInstance(String param1, String param2, String desc) {
        GameModeFragment fragment = new GameModeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, param1);
        args.putString(ARG_VALUE, param2);
        args.putString(ARG_DESCRIPTION, desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameMode = getArguments().getString(ARG_MODE);
            gameModeValue = getArguments().getString(ARG_VALUE);
            gameModeDescription = getArguments().getString(ARG_DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_game_mode, container, false);
        ButterKnife.bind(this, fragmentView);
        label.setText(gameMode);
        description.setText(gameModeDescription);
        continentSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.continents)));
        continentSpinner.setSelection(0);
        numberSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.number_of_questions)));
        numberSpinner.setSelection(0);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int continentId = continentSpinner.getSelectedItemPosition();
                String[] continents = new String[] {"Africa", "America", "Asia", "Europe", "Oceania", "World"};
                int[] maxCounts = new int[] {54, 35, 48, 44, 14, 195};
                int count;
                String selectedContinent = continents[continentId];
                String selectedNumber = (String) numberSpinner.getSelectedItem();
                if (selectedNumber.equals("All"))
                    count = maxCounts[continentId];
                else count = Integer.parseInt(selectedNumber);
                Intent intent = new Intent(getContext(), GameActivity.class);
                intent.putExtra(GameActivity.EXTRA_COUNT, count);
                intent.putExtra(GameActivity.EXTRA_MODE, gameModeValue);
                intent.putExtra(GameActivity.EXTRA_CONTINENT, selectedContinent);
                startActivity(intent);
            }
        });
        if (getResources().getConfiguration().screenHeightDp >= 480) {
            switch (gameModeValue) {
                case "country2flag":
                    Picasso.with(getContext()).load(R.drawable.c2f).into(img);
                    break;
                case "flag2country":
                    Picasso.with(getContext()).load(R.drawable.f2c).into(img);
                    break;
                case "country2capital":
                    Picasso.with(getContext()).load(R.drawable.ct2cp).into(img);
                    break;
                case "capital2country":
                    Picasso.with(getContext()).load(R.drawable.cp2ct).into(img);
                    break;
            }
        }
        return fragmentView;
    }

}

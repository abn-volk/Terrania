package net.volangvang.terrania;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PlayFragment extends Fragment {
    @BindView(R.id.spinner_mode) Spinner modeSpinner;
    @BindView(R.id.radio_group_continent) RadioGroup continentGroup;
    @BindView(R.id.radio_group_number) RadioGroup numberGroup;
    @BindView(R.id.radio_oceania) RadioButton radioOceania;
    @BindView(R.id.radio_20) RadioButton radio20;
    @BindView(R.id.radio_30) RadioButton radio30;
    private boolean firstTimeHint = true;
    public PlayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.nav_play);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.game_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);
        modeSpinner.setSelection(0);
        for (RadioButton btn : new RadioButton[] {radio20, radio30}) {
            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (firstTimeHint && b && continentGroup.getCheckedRadioButtonId() == R.id.radio_oceania) {
                        firstTimeHint = false;
                        Toast.makeText(getContext(), R.string.msg_oceania_countries, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        radioOceania.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (firstTimeHint && b && (radio20.isChecked() || radio30.isChecked()))
                    Toast.makeText(getContext(), R.string.msg_oceania_countries, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_go)
    public void onGoButtonPressed() {
        int continentId = continentGroup.getCheckedRadioButtonId();
        int gameMode = modeSpinner.getSelectedItemPosition();
        int number = numberGroup.getCheckedRadioButtonId();
    }

    @OnClick(R.id.label_game_mode)
    public void onLabelGameModePressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_game_modes)
                .setMessage(R.string.msg_game_modes)
                .setPositiveButton(R.string.ok, null);
        builder.create().show();

    }
}

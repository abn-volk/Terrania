package com.volangvang.terrania;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    HomeFragmentInteraction activity;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (! (context instanceof HomeFragmentInteraction))
            throw new UnsupportedOperationException();
        else activity = (HomeFragmentInteraction) context;
    }

    interface HomeFragmentInteraction {
        void onPlayPressed();
    }

    @OnClick(R.id.btn_play_now)
    public void onPlayClick() {
        activity.onPlayPressed();
    }
}

package net.volangvang.terrania;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends DialogFragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.fragment_about)
                .setTitle(R.string.nav_about)
                .setPositiveButton(R.string.ok, null);
        return builder.create();
    }

    @Override
    public void onViewCreated(View dialog, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(dialog, savedInstanceState);
        ((TextView) dialog.findViewById(R.id.body_acknowledgements)).setMovementMethod(new LinkMovementMethod());
        ((TextView) dialog.findViewById(R.id.body_team)).setMovementMethod(new LinkMovementMethod());
    }
}

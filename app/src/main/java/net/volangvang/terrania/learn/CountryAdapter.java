package net.volangvang.terrania.learn;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.volangvang.terrania.R;
import net.volangvang.terrania.data.CountryContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private Cursor cursor;
    public Activity activity;

    public CountryAdapter(Activity activity) {
        this.activity = activity;
    }

    public void swapCursor (Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.moveToPosition(position);
            final int id = cursor.getInt(cursor.getColumnIndex(CountryContract.CountryEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_NAME));
            String code = cursor.getString(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_COUNTRY_CODE));
            holder.countryName.setText(name);
            int imgId = activity.getResources().getIdentifier("country_" + code.toLowerCase(), "drawable", activity.getPackageName());
            Picasso.with(activity).load(imgId).into(holder.countryFlag);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, CountryActivity.class);
                    intent.putExtra("id", id);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.countryFlag.setTransitionName("image_transition");
                        Pair<View, String> pair = Pair.create((View) holder.countryFlag, "image_transition");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair);
                        activity.startActivity(intent, options.toBundle());
                    }
                    else {
                        activity.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.country_name)
        TextView countryName;
        @BindView(R.id.country_flag)
        ImageView countryFlag;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

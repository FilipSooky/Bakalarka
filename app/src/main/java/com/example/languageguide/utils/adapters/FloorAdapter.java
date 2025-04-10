package com.example.languageguide.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.languageguide.R;
import com.example.languageguide.utils.locations.Floor;

import java.util.List;

public class FloorAdapter extends BaseAdapter {
    private Context context;
    private List<Floor> floors;

    public FloorAdapter(Context context, List<Floor> floors) {
        this.context = context;
        this.floors = floors;
    }

    @Override
    public int getCount() {
        return floors.size();
    }

    @Override
    public Object getItem(int position) {
        return floors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_floor, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textViewFloorName);
        ImageView imageView = convertView.findViewById(R.id.imageViewFloor);

        Floor floor = floors.get(position);
        textView.setText(floor.getName(context));

        // Convert imageResource name (string) to actual drawable
        int imageResId = context.getResources().getIdentifier(floor.getImageResource(), "drawable", context.getPackageName());
        imageView.setImageResource(imageResId);

        return convertView;
    }
}

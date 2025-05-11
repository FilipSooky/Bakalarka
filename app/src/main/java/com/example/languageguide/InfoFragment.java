package com.example.languageguide;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.languageguide.utils.DBHelper;
import com.example.languageguide.utils.Utils;
import com.example.languageguide.utils.locations.InfoItem;

import java.util.List;

public class InfoFragment extends Fragment {
    private DBHelper dbHelper;
    private LinearLayout infoContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        infoContainer = view.findViewById(R.id.info_container);

        dbHelper = new DBHelper(requireContext());
        dbHelper.refreshInfoFromServer();
        loadInfoFromServer();

        return view;
    }

    private void loadInfoFromServer() {
        new Thread(() -> {
            List<InfoItem> infoItems = dbHelper.getAllInfoItems();

            requireActivity().runOnUiThread(() -> {
                for (InfoItem item : infoItems) {
                    TextView tv = new TextView(getContext());
                    tv.setText(formatKey(Utils.getTranslatedString(getContext(),item.getKey())));
                    tv.setTextSize(item.getFontSize());
                    tv.setPadding(8, 8, 8, 8);

                    if ("bold".equalsIgnoreCase(item.getFontStyle())) {
                        tv.setTypeface(null, Typeface.BOLD);
                    } else if ("italic".equalsIgnoreCase(item.getFontStyle())) {
                        tv.setTypeface(null, Typeface.ITALIC);
                    } else {
                        tv.setTypeface(null, Typeface.NORMAL);
                    }

                    infoContainer.addView(tv);
                }
            });
        }).start();
    }

    private String formatKey(String rawKey) {
        String[] parts = rawKey.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            formatted.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }
        return formatted.toString().trim();
    }

}
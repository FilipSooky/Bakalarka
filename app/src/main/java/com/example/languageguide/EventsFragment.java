package com.example.languageguide;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.languageguide.utils.DBHelper;
import com.example.languageguide.utils.Event;
import com.example.languageguide.utils.EventDBManager;
import com.example.languageguide.utils.adapters.EventAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    public static EventDBManager eventDBManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDBManager = new EventDBManager(requireContext());
        eventDBManager.open();
        eventAdapter = new EventAdapter(requireContext(), new ArrayList<>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        // Set adapter first with empty data
        recyclerView.setAdapter(eventAdapter);

        // Then load data
        loadEvents();

        return view;
    }

    private void loadEvents() {
        eventDBManager.refreshEvents((success, events) -> {
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    if (success && events != null) {
                        eventAdapter = new EventAdapter(requireContext(), events);
                    } else {
                        List<Event> cachedEvents = eventDBManager.getAllEvents();
                        if (!cachedEvents.isEmpty()) {
                            eventAdapter = new EventAdapter(requireContext(), cachedEvents);
                        }
                    }
                    recyclerView.setAdapter(eventAdapter);
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventDBManager != null) {
            eventDBManager.close();
        }
    }
}
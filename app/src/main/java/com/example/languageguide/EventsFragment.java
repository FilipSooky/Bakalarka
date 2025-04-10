package com.example.languageguide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.languageguide.utils.Event;
import com.example.languageguide.utils.EventDBManager;
import com.example.languageguide.utils.adapters.EventAdapter;

import java.util.List;

public class EventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    public static EventDBManager eventDBManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        eventDBManager = new EventDBManager(getContext());
        eventDBManager.open();

        List<Event> events = eventDBManager.getAllEvents();
        eventAdapter = new EventAdapter(events);
        recyclerView.setAdapter(eventAdapter);

        return view;
    }
}
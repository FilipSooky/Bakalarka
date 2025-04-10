package com.example.languageguide;

import static com.example.languageguide.utils.Utils.loadFloorsFromJson;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.example.languageguide.utils.adapters.FloorAdapter;
import com.example.languageguide.utils.locations.Floor;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private SearchView searchViewHome;
    private List<Floor> floors = new ArrayList<>();
    private int selectedSemester = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        searchViewHome = view.findViewById(R.id.searchViewHome);

        floors = loadFloorsFromJson(requireContext());
        loadFloors();

        Spinner spinnerSemester = view.findViewById(R.id.spinnerSemesterHome);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Arrays.asList("1", "2", "3", "4", "5", "6") // Bachelor semesters
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(adapter);

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSemester = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchViewHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("SEARCH_QUERY", query);
                bundle.putInt("SELECTED_SEMESTER", selectedSemester);

                LocationsFragment locationsFragment = new LocationsFragment();
                locationsFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, locationsFragment) // Make sure the container ID is correct
                        .addToBackStack(null)
                        .commit();

                NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_location); // Set the Location button as selected

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }
    private void loadFloors() {
        GridView gridViewFloors = view.findViewById(R.id.gridViewFloors);
        FloorAdapter adapter = new FloorAdapter(getContext(), floors);
        gridViewFloors.setAdapter(adapter);
    }
}
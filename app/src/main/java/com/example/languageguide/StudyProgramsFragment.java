package com.example.languageguide;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.languageguide.utils.StudyProgram;
import com.example.languageguide.utils.adapters.StudyProgramAdapter;

import java.util.ArrayList;
import java.util.List;


public class StudyProgramsFragment extends Fragment {


    private RecyclerView recyclerView;
    private StudyProgramAdapter adapter;
    private List<StudyProgram> allPrograms = new ArrayList<>();
    private List<StudyProgram> filteredPrograms = new ArrayList<>();

    public StudyProgramsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_programs, container, false);

        recyclerView = view.findViewById(R.id.recycler_study_programs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allPrograms = loadPrograms(); // Simulácia databázy
        filteredPrograms.addAll(allPrograms);

        adapter = new StudyProgramAdapter(filteredPrograms);
        recyclerView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPrograms(newText);
                return true;
            }
        });

        return view;
    }

    private void filterPrograms(String query) {
        filteredPrograms.clear();
        for (StudyProgram program : allPrograms) {
            if (program.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredPrograms.add(program);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private List<StudyProgram> loadPrograms() {
        List<StudyProgram> studyPrograms = new ArrayList<>();
        Resources resources = getResources();
        studyPrograms.add(new StudyProgram(
                resources.getString(R.string.sp_aplikovana_informatika_nazov),
                resources.getString(R.string.sp_aplikovana_informatika_typ),
                resources.getString(R.string.sp_aplikovana_informatika_forma),
                resources.getString(R.string.sp_aplikovana_informatika_popis)
        ));

        studyPrograms.add(new StudyProgram(
                resources.getString(R.string.sp_manazment_informatika_nazov),
                resources.getString(R.string.sp_manazment_informatika_typ),
                resources.getString(R.string.sp_manazment_informatika_forma),
                resources.getString(R.string.sp_manazment_informatika_popis)
        ));

        studyPrograms.add(new StudyProgram(
                resources.getString(R.string.sp_pocitacove_siete_nazov),
                resources.getString(R.string.sp_pocitacove_siete_typ),
                resources.getString(R.string.sp_pocitacove_siete_forma),
                resources.getString(R.string.sp_pocitacove_siete_popis)
        ));
        return studyPrograms;
    }
}
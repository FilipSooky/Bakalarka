package com.example.languageguide.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.languageguide.R;
import com.example.languageguide.utils.StudyProgram;

import java.util.List;

public class StudyProgramAdapter extends RecyclerView.Adapter<StudyProgramAdapter.ViewHolder> {

    private List<StudyProgram> programs;

    public StudyProgramAdapter(List<StudyProgram> programs) {
        this.programs = programs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, level, form, description;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.program_name);
            level = view.findViewById(R.id.program_level);
            form = view.findViewById(R.id.program_form);
            description = view.findViewById(R.id.program_description);
        }
    }

    @Override
    public StudyProgramAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_program, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudyProgram program = programs.get(position);
        holder.name.setText(program.getName());
        holder.level.setText(program.getLevel());
        holder.form.setText(program.getForm());
        holder.description.setText(program.getDescription());
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }
}


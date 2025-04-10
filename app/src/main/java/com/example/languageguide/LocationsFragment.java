package com.example.languageguide;

import static com.example.languageguide.utils.Utils.loadFloorsFromJson;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.languageguide.utils.Utils;
import com.example.languageguide.utils.locations.Floor;
import com.example.languageguide.utils.locations.Room;
import com.example.languageguide.utils.locations.ScheduleHour;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class LocationsFragment extends Fragment {
    private PhotoView photoView;
    private Button buttonFloorSelection;
    private SearchView searchView;
    private List<Floor> floors = new ArrayList<>();
    private Spinner spinnerSemester;
    private int selectedSemester = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        photoView = view.findViewById(R.id.photoView);
        photoView.setMaximumScale(5.0f); // Allows zooming up to 5x
        photoView.setMinimumScale(1.0f);

        LinearLayout toggleContainer = view.findViewById(R.id.toggleContainer);
        toggleContainer.bringToFront();

        searchView = view.findViewById(R.id.searchView);

        setupSearch();
        setupSpinner(view);

        buttonFloorSelection = view.findViewById(R.id.buttonFloorSelection);
        buttonFloorSelection.setText(getString(R.string.select));

        floors = loadFloorsFromJson(requireContext());

        buttonFloorSelection.setOnClickListener(this::showFloorSelectionMenu);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey("SELECTED_SEMESTER")) {
                selectedSemester = args.getInt("SELECTED_SEMESTER");
                spinnerSemester.setSelection(selectedSemester - 1);

            }
            if (args.containsKey("SEARCH_QUERY")) {
                String searchQuery = args.getString("SEARCH_QUERY");
                view.post(() -> searchRoomBySubject(searchQuery));
            }
        }

        return view;
    }

    private void setupSpinner(View view)
    {
        spinnerSemester = view.findViewById(R.id.spinnerSemester);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Arrays.asList("1", "2", "3", "4", "5", "6")
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(adapter);

        // Listener na výber semestra
        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSemester = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showFloorSelectionMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);

        for (Floor floor : floors) {
            popupMenu.getMenu().add(floor.getName(getContext()));
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String selectedFloor = menuItem.getTitle().toString();
            buttonFloorSelection.setText(selectedFloor);
            Floor floor = getFloorByName(selectedFloor);

            if (floor != null) {
                showRoomsMenu(anchor, floor);
            }
            return true;
        });

        popupMenu.show();
    }

    private void showRoomsMenu(View anchor, Floor floor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);
        List<Room> filteredRooms = new ArrayList<>();

        for (Room room : floor.getRooms()) {
            List<ScheduleHour> filteredSubjects = new ArrayList<>();

            for (ScheduleHour hour : room.getScheduleHours()) {
                if (hour.getSemester() == selectedSemester) {
                    filteredSubjects.add(hour);
                }
            }

            if (!filteredSubjects.isEmpty()) {
                filteredRooms.add(new Room(room.getName(), filteredSubjects));
            }
        }

        if (filteredRooms.isEmpty()) {
            Utils.showNoRoomsDialog(requireContext());
            return;
        }

        for (Room room : filteredRooms) {
            popupMenu.getMenu().add(room.getName());
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String selectedRoom = menuItem.getTitle().toString();
            Room room = getRoomByName(filteredRooms, selectedRoom);
            if (room != null) {
                showScheduleDialog(room, floor);
            }
            return true;
        });

        popupMenu.show();
    }

    private void showScheduleDialog(Room room, Floor floor) {
        String resourceName = floor.getImageResource(); // Get the resource name as a String
        int resourceId = getResources().getIdentifier(resourceName, "drawable", requireActivity().getPackageName()); // Inside Fragment

        photoView.setImageResource(resourceId );
        StringBuilder schedule = new StringBuilder();
        for (ScheduleHour hour : room.getScheduleHours()) {
            schedule.append(hour.getSubject(requireContext())) // Use the utility method
                    .append(" (")
                    .append(hour.getType(getContext()))
                    .append(") - ")
                    .append(hour.getTimeFrom())
                    .append(" - ")
                    .append(hour.getTimeTo())
                    .append("\n");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.subjects_in_room) + " " + room.getName())
                .setMessage(schedule.toString().trim())
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }

    private void showRoomSelectionDialog(List<Room> rooms) {
        // Create an array of strings to display in the dialog
        String[] roomDetails = new String[rooms.size()];

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            StringBuilder roomInfo = new StringBuilder();
            // Add the room name
            roomInfo.append(room.getName()).append(":\n");
            // Add the subjects for the room
            for (ScheduleHour hour : room.getScheduleHours()) {
                roomInfo.append(hour.getSubject(requireContext()))
                        .append(" (")
                        .append(hour.getType(getContext()))
                        .append(") - ")
                        .append(hour.getTimeFrom())
                        .append(" - ")
                        .append(hour.getTimeTo())
                        .append("\n");
            }
            // Add the room details to the array
            roomDetails[i] = roomInfo.toString();
        }
        // Show the dialog with room details
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_room))
                .setItems(roomDetails, (dialog, which) -> {
                    Room selectedRoom = rooms.get(which);
                    Floor matchedFloor = selectedRoom.getFloor(floors);
                    buttonFloorSelection.setText(matchedFloor.getName(requireContext()) + " - " + selectedRoom.getName());
                    showScheduleDialog(selectedRoom, matchedFloor);
                })
                .show();
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRoomBySubject(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // We handle search only on submit
            }
        });
    }

    private void searchRoomBySubject(String query) {
        if (floors == null || floors.isEmpty()) {
            Log.e("Search", "No floors data available!");
            return;
        }
        query = query.toLowerCase().trim();
        List<Room> matchingRooms = new ArrayList<>();

        for (Floor floor : floors) {
            for (Room room : floor.getRooms()) {
                List<ScheduleHour> matchingSubjects = new ArrayList<>();

                for (ScheduleHour hour : room.getScheduleHours()) {
                    if (hour.getSubject(requireContext()).toLowerCase().contains(query) && hour.getSemester() == selectedSemester) {
                        matchingSubjects.add(hour);
                    }
                }

                if (!matchingSubjects.isEmpty()) {
                    matchingRooms.add(new Room(room.getName(), matchingSubjects));
                }
            }
        }

        if (!matchingRooms.isEmpty()) {
            if (matchingRooms.size() == 1) {
                Room matchedRoom = matchingRooms.get(0);
                Floor matchedFloor = matchedRoom.getFloor(floors);
                buttonFloorSelection.setText(matchedFloor.getName(getContext()) + " - " + matchedRoom.getName());
                showScheduleDialog(matchedRoom, matchedFloor);
            } else {
                showRoomSelectionDialog(matchingRooms);
            }
        } else Utils.showNoRoomsDialog(requireContext());
    }

    private Floor getFloorByName(String name) {
        for (Floor floor : floors) {
            if (floor.getName(getContext()).equals(name)) {
                return floor;
            }
        }
        return null;
    }

    private Room getRoomByName(List<Room> rooms, String name) {
        for (Room room : rooms) {
            if (room.getName().equals(name)) {
                return room;
            }
        }
        return null;
    }

}
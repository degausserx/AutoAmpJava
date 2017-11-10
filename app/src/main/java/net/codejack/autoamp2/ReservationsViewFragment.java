package net.codejack.autoamp2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.codejack.autoamp2.collections.Days;
import net.codejack.autoamp2.data.DayPlan;
import net.codejack.autoamp2.data.Lesson;
import net.codejack.autoamp2.data.Student;
import net.codejack.autoamp2.helpers.SharingIsCaring;
import net.codejack.autoamp2.helpers.StringStuff;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReservationsViewFragment extends Fragment {

    public static final String TAG = "ReservationsView";

    private Date date;
    private Button button_save;
    private Spinner spinner_student;
    private TableLayout table;
    private List<Student> student_list;
    private Map<String, Student> student_map;
    private ArrayAdapter<?> studentAdapter;
    private Map<String, String> appointments;
    private Student last_student;
    private List rows;
    private View view;
    private List<Date> dates;

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reservations_view, container, false);
        v = init(v);
        this.view = v;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Edit reservations");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    public ReservationsViewFragment() {

    }

    private void goback() {
        ((MainActivity) this.getActivity()).passScreen(ReservationsFragment.TAG, false);
    }

    private View init(View v) {
        date = (Date) SharingIsCaring.getInstance().getData();

        button_save = (Button) v.findViewById(R.id.button_reservations_save);
        spinner_student = (Spinner) v.findViewById(R.id.spinner_reservations_user);
        table = (TableLayout) v.findViewById(R.id.table_reservations);

        appointments = new TreeMap<>();
        student_list = new LinkedList<>();
        student_map = new HashMap<>();
        studentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, student_list);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid());

        spinner_student.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                last_student = (Student) studentAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initStudents();

        initButton(button_save);

        return v;
    }

    private void initButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save

                gatherDate();

                goback();
            }
        });
    }

    private void initStudents() {
        mDatabase.child("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    student_list.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Student post = (Student) postSnapshot.getValue(Student.class);
                        String id = postSnapshot.getKey();
                        student_list.add(post);
                        student_map.put(id, post);
                    }

                    spinner_student.setAdapter(studentAdapter);

                    initTimes();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initTimes() {
        mDatabase.child("schedule").child("reservations").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String date_string = (String) postSnapshot.getKey();
                        String value = (String) postSnapshot.getValue();
                        Date end = StringStuff.endOfDayWith(date);
                        Date start = StringStuff.midnightWith(date);
                        Date time = StringStuff.stringToDate(date_string);


                        if (time.before(end) && time.after(start)) {
                            appointments.put(date_string, value);
                        }

                    }
                }

                initDays();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initDays() {
        mDatabase.child("schedule").child("days").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Days post = (Days) postSnapshot.getValue(Days.class);
                        String id = postSnapshot.getKey();

                        int track = 0;

                        for (HashMap.Entry<String, String> map: appointments.entrySet()) {

                            String search = map.getKey();

                            for (DayPlan plan: post.getPlan()) {

                                // get time. i did this badly, had trouble with object/hashmap on firebase and didn't pass object data over properly.

                            }
                        }
                    }
                    initRows();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initRows() {

        rows = new ArrayList();

        Map<String, String> appointsments_sorted = new TreeMap<>();
        Map<String, String> treeMap = new TreeMap<String, String>(appointments);
        for (String str : treeMap.keySet()) {
            appointsments_sorted.put(str, appointments.get(str));
        }

        appointments = appointsments_sorted;

        if (rows.size() < 1) {

            for (Map.Entry<String, String> entry : appointments.entrySet()) {

                String key = entry.getKey();
                String user_id = entry.getValue();

                String[] splitTime = key.trim().split("\\s+");
                String time = splitTime[1];

                // get name here using Student objects

                // get something else to determine finish time

                final TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_row_reservations, null);
                final TextView startTime = (TextView) tableRow.findViewById(R.id.edit_reservations_view_edit_start);
                final TextView studentNameO = (TextView) tableRow.findViewById(R.id.text_reservations_name);
                final TextView HiddenID = (TextView) tableRow.findViewById(R.id.text_reservation_hidden);
                final Switch switchObject = (Switch) tableRow.findViewById(R.id.switch_reservations_view);

                startTime.setText(time);

                if (user_id.equals("NULL")) {
                    studentNameO.setText("");
                    switchObject.setChecked(false);
                } else {
                    studentNameO.setText(student_map.get(user_id).getFirstName() + " " + student_map.get(user_id).getLastName());
                    switchObject.setChecked(true);
                }

                HiddenID.setText(user_id);

                table.addView(tableRow);

                rows.add(tableRow);

                switchObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (last_student instanceof Student) {
                            if (!isChecked) {
                                studentNameO.setText("");
                                HiddenID.setText("NULL");
                            }
                            else {
                                // get player from student list, display name
                                Student s = (Student) spinner_student.getSelectedItem();
                                studentNameO.setText(last_student.getFirstName() + " " + last_student.getLastName());
                                HiddenID.setText(last_student.getStudentId());
                            }
                        } else {

                        }
                    }
                });
            }
        }

    }

    private void gatherDate() {

        TableLayout table = (TableLayout) this.view.findViewById(R.id.table_reservations);

        for (int i = 0; i < table.getChildCount(); i++) {
            View view = table.getChildAt(i);
            if (view instanceof TableRow) {

                TableRow row = (TableRow) view;

                String start;
                String id;

                final TextView startTime = (TextView) row.findViewById(R.id.edit_reservations_view_edit_start);
                final TextView studentNameO = (TextView) row.findViewById(R.id.text_reservations_name);
                final TextView HiddenID = (TextView) row.findViewById(R.id.text_reservation_hidden);
                final Switch switchObject = (Switch) row.findViewById(R.id.switch_reservations_view);

                boolean switchStatus = switchObject.isChecked();
                id = HiddenID.getText().toString().trim();
                start = startTime.getText().toString();

                String day = StringStuff.dateToString(date) + " " + start;
                Date d = StringStuff.stringToDate(day);

                if (switchObject.isChecked()) {
                    Student s = student_map.get(id);

                    Lesson lesson = new Lesson();
                    lesson.setOpen(true);
                    lesson.setDate(d);
                    lesson.setStudent(s);

                    mDatabase.child("schedule").child("lessons").child(day).setValue(lesson);
                    mDatabase.child("schedule").child("reservations").child(day).setValue(id);
                }

                else {
                    mDatabase.child("schedule").child("lessons").child(day).removeValue();
                    mDatabase.child("schedule").child("reservations").child(day).setValue("NULL");
                }
            }
        }

    }

}

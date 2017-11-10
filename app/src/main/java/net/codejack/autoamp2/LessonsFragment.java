package net.codejack.autoamp2;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import net.codejack.autoamp2.data.Lesson;
import net.codejack.autoamp2.data.Student;
import net.codejack.autoamp2.helpers.StringStuff;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LessonsFragment extends Fragment {

    public static final String TAG = "Lessons";
    public static final int REQUEST_GPS = 1;

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    private TableLayout table;
    private TableLayout table2;
    private Map<String, Lesson> lessons;
    private Map<String, Student> students;
    private Button button_today;
    private Button button_upcoming;

    private boolean loaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lessons, container, false);

        if(savedInstanceState == null){
            loaded = false;
        }

        init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Lessons");
    }

    public LessonsFragment() {

    }

    private View init(View v) {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid());

        table = (TableLayout) v.findViewById(R.id.table_lessons);
        table2 = (TableLayout) v.findViewById(R.id.table2_lessons);
        table2.setVisibility(View.GONE);

        button_today = (Button) v.findViewById(R.id.lessons_button_today);
        button_upcoming = (Button) v.findViewById(R.id.lessons_button_upcoming);

        lessons = new TreeMap<>();
        students = new HashMap<>();

        initButtons();

        initLessonListener();

        return v;
    }

    private void initButtons() {
        button_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (table.getVisibility() == View.GONE) {
                    table2.setVisibility(View.GONE);
                    table.setVisibility(View.VISIBLE);
                }
            }
        });

        button_upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (table2.getVisibility() == View.GONE) {
                    table.setVisibility(View.GONE);
                    table2.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initLessonListener() {
        mDatabase.child("schedule").child("lessons").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (isAdded() && !loaded) {

                    lessons.clear();
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        String string = (String) post.getKey();
                        Lesson lesson = (Lesson) post.getValue(Lesson.class);
                        lessons.put(string, lesson);
                    }

                    loaded = true;
                    initTable();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void initTable() {

        Date midnight = StringStuff.midnight();
        Date endofnight = StringStuff.endOfDay();
        int helper = 0;
        table.removeAllViews();
        table2.removeAllViews();

        for (String less : lessons.keySet()) {

            Lesson lesson = lessons.get(less);

            final Student student = lesson.getStudent();
            students.put(student.getStudentId(), student);

            Date date = lesson.getDate();

            final TableRow row = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_row_lessons, null);
            final TextView text_start;
            final TextView text_name;
            final Button button_start;
            final Button button_close;
            final TextView text_full;
            final TextView text_id;

            if (date.after(midnight)) {

                text_start = (TextView) row.findViewById(R.id.text_lessons_start);
                text_name = (TextView) row.findViewById(R.id.text_lessons_name);
                text_full = (TextView) row.findViewById(R.id.text_lessons_name_full);
                text_id = (TextView) row.findViewById(R.id.text_lessons_id);
                button_start = (Button) row.findViewById(R.id.button_lessons_start);
                button_close = (Button) row.findViewById(R.id.button_lessons_close);

                if (RouteTrackingFragment.getInstance().status && RouteTrackingFragment.getInstance().time.equals(StringStuff.dateToStringMinute(date))) {
                    button_start.setText("Stop");
                    button_start.setBackgroundColor(getResources().getColor(R.color.colorRed));

                    button_close.setText("View");
                    button_close.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                }

                text_full.setText(StringStuff.dateToStringMinute(date));
                text_id.setText(student.getStudentId());

                if (!lesson.isOpen()) {
                    text_start.setText(StringStuff.dateToStringMinute(date));
                    button_start.setVisibility(View.GONE);
                    button_close.setVisibility(View.GONE);
                }

                else {

                    if (date.before(endofnight)) {
                        text_start.setText(StringStuff.dateToStringHourOnly(date));
                        helper = 0;
                    }

                    else {
                        text_start.setText(StringStuff.dateToStringMinute(date));
                        button_start.setVisibility(View.GONE);
                        button_close.setVisibility(View.GONE);
                        helper = 1;
                    }

                    initClose(button_close, row, helper);
                    initStart(button_start, row, button_close);
                }

                text_name.setText(lesson.getStudent().getFirstName() + lesson.getStudent().getLastName());

            } else {
                helper = 2;
            }

            if (helper == 0) table.addView(row);
            else if (helper == 1) table2.addView(row);

        }

    }

    private void initClose(Button button, final TableRow row, final int i) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!RouteTrackingFragment.getInstance().status) {
                    if (i == 0) {
                        // remove today
                        removeRow(i, true, row);
                    } else {
                        removeRow(i, false, row);
                        // remove
                    }
                }
                else {
                    ((MainActivity) getActivity()).replaceScreen("RouteTracking");
                }
            }
        });
    }

    private void initStart(final Button button, final TableRow row, final Button button_close) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!RouteTrackingFragment.getInstance().status) {
                    if (!RouteTrackingFragment.getInstance().isPermissive((MainActivity) getActivity())) {
                        requestAction();
                    }
                    else {
                        TextView time = (TextView) row.findViewById(R.id.text_lessons_name_full);
                        TextView name = (TextView) row.findViewById(R.id.text_lessons_name);
                        TextView id = (TextView) row.findViewById(R.id.text_lessons_id);
                        button.setText("Stop");
                        button_close.setText("View");
                        button_close.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                        String myTime = time.getText().toString().trim();
                        String myName = name.getText().toString().trim();
                        String myID = id.getText().toString();
                        button.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        RouteTrackingFragment.getInstance().startTracking(myTime, myName, students.get(myID));
                    }
                }

                else if (button.getText().toString().toLowerCase().equals("stop")) {
                    button.setText("Start");
                    button.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    button_close.setText("Close");
                    button_close.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                    RouteTrackingFragment.getInstance().stopTracking();
                }

            }
        });
    }

    private void removeRow(int i, boolean today, TableRow row) {

        TableLayout table;
        if (today) table = this.table;
        else table = this.table2;

        final TextView text_full = (TextView) row.findViewById(R.id.text_lessons_name_full);
        final Button button_start = (Button) row.findViewById(R.id.button_lessons_start);
        final Button button_close = (Button) row.findViewById(R.id.button_lessons_close);

        button_close.setVisibility(View.GONE);
        button_start.setVisibility(View.GONE);

        String time = text_full.getText().toString();

        Lesson lesson = lessons.get(time);
        lesson.setOpen(false);

        mDatabase.child("schedule").child("lessons").child(time).setValue(lesson);

    }

    private void requestAction() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
    }
}
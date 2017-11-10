package net.codejack.autoamp2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.codejack.autoamp2.collections.Days;
import net.codejack.autoamp2.collections.ReservationsManager;
import net.codejack.autoamp2.data.Reservations;
import net.codejack.autoamp2.data.Schedule;
import net.codejack.autoamp2.helpers.StringStuff;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Schedule";
    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    private List<Days> listOfDays;
    private ArrayAdapter<?> arrayAdapter;
    private CaldroidFragment caldroidFragment;
    private List<Days> scheduleList;
    private Calendar cal;
    private Schedule schedule;
    private Map<Date, Integer> lastColor;
    private ReservationsManager reservationsManager;
    private boolean can_update;

    private Button save;
    private Button create;
    private Button edit;
    private Spinner day;

    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Schedule");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    public ScheduleFragment() {

    }

    private void goback() {
        ((MainActivity) this.getActivity()).passScreen(HomeFragment.TAG, false);
    }

    private View init(View v) {

        reservationsManager = new ReservationsManager();

        save = (Button) v.findViewById(R.id.button_schedule_main_save);
        create = (Button) v.findViewById(R.id.button_schedule_main_create);
        edit = (Button) v.findViewById(R.id.button_schedule_main_edit);
        day = (Spinner) v.findViewById(R.id.spinner_schedule_day);

        save.setOnClickListener(this);
        create.setOnClickListener(this);
        edit.setOnClickListener(this);

        listOfDays = new ArrayList<>();
        schedule = new Schedule();
        scheduleList = new LinkedList<>();
        lastColor = new HashMap<>();

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
        args.putInt(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, 0);
        caldroidFragment.setArguments(args);

        Calendar c = Calendar.getInstance();
        Date theDate = c.getTime();
        caldroidFragment.setMinDate(theDate);

        c.add(Calendar.MONTH, 3);
        int max = c.getActualMaximum(c.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, max);
        theDate = c.getTime();
        caldroidFragment.setMaxDate(theDate);

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listOfDays);

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.container_calendar, caldroidFragment);
        t.commit();

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid()).child("schedule");

        mDatabase.child("days").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    can_update = true;
                    listOfDays.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Days post = postSnapshot.getValue(Days.class);
                        listOfDays.add(post);
                    }

                    day.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("schedule").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    Schedule post = (Schedule) dataSnapshot.getValue(Schedule.class);
                    if (post != null) {
                        schedule = post;
                        scheduleList = post.getSchedule();
                        lastColor.clear();
                        can_update = true;
                        for (int i = 0; i < scheduleList.size(); i++) {
                            Days sc = scheduleList.get(i);
                            Date d = getDate(sc.getDate_year(), sc.getDate_month(), sc.getDate_day());
                            Date now = StringStuff.midnight();
                            if (d.compareTo(now) >= 0) {
                                lastColor.put(d, 1);
                                caldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(translateColor(sc.getColor())), d);
                            }
                            else {
                                scheduleList.remove(sc);
                                update();
                            }
                        }
                        caldroidFragment.refreshView();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("reservations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    ReservationsManager post = (ReservationsManager) dataSnapshot.getValue(ReservationsManager.class);
                    if (post != null) {
                        reservationsManager.putReservationsList(post.getAll());
                        caldroidFragment.refreshView();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {

                Days c = (Days) day.getSelectedItem();

                if (c != null) {

                    String col = c.getColor();
                    Days again = new Days();

                    cal.setTime(date);
                    again.setDate_day(cal.get(Calendar.DAY_OF_MONTH));
                    again.setDate_month(cal.get(Calendar.MONTH));
                    again.setDate_year(cal.get(Calendar.YEAR));
                    again.setName(c.getName());
                    again.setPlan(c.getPlan());
                    again.setColor(c.getColor());
                    again.setSafe(true);
                    boolean matching = false;

                    Days match = getMatch(again);
                    if (match.getColor() != null) {
                        again = match;
                        matching = true;
                    }

                    if (!lastColor.containsKey(date) || lastColor.get(date) < 1) {
                        if (listOfDays.size() > 0) {
                            caldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(translateColor(col)), date);
                            if (!matching) scheduleList.add(again);
                            lastColor.put(date, 1);
                        }
                    }

                    else {
                        caldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.black), date);
                        scheduleList.remove(index);
                        lastColor.remove(date);
                    }

                    caldroidFragment.refreshView();
                }
            }

            @Override
            public void onCaldroidViewCreated() {

            }

        };

        caldroidFragment.setCaldroidListener(listener);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_schedule_main_save: save();
                break;
            case R.id.button_schedule_main_create: goToCreate();
                break;
            case R.id.button_schedule_main_edit: goToEdit();
        }
    }

    private void goToCreate() {
        ((MainActivity) getActivity()).replaceScreen(ScheduleCreateFragment.TAG);
    }

    private void goToEdit() {
        // pass in correct spinner element, skipping until later if i have time for this area
        ((MainActivity) getActivity()).replaceScreen(ScheduleEditFragment.TAG);
    }

    private void save() {
        update();
        goback();
    }

    private int translateColor(String col) {
        switch (col) {
            case "red": return R.drawable.red;
            case "blue": return R.drawable.blue;
            case "aqua": return R.drawable.aqua;
            case "green": return R.drawable.green;
            case "purple": return R.drawable.purple;
            case "orange": return R.drawable.orange;
            case "brown": return R.drawable.brown;
        }
        return 0;
    }

    private Date getDate(int year, int month, int day) {
        Date d = new GregorianCalendar(year, month, day).getTime();
        return d;
    }

    private Days getMatch(Days d) {
        for (int i = 0; i < scheduleList.size(); i++) {
            if (d.toString().equals(scheduleList.get(i).toString())) {
                index = i;
                return scheduleList.get(i);
            }
        }
        return new Days();
    }

    private void update() {
        if (can_update) {
            // save data
            schedule.setSchedule(scheduleList);

            reservationsManager.putSchedule(schedule);
            reservationsManager.mergeWith();
            reservationsManager.setToUpload();

            mDatabase.child("reservations").removeValue();

            HashMap<String, Reservations> b = reservationsManager.getAll();
            for (Map.Entry key: b.entrySet()) {
                String time = (String) key.getKey();

                Reservations value = (Reservations) key.getValue();
                mDatabase.child("reservations").child(time).setValue(value.getUserID());
            }

            mDatabase.child("schedule").setValue(schedule);
        }
    }

}
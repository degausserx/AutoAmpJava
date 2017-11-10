package net.codejack.autoamp2;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.codejack.autoamp2.collections.ReservationsManager;
import net.codejack.autoamp2.data.Reservations;
import net.codejack.autoamp2.helpers.SharingIsCaring;
import net.codejack.autoamp2.helpers.StringStuff;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ReservationsFragment extends Fragment {

    public static final String TAG = "Reservations";

    private CaldroidFragment caldroidFragment;
    private ReservationsManager reservationsManager;
    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    private Calendar calendar;
    private List<Date> dates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reservations, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Reservations");
    }

    public ReservationsFragment() {

    }

    private View init(View v) {

        reservationsManager = new ReservationsManager();
        dates = new LinkedList<>();

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        calendar = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, calendar.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, calendar.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
        args.putInt(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, 0);
        caldroidFragment.setArguments(args);

        Calendar c = Calendar.getInstance();
        Date theDate = c.getTime();
        caldroidFragment.setMinDate(theDate);

        c.add(Calendar.MONTH, 3);
        int dom = c.DAY_OF_MONTH;
        int max = c.getActualMaximum(dom);
        c.set(Calendar.DAY_OF_MONTH, max);
        theDate = c.getTime();
        caldroidFragment.setMaxDate(theDate);

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.reservations_container_calendar, caldroidFragment);
        t.commit();

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid()).child("schedule");

        mDatabase.child("reservations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    HashMap<String, Reservations> map = new HashMap<>();
                    int missing_lesson = 0;
                    int total_lesson = 0;
                    String time = "";

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String time_before = time;
                        time = child.getKey();
                        String id = (String) child.getValue();

                        Reservations r = new Reservations();
                        r.setUserID(id);
                        r.setTimeExtended(time);

                        Date current = new Date();
                        String now = StringStuff.dateToStringMidnight(current);

                        // if the day is old.
                        if (time.compareTo(now) < 0) {
                            time = "";
                        }

                        else {

                            map.put(time, r);

                            if (time_before.equals("")) {
                                total_lesson++;
                                if (id.equals("NULL") || id.length() < 1) missing_lesson++;
                            }

                            else {

                                Date time_before_date = StringStuff.midnightFromPerfect(time_before);
                                Date time_date = StringStuff.midnightFromPerfect(time);
                                String tb = StringStuff.dateToString(time_before_date);
                                String t = StringStuff.dateToString(time_date);

                                if (tb.equals(t)) {
                                    total_lesson++;
                                    if (id.equals("NULL") || id.length() < 1) missing_lesson++;
                                } else {

                                    Date d = StringStuff.stringToDateDay(time_before);
                                    Date midnight = StringStuff.midnightPerfect(d);
                                    dates.add(midnight);

                                    String piece = "green";
                                    if ((total_lesson - missing_lesson) == 0) piece = "orange";
                                    if (missing_lesson == 0) piece = "purple";

                                    drawBackground(piece, midnight);

                                    total_lesson = 1;
                                    missing_lesson = 0;
                                    if (id.equals("NULL") || id.length() < 1) missing_lesson++;
                                }
                            }
                        }

                    }

                    if (time.length() > 0) {

                        Date d = StringStuff.stringToDateDay(time);
                        Date midnight = StringStuff.midnightPerfect(d);
                        dates.add(midnight);

                        String piece = "green";
                        if ((total_lesson - missing_lesson) == 0) piece = "orange";
                        if (missing_lesson == 0) piece = "purple";

                        drawBackground(piece, midnight);

                        reservationsManager.putReservationsList(map);
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
                // check its clickable / color


                // go to next
                if (dates.contains(date)) {
                    SharingIsCaring.getInstance().setData(date);
                    ((MainActivity) getActivity()).replaceScreen(ReservationsViewFragment.TAG);
                }
            }

            @Override
            public void onCaldroidViewCreated() {

            }

        };

        caldroidFragment.setCaldroidListener(listener);

        return v;
    }

    private void drawBackground(String color, Date date) {
        Drawable draw = getResources().getDrawable(R.drawable.green);
        if (color.equals("purple")) draw = getResources().getDrawable(R.drawable.purple);
        else if (color.equals("orange")) draw = getResources().getDrawable(R.drawable.orange);
        caldroidFragment.setBackgroundDrawableForDate(draw, date);
    }
}
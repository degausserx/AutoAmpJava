package net.codejack.autoamp2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Home";
    private static HomeFragment instance;

    public static HomeFragment getInstance() {
        if(instance == null) {
            instance = new HomeFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        v = init(v);
        return v;
    }

    public HomeFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).unsetToolbarBack();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Home");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    private View init(View v) {

        TextView home_logout = (TextView) v.findViewById(R.id.home_button_logout);
        TextView home_students = (TextView) v.findViewById(R.id.home_button_students);
        TextView home_schedule = (TextView) v.findViewById(R.id.home_button_schedule);
        TextView home_reservations = (TextView) v.findViewById(R.id.home_button_reservations);
        TextView home_lessons = (TextView) v.findViewById(R.id.home_button_lessons);
        TextView home_route_tracking = (TextView) v.findViewById(R.id.home_button_route_tracking);

        home_logout.setOnClickListener(this);
        home_students.setOnClickListener(this);
        home_schedule.setOnClickListener(this);
        home_reservations.setOnClickListener(this);
        home_lessons.setOnClickListener(this);
        home_route_tracking.setOnClickListener(this);

        if (!RouteTrackingFragment.getInstance().status) home_route_tracking.setEnabled(false);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_button_students: loadFragment("Students");
                break;
            case R.id.home_button_schedule: loadFragment("Schedule");
                break;
            case R.id.home_button_reservations: loadFragment("Reservations");
                break;
            case R.id.home_button_lessons: loadFragment("Lessons");
                break;
            case R.id.home_button_route_tracking: loadFragment("RouteTracking");
                break;
            case R.id.home_button_logout: logout();
                break;
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        ((MainActivity) this.getActivity()).replaceScreen("Login");
    }

    private void loadFragment(String text) {
        ((MainActivity) this.getActivity()).replaceScreen(text);
    }
}
package net.codejack.autoamp2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScheduleEditFragment extends Fragment {

    public static final String TAG = "ScheduleEdit";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_edit, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Edit day");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    public ScheduleEditFragment() {

    }

    private void goback() {
        ((MainActivity) this.getActivity()).passScreen(ScheduleFragment.TAG, false);
    }

    private View init(View v) {

        return v;
    }
}

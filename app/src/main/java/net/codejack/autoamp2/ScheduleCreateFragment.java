package net.codejack.autoamp2;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import net.codejack.autoamp2.collections.Days;
import net.codejack.autoamp2.data.DayPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ScheduleCreateFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ScheduleCreate";
    private static List<DayPlan> timeList;
    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;

    static {
        timeList = new ArrayList<>();
    }

    private Button button_plus;
    private Button button_save;
    private EditText name;
    private Spinner color;
    private Spinner duration;
    private TableLayout table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_create, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Add day");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    public ScheduleCreateFragment() {

    }

    private void goback() {
        ((MainActivity) this.getActivity()).passScreen(ScheduleFragment.TAG, true);
    }

    private View init(View v) {

        table = (TableLayout) v.findViewById(R.id.schedule_create_table);
        button_plus = (Button) v.findViewById(R.id.button_schedule_create_plus);
        button_save = (Button) v.findViewById(R.id.button_schedule_create_save);
        name = (EditText) v.findViewById(R.id.edit_schedule_create_name);
        color = (Spinner) v.findViewById(R.id.spinner_schedule_color);
        duration = (Spinner) v.findViewById(R.id.spinner_schedule_duration);

        ((MainActivity) this.getActivity()).focusFix(name);

        button_plus.setOnClickListener(this);
        button_save.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_schedule_create_plus: addRow();
                break;
            case R.id.button_schedule_create_save: save();
                break;
        }
    }

    private void addRow() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "TimePicker");
    }

    protected void submitRow(TextView tv, TableRow row, String hour, String min) {
        int dur = getSpinnerDuration();
        DayPlan dp = new DayPlan(hour, min, dur);

        if (Integer.parseInt(dp.getStart_hour()) <= Integer.parseInt(dp.getEnd_hour())) {
            table.addView(row);
            tv.setText(dp.getEnd_hour() + ":" + dp.getEnd_minute());
            timeList.add(dp);
        }
        else {
            Toast.makeText(this.getContext(), "Midnight can't be crossed!", Toast.LENGTH_SHORT);
        }
    }

    private void save() {
        if (getText(name).length() > 0 && timeList.size() > 0) {
            // save

            Collections.sort(timeList);

            Days days = new Days();
            days.setPlan(timeList);
            days.setName(getText(name));
            days.setColor(getSpinnerColor());
            days.update();

            goback();
        }
        else {
            if (getText(name).length() < 1) Toast.makeText(this.getContext(), "Name field required", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this.getContext(), "No time slots found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getText(EditText e) {
        return e.getText().toString().trim();
    }

    private String getSpinnerColor() {
        final int i = color.getSelectedItemPosition();
        switch (i) {
            case 0: return "red";
            case 1: return "blue";
            case 2: return "aqua";
            case 3: return "green";
            case 4: return "purple";
            case 5: return "orange";
            case 6: return "brown";
        }
        return null;
    }

    private int getSpinnerDuration() {
        final int i = duration.getSelectedItemPosition();
        switch (i) {
            case 0: return 15;
            case 1: return 30;
            case 2: return 45;
            case 3: return 60;
            case 4: return 90;
            case 5: return 120;
            case 6: return 180;
            case 7: return 240;
        }
        return 0;
    }
}

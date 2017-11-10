package net.codejack.autoamp2;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

        ScheduleCreateFragment parent = (ScheduleCreateFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ScheduleCreateFragment.TAG);

        final TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_row_schedule, null);
        TextView tv;
        TextView te;
        final Button b = (Button) tableRow.findViewById(R.id.button_schedule_create_delete_row);

        String hour = String.format(Locale.ENGLISH, "%02d", hourOfDay);
        String min = String.format(Locale.ENGLISH, "%02d", minute);
        tv = (TextView) tableRow.findViewById(R.id.edit_schedule_create_edit_start);
        te = (TextView) tableRow.findViewById(R.id.edit_schedule_create_edit_end);
        tv.setText(hour + ":" + min);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_schedule_create_delete_row) {
                    TableLayout t = (TableLayout) tableRow.getParent();
                    t.removeView(tableRow);
                }
            }
        });

        parent.submitRow(te, tableRow, hour, min);
    }
}
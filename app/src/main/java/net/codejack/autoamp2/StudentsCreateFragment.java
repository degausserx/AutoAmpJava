package net.codejack.autoamp2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.codejack.autoamp2.collections.Students;
import net.codejack.autoamp2.data.Address;
import net.codejack.autoamp2.data.Student;


public class StudentsCreateFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "StudentsCreate";

    private EditText first_name;
    private EditText last_name;
    private EditText email;
    private EditText phone;
    private EditText address_street;
    private EditText address_number;
    private EditText address_postcode;
    private EditText address_city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_students_create, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Create student");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    public StudentsCreateFragment() {

    }

    private void goback() {
        ((MainActivity) this.getActivity()).passScreen(StudentsFragment.TAG, true);
    }

    private View init(View v) {

        Button create = (Button) v.findViewById(R.id.button_student_create_create);
        first_name = (EditText) v.findViewById(R.id.edit_student_create_first_name);
        last_name = (EditText) v.findViewById(R.id.edit_student_create_last_name);
        email = (EditText) v.findViewById(R.id.edit_student_create_email);
        phone = (EditText) v.findViewById(R.id.edit_student_create_phone);
        address_street = (EditText) v.findViewById(R.id.edit_student_create_address_street);
        address_number = (EditText) v.findViewById(R.id.edit_student_create_address_number);
        address_postcode = (EditText) v.findViewById(R.id.edit_student_create_address_postcode);
        address_city = (EditText) v.findViewById(R.id.edit_student_create_address_city);

        ((MainActivity) this.getActivity()).focusFix(first_name);
        ((MainActivity) this.getActivity()).focusFix(last_name);
        ((MainActivity) this.getActivity()).focusFix(email);
        ((MainActivity) this.getActivity()).focusFix(phone);
        ((MainActivity) this.getActivity()).focusFix(address_street);
        ((MainActivity) this.getActivity()).focusFix(address_number);
        ((MainActivity) this.getActivity()).focusFix(address_postcode);
        ((MainActivity) this.getActivity()).focusFix(address_city);

        create.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_student_create_create) {
            tryCreate();
        }
    }

    private void tryCreate() {
        if (getText(first_name).length() > 0 && getText(last_name).length() > 0) {

            // create object
            Student student = new Student();
            student.setFirstName(getText(first_name));
            student.setLastName(getText(last_name));
            student.setPhone(getText(phone));
            student.setEmail(getText(email));

            Address address = new Address();
            address.setStreet(getText(address_street));
            address.setCity(getText(address_city));
            address.setNumber(getText(address_number));
            address.setPostcode(getText(address_postcode));

            student.setAddress(address);

            // create DAO
            Students studentDAO = new Students(student);
            studentDAO.update();

            // go back
            getFragmentManager().popBackStackImmediate();
        }
        else {
            // toast
        }
    }

    private String getText(EditText e) {
        String s = e.getText().toString().trim();
        return s;
    }
}

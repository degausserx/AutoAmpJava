package net.codejack.autoamp2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.codejack.autoamp2.collections.Students;
import net.codejack.autoamp2.data.Address;
import net.codejack.autoamp2.data.Student;
import net.codejack.autoamp2.helpers.SharingIsCaring;


public class StudentsEditFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "StudentsEdit";

    private Student student;

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
        View v = inflater.inflate(R.layout.fragment_students_edit, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Edit Student");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    public StudentsEditFragment() {

    }

    private void goback() {
        ((MainActivity) this.getActivity()).passScreen(StudentsFragment.TAG, true);
    }

    private View init(View v) {
        student = (Student) SharingIsCaring.getInstance().getData();

        Button delete = (Button) v.findViewById(R.id.button_student_edit_delete);
        Button save = (Button) v.findViewById(R.id.button_student_edit_save);

        first_name = (EditText) v.findViewById(R.id.edit_student_edit_first_name);
        last_name = (EditText) v.findViewById(R.id.edit_student_edit_last_name);
        email = (EditText) v.findViewById(R.id.edit_student_edit_email);
        phone = (EditText) v.findViewById(R.id.edit_student_edit_phone);
        address_street = (EditText) v.findViewById(R.id.edit_student_edit_address_street);
        address_number = (EditText) v.findViewById(R.id.edit_student_edit_address_number);
        address_postcode = (EditText) v.findViewById(R.id.edit_student_edit_address_postcode);
        address_city = (EditText) v.findViewById(R.id.edit_student_edit_address_city);

        ((MainActivity) this.getActivity()).focusFix(first_name);
        ((MainActivity) this.getActivity()).focusFix(last_name);
        ((MainActivity) this.getActivity()).focusFix(email);
        ((MainActivity) this.getActivity()).focusFix(phone);
        ((MainActivity) this.getActivity()).focusFix(address_street);
        ((MainActivity) this.getActivity()).focusFix(address_number);
        ((MainActivity) this.getActivity()).focusFix(address_postcode);
        ((MainActivity) this.getActivity()).focusFix(address_city);

        first_name.setText(student.getFirstName());
        last_name.setText(student.getLastName());
        email.setText(student.getEmail());
        phone.setText(student.getPhone());
        address_street.setText(student.getAddress().getStreet());
        address_number.setText(student.getAddress().getNumber());
        address_postcode.setText(student.getAddress().getPostcode());
        address_city.setText(student.getAddress().getCity());

        delete.setOnClickListener(this);
        save.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_student_edit_delete: delete();
                break;
            case R.id.button_student_edit_save: save();
                break;
        }
    }

    private void save() {
        // update
        if (getText(first_name).length() > 0 && getText(last_name).length() > 0) {
            student.setFirstName(getText(first_name));
            student.setLastName(getText(last_name));
            student.setPhone(getText(phone));
            student.setEmail(getText(email));

            Address address = student.getAddress();
            address.setStreet(getText(address_street));
            address.setCity(getText(address_city));
            address.setNumber(getText(address_number));
            address.setPostcode(getText(address_postcode));
            student.setAddress(address);

            // create DAO
            Students studentDAO = new Students(student);
            studentDAO.update();

            goback();
        }
        else {
            // toast
        }
    }

    private void delete() {
        Students studentDAO = new Students(student);
        studentDAO.delete();

        goback();
    }

    private String getText(EditText e) {
        String s = e.getText().toString().trim();
        return s;
    }

}

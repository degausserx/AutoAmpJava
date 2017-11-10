package net.codejack.autoamp2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.codejack.autoamp2.data.Student;
import net.codejack.autoamp2.helpers.SharingIsCaring;

import java.util.LinkedList;
import java.util.List;

public class StudentsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Students";

    private ListView list;
    private ArrayAdapter<?> arrayAdapter;
    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    private List<Student> student_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_students, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Students");
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    public StudentsFragment() {

    }

    private View init(View v) {
        list = (ListView) v.findViewById(R.id.list_students);
        Button create = (Button) v.findViewById(R.id.button_create);

        create.setOnClickListener(this);

        student_list = new LinkedList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, student_list);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid()).child("students");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student_list.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Student post = postSnapshot.getValue(Student.class);
                    student_list.add(post);
                }

                list.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Student s = (Student) list.getItemAtPosition(i);
                SharingIsCaring.getInstance().setData(s);
                ((MainActivity) getActivity()).replaceScreen(StudentsEditFragment.TAG);
            }
        });

        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_create) {
            ((MainActivity) getActivity()).replaceScreen(StudentsCreateFragment.TAG);
        }
    }



}

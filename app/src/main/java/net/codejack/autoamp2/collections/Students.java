package net.codejack.autoamp2.collections;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.codejack.autoamp2.data.Student;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Degausser on 7/7/2017.
 */

public class Students {

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    private Student student;

    public Students(Student student) {
        this.student = student;
        init();
    }

    private void init() {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid()).child("students");
    }

    public void update() {
        if (student.getFirstName() != null) mDatabase.child(student.getStudentId()).setValue(student);
    }

    public void delete() {
        if (student.getFirstName() != null) mDatabase.child(student.getStudentId()).removeValue();
    }

}

package net.codejack.autoamp2.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.codejack.autoamp2.helpers.StringStuff;

import java.io.Serializable;

/**
 * Created by Degausser on 7/7/2017.
 */

public class Student implements Serializable {

    private String studentId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Address address;

    public Student() {
        studentId = StringStuff.getRandomString(20);
    }

    @Override
    public String toString(){
        return firstName + " " + lastName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

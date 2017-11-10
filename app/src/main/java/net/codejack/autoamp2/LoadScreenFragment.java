package net.codejack.autoamp2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class LoadScreenFragment extends Fragment {

    public static final String TAG = "LoadScreen";

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_load_screen, container, false);
        v = init(v);
        return v;
    }

    public LoadScreenFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).unsetToolbarBack();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name));
        ((MainActivity) this.getActivity()).setAsActive(TAG, true);
    }

    private View init(View v) {

        final Activity activity = ((MainActivity) this.getActivity());

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    ((MainActivity) getActivity()).replaceScreen("Home");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (isAdded()) {
                    FirebaseAuth.getInstance().signOut();
                    ((MainActivity) getActivity()).replaceScreen("Login");
                }
            }
        });

        return v;
    }

}

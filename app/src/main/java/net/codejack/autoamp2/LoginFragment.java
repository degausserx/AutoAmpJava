package net.codejack.autoamp2;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Activity activity;
    public static final String TAG = "Login";
    private static LoginFragment instance;

    private Button login_login, login_create, login_help;
    private EditText login_email, login_password;
    private CheckBox login_remember;

    private String final_email;
    private boolean remember = false;

    public static LoginFragment getInstance() {
        if(instance == null) {
            instance = new LoginFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        v = initView(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) this.getActivity()).unsetToolbarBack();
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name));
    }

    public LoginFragment() {

    }

    private View initView(View v) {
        activity = getActivity();

        login_login = (Button) v.findViewById(R.id.login_button_login);
        login_create = (Button) v.findViewById(R.id.login_button_create);
        login_help = (Button) v.findViewById(R.id.login_button_help);
        login_remember = (CheckBox) v.findViewById(R.id.login_checkbox_remember);
        login_email = (EditText) v.findViewById(R.id.login_edit_email);
        login_password = (EditText) v.findViewById(R.id.login_edit_password);

        ((MainActivity) this.getActivity()).focusFix(login_email);
        ((MainActivity) this.getActivity()).focusFix(login_password);

        login_login.setOnClickListener(this);
        login_create.setOnClickListener(this);
        login_help.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.wtf(TAG, "LOGIN: success");
                } else {
                    Log.wtf(TAG, "LOGIN: fail");
                }
            }
        };

        String email = null;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        remember = sharedPref.getBoolean("remember", false);
        if (remember) {
            email = sharedPref.getString("username", null);
            login_email.setText(email);
            if (!login_remember.isChecked()) login_remember.setChecked(true);
        }
        else {
            if (login_remember.isChecked()) login_remember.setChecked(false);
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_create: attemptCreate();
                break;
            case R.id.login_button_login: attemptLogin();
                break;
            case R.id.login_button_help: attemptHelp();
                break;
        }
    }

    private void createAccount(String email, String password) {
        disableButtons();
        final String final_email = email;
        final String final_password = password;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(activity, R.string.create_failed, Toast.LENGTH_SHORT).show();
                            enableButtons();
                        }

                        else {
                            signIn(final_email, final_password);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        disableButtons();
        final_email = email;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        login_password.setText("");
                        if (!task.isSuccessful()) {
                            enableButtons();
                            Toast.makeText(activity, R.string.auth_failed, Toast.LENGTH_SHORT).show();

                            login_password.setText("");

                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", "");
                            editor.putBoolean("remember", false);
                            editor.commit();
                        }
                        else {
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", final_email);
                            editor.putBoolean("remember", remember);
                            editor.commit();

                            activateHome(currentUser);
                        }
                    }
                });
    }

    private void attemptCreate() {
        String username = login_email.getText().toString();
        String password = login_password.getText().toString();
        if (username.length() > 0 && password.length() > 0) createAccount(username, password);
    }

    private void attemptLogin() {
        String username = login_email.getText().toString();
        String password = login_password.getText().toString();
        remember = login_remember.isChecked();

        if (username.length() > 0 && password.length() > 0) signIn(username, password);
    }

    private void attemptHelp() {

    }

    private void activateHome(FirebaseUser user) {
        ((MainActivity) this.getActivity()).replaceScreen("LoadScreen");
    }

    private void enableButtons() {
        setAvailability(true);
        login_create.setBackgroundResource(R.color.colorPurple);
        login_login.setBackgroundResource(R.color.colorGreen);
        login_help.setBackgroundResource(R.color.colorOrange);
    }

    private void disableButtons() {
        setAvailability(false);
        login_create.setBackgroundResource(R.color.colorG6);
        login_login.setBackgroundResource(R.color.colorG6);
        login_help.setBackgroundResource(R.color.colorG6);
    }

    private void setAvailability(boolean status) {
        login_create.setEnabled(status);
        login_login.setEnabled(status);
        login_help.setEnabled(status);
        login_password.setEnabled(status);
        login_email.setEnabled(status);
        login_remember.setEnabled(status);
    }
}

package net.codejack.autoamp2;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String saved_class;
    private final String TAG = "Main";
    private boolean screen;
    private String last_tag = null;
    public static Context context;

    private static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        context = this;
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        screen = sharedPref.getBoolean("screen", false);
        saved_class = sharedPref.getString("class", null);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            count++;
            replaceScreen(LoginFragment.TAG);
        }
        else {
            if (!screen || count == 0) {
                replaceScreen(LoadScreenFragment.TAG);
                count++;
            }
            else {
                replaceScreen(saved_class);
            }
        }
    }

    protected void passScreen(String name, boolean screen) {
        getSupportFragmentManager().popBackStack();
        setAsActive(name, screen);
    }

    protected void setAsActive(String name, boolean screen) {
        this.saved_class = name;
        this.screen = screen;
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("class", name);
        editor.putBoolean("screen", screen);
        editor.commit();
    }

    private void initToolbar(Toolbar tool) {
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );

        setSupportActionBar(toolbar);
    }

    // routing stuff

    protected void replaceScreen(String fragment) {
        clearScreenVars();
        saved_class = fragment;
        realReplaceScreen(fragment);
    }

    private void realReplaceScreen(String fragment) {
        last_tag = fragment;
        switch (fragment) {
            case LoginFragment.TAG: replaceFragment(LoginFragment.getInstance(), false);
                break;
            case LoadScreenFragment.TAG: replaceFragment(new LoadScreenFragment(), false);
                break;
            case HomeFragment.TAG: replaceFragment(HomeFragment.getInstance(), false);
                break;
            case StudentsFragment.TAG: replaceFragment(new StudentsFragment(), true);
                break;
            case StudentsCreateFragment.TAG: replaceFragment(new StudentsCreateFragment(), true);
                break;
            case StudentsEditFragment.TAG: replaceFragment(new StudentsEditFragment(), true);
                break;
            case LessonsFragment.TAG: replaceFragment(new LessonsFragment(), true);
                break;
            case ScheduleFragment.TAG: replaceFragment(new ScheduleFragment(), true);
                break;
            case ScheduleCreateFragment.TAG: replaceFragment(new ScheduleCreateFragment(), true);
                break;
            case ScheduleEditFragment.TAG: replaceFragment(new ScheduleEditFragment(), true);
                break;
            case ReservationsFragment.TAG: replaceFragment(new ReservationsFragment(), true);
                break;
            case ReservationsViewFragment.TAG: replaceFragment(new ReservationsViewFragment(), true);
                break;
            case RouteTrackingFragment.TAG: replaceFragment(RouteTrackingFragment.getInstance(), true);
                break;
        }
    }

    private void replaceFragment(Fragment frag, boolean back) {
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null){
            setToolbarBack();

            FragmentTransaction t = manager.beginTransaction();
            Fragment currentFrag = manager.findFragmentById(R.id.layout_frame_main);

            if (!back) {
                t.replace(R.id.layout_frame_main, frag).commit();
                screen = false;
            } else {
                if (!currentFrag.getClass().equals(frag.getClass())) {
                    t.replace(R.id.layout_frame_main, frag, last_tag).addToBackStack(null).commit();;
                }
                screen = true;
            }

            writeFragment();
        }
    }

    private void clearScreenVars() {
        saved_class = null;
    }

    private void writeFragment() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("class");
        editor.putBoolean("screen", screen);
        if (saved_class != null) editor.putString("class", saved_class);
        editor.apply();
    }

    protected void setToolbarBack() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getDrawable(R.mipmap.ic_back));
    }

    protected void unsetToolbarBack() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(null);
    }

    protected void setToolbarName(String name) {
        toolbar.setTitle(name);
    }

    protected void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void focusFix(EditText edit) {
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

}

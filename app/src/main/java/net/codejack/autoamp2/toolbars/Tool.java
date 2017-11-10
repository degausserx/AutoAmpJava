package net.codejack.autoamp2.toolbars;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.codejack.autoamp2.R;

/**
 * Created by Degausser on 7/7/2017.
 */

class Tool extends AppCompatActivity {

    private static Tool instance;

    public static Tool getInstance() {
        if (instance == null) instance = new Tool();
        return instance;
    }

    public void initToolbar(Toolbar tool) {
        tool.setTitle(R.string.app_name);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tool.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_back));
    }

}

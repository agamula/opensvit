package ua.opensvit.activities.news;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private View mDrawerFragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerFragmentView = findViewById(R.id.drawer_fragment);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new
                    MainFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
        }

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setupDrawerToggle();

        OpenWorldApi api = new OpenWorldApi();
        VideoStreamApp app = VideoStreamApp.getInstance();
        app.setDbApi(api);
    }

    private void setupDrawerToggle() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, R.string.navigation_drawer_open_default, R.string
                .navigation_drawer_close_default) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce
        // them to the drawer,
        // per the navigation drawer design guidelines.
        // if (!mUserLearnedDrawer && !mFromSavedInstanceState)
        // {
        mDrawerLayout.closeDrawer(mDrawerFragmentView);
        // }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public static void startFragment(FragmentActivity activity, Fragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        //TODO add animation
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

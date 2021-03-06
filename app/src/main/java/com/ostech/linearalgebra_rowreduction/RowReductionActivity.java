package com.ostech.linearalgebra_rowreduction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.*;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.*;

import com.google.android.material.navigation.NavigationView;

public class RowReductionActivity extends AppCompatActivity
                                    implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "RowReductionActivity";

    private DrawerLayout rootLayout;
    private ActionBarDrawerToggle drawerToggler;
    private NavigationView navigationView;

    private Fragment onScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_reduction);

        rootLayout = findViewById(R.id.drawer_layout);
        drawerToggler = new ActionBarDrawerToggle(this, rootLayout, R.string.nav_open,
                                                    R.string.nav_close);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        rootLayout.addDrawerListener(drawerToggler);
        drawerToggler.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchFragment(new RowReductionFragment());
    }   //  end of onCreate()

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggler.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }   //  end of onOptionsItemSelected()

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.row_reduction_menu_item:
                if (!(onScreenFragment instanceof RowReductionFragment)) {
                    switchFragment(new RowReductionFragment());
                }
                break;

            case R.id.simultaneous_menu_item:
                if (!(onScreenFragment instanceof SimultaneousEquationsFragment)) {
                    switchFragment(new SimultaneousEquationsFragment());
                }
                break;

            case R.id.inverse_menu_item:
                if (!(onScreenFragment instanceof InverseFragment)) {
                    switchFragment(new InverseFragment());
                }
                break;

            case R.id.linear_dependency_menu_item:
                if (!(onScreenFragment instanceof LinearDependencyFragment)) {
                    switchFragment(new LinearDependencyFragment());
                }
                break;

            case R.id.rank_menu_item:
                if (!(onScreenFragment instanceof RankFragment)) {
                    switchFragment(new RankFragment());
                }
                break;

            case R.id.help_menu_item:
                if (!(onScreenFragment instanceof HelpFragment)) {
                    switchFragment(new HelpFragment());
                }
                break;

            case R.id.about_menu_item:
                if (!(onScreenFragment instanceof AboutFragment)) {
                    switchFragment(new AboutFragment());
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        rootLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void switchFragment(Fragment fragment) {
        onScreenFragment = fragment;

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                        .replace(R.id.dummy_container, onScreenFragment)
                        .commit();
    }   //  end of switchFragment()

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to close this app?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }   //  end of onBackPressed()
}   //  end of class
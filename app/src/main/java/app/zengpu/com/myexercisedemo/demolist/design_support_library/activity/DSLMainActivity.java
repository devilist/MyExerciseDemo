package app.zengpu.com.myexercisedemo.demolist.design_support_library.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.adapter.DSLMainPagerAdapter;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment.OneFragment;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment.ThreeFragment;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment.TwoFragment;

/**
 * Created by zengpu on 16/8/22.
 */
public class DSLMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewPager viewPager;
//    private DSLMainPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsl_activity_main);

        initView();
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewpager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void setupViewpager(ViewPager viewPager) {
        DSLMainPagerAdapter pagerAdapter = new DSLMainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new OneFragment(),"one");
        pagerAdapter.addFragment(new TwoFragment(),"two");
        pagerAdapter.addFragment(new ThreeFragment(),"three");
        pagerAdapter.addFragment(new TwoFragment(),"four");
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

    }
    private void setupViewpager1(ViewPager viewPager) {
        DSLMainPagerAdapter pagerAdapter = new DSLMainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new TwoFragment(),"二");
        pagerAdapter.addFragment(new OneFragment(),"一");
        pagerAdapter.addFragment(new ThreeFragment(),"三");
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_viewpager0:
                setupViewpager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_viewpager1:
                setupViewpager1(viewPager);
                tabLayout.setupWithViewPager(viewPager);
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_slideshow:
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_manage:
                drawerLayout.closeDrawers();
                return true;
            default:
                return true;
        }
    }
}

package app.zengpu.com.myexercisedemo.demolist.design_support_library.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.adapter.DSLMainPagerAdapter;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment.OneFragment;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment.ThreeFragment;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment.TwoFragment;

/**
 * Created by zengpu on 16/8/22.
 */
public class DSLMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private FloatingActionsMenu action_menu;
    private FloatingActionButton action_a;
    private FloatingActionButton action_b;
    private FloatingActionButton action_c;
    private ImageView layerIv;

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
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewpagerAndTab(viewPager);

        // fab
        action_menu = (FloatingActionsMenu) findViewById(R.id.action_menu);
        action_a = (FloatingActionButton) findViewById(R.id.action_a);
        action_b = (FloatingActionButton) findViewById(R.id.action_b);
        action_c = (FloatingActionButton) findViewById(R.id.action_c);
        action_a.setSize(FloatingActionButton.SIZE_MINI);
        action_b.setSize(FloatingActionButton.SIZE_MINI);
        action_c.setSize(FloatingActionButton.SIZE_MINI);


    }

    private void setupViewpagerAndTab(ViewPager viewPager) {
        DSLMainPagerAdapter pagerAdapter = new DSLMainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new TwoFragment(),"二");
        pagerAdapter.addFragment(new OneFragment(),"一");
        pagerAdapter.addFragment(new ThreeFragment(),"三");
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupFabMenu() {
        action_a.setOnClickListener(this);
        action_b.setOnClickListener(this);
        action_c.setOnClickListener(this);
        // 加号按钮
        for (int i = 0; i < action_menu.getChildCount(); i++) {
            if (action_menu.getChildAt(i) instanceof AddFloatingActionButton) {
                action_menu.getChildAt(i).setOnClickListener(this);
                break;
            }
        }

        layerIv = (ImageView) findViewById(R.id.iv_layer);
        layerIv.setOnClickListener(this);


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
            case R.id.nav_viewpager:
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_viewpager1:
                tabLayout.setupWithViewPager(viewPager);
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_setting:
                DSLSettingsActivity.actionStart(this);
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_manage:
                drawerLayout.closeDrawers();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onClick(View v) {

        // 点击了+按钮
        if (v instanceof AddFloatingActionButton) {
            if (action_menu.isExpanded())
                layerIv.setVisibility(View.GONE);
            else
                layerIv.setVisibility(View.VISIBLE);
//
            action_menu.toggle();
            return;
        }

        switch (v.getId()) {
            case R.id.action_a:
                Toast.makeText(this,"action_a",Toast.LENGTH_SHORT).show();
                layerIv.setVisibility(View.GONE);
                action_menu.collapse();
                break;
            case R.id.action_b:
                Toast.makeText(this,"action_b",Toast.LENGTH_SHORT).show();
                layerIv.setVisibility(View.GONE);
                action_menu.collapse();
                break;
            case R.id.action_c:
                Toast.makeText(this,"action_c",Toast.LENGTH_SHORT).show();
                layerIv.setVisibility(View.GONE);
                action_menu.collapse();
                break;
            case R.id.iv_layer:
                layerIv.setVisibility(View.GONE);
                action_menu.collapse();
                break;

        }

    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DSLMainActivity.class);
        context.startActivity(intent);
    }
}

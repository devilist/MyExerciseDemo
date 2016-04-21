package app.zengpu.com.myexercisedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import app.zengpu.com.myexercisedemo.multi_drawer.MultiDrawerActivity;
import app.zengpu.com.myexercisedemo.pull_refresh_load_0.RefreshAndLoadActivity;
import app.zengpu.com.myexercisedemo.pull_refresh_load_1.GeneralRefreshLoadActivity;
import app.zengpu.com.myexercisedemo.pull_refresh_load_1.RefreshAndLoadBaseActivity;
import app.zengpu.com.myexercisedemo.tupianlunbo0.TuPianLunBoActivity;
import app.zengpu.com.myexercisedemo.tupianlunbo1.ImageLoopActivity;

/**
 * Created by zengpu on 16/3/30.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button tuPianLunBo;
    private Button imageLoop;
    private Button refreshandLoad;
    private Button refreshandLoad1;
    private Button refreshandLoadGeneral;
    private Button multiDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tuPianLunBo = (Button) findViewById(R.id.go_to_tupianlunbo);
        imageLoop = (Button) findViewById(R.id.go_to_image_loop);
        refreshandLoad = (Button) findViewById(R.id.go_to_refresh_load);
        refreshandLoad1 = (Button) findViewById(R.id.go_to_refresh_load1);
        refreshandLoadGeneral = (Button) findViewById(R.id.go_to_refresh_load_general);
        multiDrawer = (Button) findViewById(R.id.go_to_multi_drawer);

        tuPianLunBo.setOnClickListener(this);
        imageLoop.setOnClickListener(this);
        refreshandLoad.setOnClickListener(this);
        refreshandLoad1.setOnClickListener(this);
        refreshandLoadGeneral.setOnClickListener(this);
        multiDrawer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.go_to_tupianlunbo:
                Intent intent = new Intent(this, TuPianLunBoActivity.class);
                startActivity(intent);
                break;

            case R.id.go_to_image_loop:
                Intent intent1 = new Intent(this, ImageLoopActivity.class);
                startActivity(intent1);
                break;
            case R.id.go_to_refresh_load:
                Intent intent2 = new Intent(this, RefreshAndLoadActivity.class);
                startActivity(intent2);
                break;
            case R.id.go_to_refresh_load1:
                Intent intent3 = new Intent(this, RefreshAndLoadBaseActivity.class);
                startActivity(intent3);
                break;
            case R.id.go_to_refresh_load_general:
                Intent intent5 = new Intent(this, GeneralRefreshLoadActivity.class);
                startActivity(intent5);
                break;
            case R.id.go_to_multi_drawer:
                Intent intent4 = new Intent(this, MultiDrawerActivity.class);
                startActivity(intent4);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

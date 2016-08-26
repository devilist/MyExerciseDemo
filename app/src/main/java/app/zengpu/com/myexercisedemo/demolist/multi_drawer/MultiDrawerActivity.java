package app.zengpu.com.myexercisedemo.demolist.multi_drawer;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 16/4/2.
 */
public class MultiDrawerActivity extends BaseActivity {

    private LinearLayout viewUp;
    private LinearLayout viewDown;
    private TextView up;

    private MultiDrawerLayout upDownDrawer;
    private int screenHeight;
    private static final float OFFSET_FACTOR = 0.45f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSwipeBackEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.multi_drawer_layout);
//        mUpDrawerLayout = (UpDrawerLayout) findViewById(R.id.up_drawer_layout);
//        mUpDrawerLayout.openDrawer();
//        slidinglayout1 = (SlidingUpDownLayout1) findViewById(R.id.slidingupdownlayout);
//        slidinglayout = (SlidingLayout) findViewById(R.id.slidingupdownlayout);
        viewUp = (LinearLayout)findViewById(R.id.view_up);
        viewDown =(LinearLayout)findViewById(R.id.view_down);
//        up = (TextView) findViewById(R.id.up);
//        slidinglayout1.setScrollEvent(viewUp);
//        slidinglayout1.setScrollEvent(viewDown);
//        slidinglayout.setScrollEvent(up);

        //屏幕高度   px
//        screenHeight = getResources().getDisplayMetrics().heightPixels;
//
//        upDownDrawer = (MultiDrawerLayout) findViewById(R.id.multi_drawer_layout);
//
//        ViewGroup.MarginLayoutParams  lp =
//                (ViewGroup.MarginLayoutParams)upDownDrawer.getLayoutParams();
//        lp.height = screenHeight;

//        ViewGroup.MarginLayoutParams lp1 = (ViewGroup.MarginLayoutParams)viewUp.getLayoutParams();
//        lp1.height = (int) (screenHeight * OFFSET_FACTOR);
//        ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams)viewDown.getLayoutParams();
////        lp2.height = (int) (screenHeight * OFFSET_FACTOR);

//        viewUp.setLayoutParams(lp1);
//        viewDown.setLayoutParams(lp2);
//        upDownDrawer.setLayoutParams(lp);


//        Log.i("TAG", "lp1.height is :" + lp1.height);
//        Log.i("TAG","viewUp height is :" + viewUp.getHeight());
//        Log.i("TAG","viewdown height is :" + viewDown.getHeight());
//        Log.i("TAG","updowndrawer height is :" + upDownDrawer.getHeight());
//        Log.i("TAG","screenheight  is :" + screenHeight);

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

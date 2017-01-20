package app.zengpu.com.myexercisedemo.demolist.svg_path_anim;

import android.os.Bundle;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2017/1/13.
 */

public class SvgAnimActivity extends BaseActivity {


    private SvgPathView svgPathView0;
    private SvgPathView svgPathView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svgpath);

        initView();
    }

    private void initView() {

        svgPathView0 = (SvgPathView) findViewById(R.id.spv_view0);
        svgPathView0.startAnim();

        svgPathView1 = (SvgPathView) findViewById(R.id.spv_view1);
        svgPathView1.setSvgPathString(getString(R.string.poems_hhl))
                .setStrokeAnimDuration(5000)
                .setStrokeColor(0xff757575)
                .setStrokeWidth(1.0f)
                .setAnimDelay(500)
                .setNeedFill(true)
                .setFillColor(0xff616161)
                .setFillAnimDuration(3000)
                .startAnim();

    }
}

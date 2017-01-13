package app.zengpu.com.myexercisedemo.demolist.svg_path_anim;

import android.os.Bundle;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2017/1/13.
 */

public class SvgAnimActivity extends BaseActivity {


    private SvgPathView svgPathView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svgpath);

        initView();

    }

    private void initView() {

        svgPathView = (SvgPathView) findViewById(R.id.spv_view);
        svgPathView.setSvgPathString(getString(R.string.poems_hhl));

//        SvgPathParser svgPathParser = new SvgPathParser();
//
//        try {
//            Path path = svgPathParser.parsePath(getString(R.string.poems_hhl));
//            SvgPath svgPath = new SvgPath(path);
//            svgPathView.setSvgPath(svgPath);
//            svgPathView.startAnim();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }
}

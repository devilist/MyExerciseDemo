package app.zengpu.com.myexercisedemo.demolist.vertical_textview;

import android.os.Bundle;
import android.text.Html;
import android.widget.HorizontalScrollView;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.selected_textview.StringContentUtil;

/**
 * Created by zengpu on 2017/1/20.
 */

public class VerticalTextViewActivity extends BaseActivity {

    private VerticalTextView vtv_text_rtl, vtv_text_ltr;
    private HorizontalScrollView scroll_rtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_textview);
        init();
    }

    private void init() {
        scroll_rtl = (HorizontalScrollView) findViewById(R.id.scroll_rtl);
        vtv_text_rtl = (VerticalTextView) findViewById(R.id.vtv_text_rtl);
        vtv_text_ltr = (VerticalTextView) findViewById(R.id.vtv_text_ltr);


        vtv_text_rtl.setText(Html.fromHtml(StringContentUtil.str_hanzi).toString());
        vtv_text_ltr.setText(Html.fromHtml(StringContentUtil.str_hanzi).toString());


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // 滚到右侧
        scroll_rtl.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }
}

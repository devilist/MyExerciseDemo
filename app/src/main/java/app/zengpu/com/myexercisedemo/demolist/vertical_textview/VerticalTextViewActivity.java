package app.zengpu.com.myexercisedemo.demolist.vertical_textview;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

        vtv_text_ltr.setLeftToRight(false)
                .setLineSpacingExtra(10)
                .setCharSpacingExtra(2)
                .setUnderLineText(true)
                .setShowActionMenu(true)
                .setUnderLineColor(Color.BLUE)
                .setUnderLineWidth(2.0f)
                .setUnderLineOffset(3)
                .setTextHighlightColor(Color.RED);
        vtv_text_ltr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerticalTextViewActivity.this, "onClick事件", Toast.LENGTH_SHORT).show();
            }
        });
        vtv_text_ltr.setCustomActionMenuCallBack(new VerticalTextView.CustomActionMenuCallBack() {
            @Override
            public boolean onCreateCustomActionMenu(VerticalTextView.ActionMenu menu) {
                List<String> titleList = new ArrayList<>();
                titleList.add("翻译");
                titleList.add("分享");
                menu.addCustomMenuItem(titleList);
                return false;
            }

            @Override
            public void onCustomActionItemClicked(String itemTitle, String selectedContent) {
                Toast.makeText(VerticalTextViewActivity.this, "ActionMenu: " + itemTitle, Toast.LENGTH_SHORT).show();
            }
        });

    }
}

package app.zengpu.com.myexercisedemo.demolist.vertical_textview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.selected_textview.StringContentUtil;

/**
 * Created by zengpu on 2017/1/20.
 */

public class VerticalTextViewActivity extends AppCompatActivity {

    private VerticalTextView vtv_text_rtl, vtv_text_ltr;
//    private HorizontalScrollView scroll_rtl;

    private TextView tv_cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_textview);
        init();
    }

    private void init() {
//        scroll_rtl = (HorizontalScrollView) findViewById(R.id.scroll_rtl);
        vtv_text_rtl = (VerticalTextView) findViewById(R.id.vtv_text_rtl);
        vtv_text_ltr = (VerticalTextView) findViewById(R.id.vtv_text_ltr);

        tv_cover = (TextView) findViewById(R.id.tv_cover);

        vtv_text_rtl.setText(StringContentUtil.str_juaner);
        vtv_text_ltr.setText(Html.fromHtml(StringContentUtil.str_cbf).toString());

        vtv_text_ltr.setLeftToRight(false)
                .setLineSpacingExtra(10)
                .setCharSpacingExtra(2)
                .setUnderLineText(true)
                .setShowActionMenu(true)
                .setUnderLineColor(0xffCEAD53)
                .setUnderLineWidth(2.5f)
                .setUnderLineOffset(3)
                .setTextHighlightColor(0xffCEAD53);
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

        tv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerticalTextViewActivity.this, "cover onClick事件", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

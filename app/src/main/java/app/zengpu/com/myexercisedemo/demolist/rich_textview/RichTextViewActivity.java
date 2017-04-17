package app.zengpu.com.myexercisedemo.demolist.rich_textview;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

/**
 * Created by tao on 2017/4/17.
 */

public class RichTextViewActivity extends BaseActivity {

    RichTextView rtv_0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_textview);

        initRichTextview();
    }

    private void initRichTextview() {
        rtv_0 = (RichTextView) findViewById(R.id.rtv_0);
        rtv_0.setOnPrefixClickListener(new RichTextView.OnPrefixClickListener() {
            @Override
            public void onPrefixClick(View v, String text) {
                Toast.makeText(RichTextViewActivity.this, "点击了前缀区域", Toast.LENGTH_SHORT).show();
            }

//            @Override
//            public void onTextClick() {
//                Toast.makeText(getContext(), "点击了文字区域", Toast.LENGTH_SHORT).show();
//            }
        });
        rtv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RichTextViewActivity.this, "点击了文字区域", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

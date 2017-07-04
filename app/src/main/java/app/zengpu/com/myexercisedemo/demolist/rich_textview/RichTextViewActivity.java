package app.zengpu.com.myexercisedemo.demolist.rich_textview;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.activity.DSLFullscreenActivity;

/**
 * Created by tao on 2017/4/17.
 */

public class RichTextViewActivity extends BaseActivity {

    RichTextView0 rtv_0;
    private RichTextView tv_html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_textview);

        initRichTextview();
    }

    private void initRichTextview() {
        rtv_0 = (RichTextView0) findViewById(R.id.rtv_0);
        rtv_0.setOnPrefixClickListener(new RichTextView0.OnPrefixClickListener() {
            @Override
            public void onPrefixClick(View v, String text) {
                Toast.makeText(RichTextViewActivity.this, "点击了前缀区域", Toast.LENGTH_SHORT).show();
            }
        });
        rtv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RichTextViewActivity.this, "点击了文字区域", Toast.LENGTH_SHORT).show();
            }
        });

        tv_html = (RichTextView) findViewById(R.id.tv_html);
        String html = "<a href='1'>#一个人看电影#</a> 哈哈，这个电影有意思吧。混淆成功后，" +
                "除生成了指定类型的混淆包外，还会在工程的根目录下或是根目录下得bin文件夹中生成" +
                "proguard文件夹，里面包含dump.txt、mapping.txt、seeds.txt四个文件..."
                + "<a href='2'>全部</a>";
        CharSequence charSequence = Html.fromHtml(html);
        tv_html.setText(html);
        tv_html.setOnHyperTextClickListener(new RichTextView.OnHyperTextClickListener() {
            @Override
            public void HyperTextClick(View v, String url, String description) {
                LogUtil.d("HyperTextClick", "url " + url + " description " + description);
                DSLFullscreenActivity.actionStart(RichTextViewActivity.this);
            }
        });
//        SpannableStringBuilder builder = new SpannableStringBuilder(charSequence);
//        URLSpan[] urls = builder.getSpans(0, charSequence.length(), URLSpan.class);
//        for (final URLSpan span : urls) {
//            final int start = builder.getSpanStart(span);
//            final int end = builder.getSpanEnd(span);
//            int flag = builder.getSpanFlags(span);
//            final String link = span.getURL();
//            String des = span.toString();
//
//
//            builder.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    LogUtil.d("RichTextViewActivity", "url " + link + " start " + start + " end " + end);
//                    widget.postInvalidate();
//                    DSLFullscreenActivity.actionStart(RichTextViewActivity.this);
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setColor(Color.parseColor("#f26d85"));
//
//                    ds.setUnderlineText(false);
//                }
//            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.removeSpan(span);
//        }
//
//        tv_html.setLinksClickable(true);
//        tv_html.setMovementMethod(LinkMovementMethod.getInstance());
//        tv_html.setText(builder);
    }
}

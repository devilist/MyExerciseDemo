package app.zengpu.com.myexercisedemo.demolist.rich_textview;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private Button btn_add_prefix;
    private EditText et_prefix;
    private PrefixEditText pet_text;
    private TextView tv_left_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_textview);

        initRichTextview();
        initPrefixEdittext();
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
    }

    private void initPrefixEdittext() {
        btn_add_prefix = (Button) findViewById(R.id.btn_add_prefix);
        et_prefix = (EditText) findViewById(R.id.et_prefix);
        pet_text = (PrefixEditText) findViewById(R.id.pet_text);
        tv_left_count = (TextView) findViewById(R.id.tv_left_count);
        pet_text.setLeftTextCountView(tv_left_count);

        btn_add_prefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pet_text.addPrefixText(et_prefix.getText().toString());
            }
        });
    }
}

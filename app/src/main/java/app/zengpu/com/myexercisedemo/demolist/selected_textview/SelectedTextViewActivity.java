package app.zengpu.com.myexercisedemo.demolist.selected_textview;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengpu on 2016/11/18.
 */

public class SelectedTextViewActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener,
        SelectableTextView.CustomActionMenuCallBack {

    private RadioGroup rg_text_gravity;
    private RadioGroup rg_text_content;
    String s;

    private SelectableTextView selectableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectedtextview);

        openDictDataBase();

        initView();

    }

    private void initView() {
        s = "<p>　　金溪民方仲永，世隶耕。仲永生五年，未尝识书具，\n" +
                "            忽啼求之。父异焉，借旁近与之，即书诗四句，并自为其名。\n" +
                "            其诗以养父母、收族为意，传一乡秀才观之。自是指物作诗立就，\n" +
                "            其文理皆有可观者。邑人奇之，稍稍宾客其父，或以钱币乞之。\n" +
                "            父利其然也，日扳仲永环谒于邑人，不使学。</p>\n" +
                "        <p>　　余闻之也久。明道中，从先人还家，于舅家见之，十二三矣。令作诗，\n" +
                "        不能称前时之闻。又七年，还自扬州，复到舅家问焉。曰：“泯然众人矣。”</p>\n" +
                "        <p>    王子曰：仲永之通悟，受之天也。其受之天也，贤于材人远矣。卒之为众人，\n" +
                "        则其受于人者不至也。彼其受之天也，如此其贤也，不受之人，且为众人；今夫不受之天，\n" +
                "            固众人，又不受之人，得为众人而已耶？</p>";

        s = Html.fromHtml(s).toString();

        selectableTextView = (SelectableTextView) findViewById(R.id.ctv_content);
        selectableTextView.setText(Html.fromHtml(StringContentUtil.str_hanzi).toString());
        selectableTextView.clearFocus();
        selectableTextView.setCustomActionMenuCallBack(this);
        selectableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectedTextViewActivity.this, "SelectableTextView 的onClick事件", Toast.LENGTH_SHORT).show();
            }
        });

        rg_text_gravity = (RadioGroup) findViewById(R.id.rg_text_gravity);
        rg_text_content = (RadioGroup) findViewById(R.id.rg_text_content);
        ((RadioButton)findViewById(R.id.rb_justify)).setChecked(true);
        ((RadioButton)findViewById(R.id.rb_hanzi)).setChecked(true);
        rg_text_gravity.setOnCheckedChangeListener(this);
        rg_text_content.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_justify:
                selectableTextView.setTextJustify(true);
                selectableTextView.postInvalidate();
                break;
            case R.id.rb_left:
                selectableTextView.setTextJustify(false);
                selectableTextView.postInvalidate();
                break;
            case R.id.rb_hanzi:
                selectableTextView.setText(Html.fromHtml(StringContentUtil.str_hanzi).toString());
                selectableTextView.postInvalidate();
                break;
            case R.id.rb_en:
                selectableTextView.setText(Html.fromHtml(StringContentUtil.str_en).toString());
                selectableTextView.postInvalidate();
                break;
            case R.id.rb_muti:
                selectableTextView.setText(Html.fromHtml(StringContentUtil.str_muti).toString());
                selectableTextView.postInvalidate();
                break;
        }

    }

    @Override
    public boolean onCreateCustomActionMenu(SelectableTextView.ActionMenu menu) {
        menu.setActionMenuBgColor(0xff666666);
        menu.setMenuItemTextColor(0xffffffff);
        List<String> titleList = new ArrayList<>();
        titleList.add("翻译");
        titleList.add("分享");
        titleList.add("分享");
        menu.addCustomMenuItem(titleList);
        return false;
    }

    @Override
    public void onCustomActionItemClicked(String itemTitle, String selectedContent) {
        if (itemTitle.equals("翻译")) {
            // 翻译
            Map<String, String> result = assetsDataBaseHelper.queryHanzi(String.valueOf(selectedContent.charAt(0)));
            if (null != result)
                LogUtil.d("SelectedTextViewActivity", String.valueOf(selectedContent.charAt(0)) + ": \n "
                        + result.get(String.valueOf(selectedContent.charAt(0))));
        }
    }

    @Override
    protected void onDestroy() {
        closeDictDataBase();
        super.onDestroy();
    }
}

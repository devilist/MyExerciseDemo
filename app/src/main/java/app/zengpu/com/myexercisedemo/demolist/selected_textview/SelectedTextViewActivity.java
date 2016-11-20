package app.zengpu.com.myexercisedemo.demolist.selected_textview;

import android.os.Bundle;
import android.text.Html;
import android.view.View;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2016/11/18.
 */

public class SelectedTextViewActivity extends BaseActivity {

    private SelectedTextView selectedTextView;
    private SelectableTextView selectableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectedtextview);

        initView();

    }

    private void initView() {
        selectedTextView = (SelectedTextView) findViewById(R.id.stv_content);
        selectableTextView = (SelectableTextView) findViewById(R.id.ctv_content);

        String s = "<p>　　金溪民方仲永，世隶耕。仲永生五年，未尝识书具，\n" +
                "            忽啼求之。父异焉，借旁近与之，即书诗四句，并自为其名。\n" +
                "            其诗以养父母、收族为意，传一乡秀才观之。自是指物作诗立就，\n" +
                "            其文理皆有可观者。邑人奇之，稍稍宾客其父，或以钱币乞之。\n" +
                "            父利其然也，日扳仲永环谒于邑人，不使学。</p>\n" +
                "        <p>　　余闻之也久。明道中，从先人还家，于舅家见之，十二三矣。令作诗，\n" +
                "        不能称前时之闻。又七年，还自扬州，复到舅家问焉。曰：“泯然众人矣。”</p>\n" +
                "        <p>    王子曰：仲永之通悟，受之天也。其受之天也，贤于材人远矣。卒之为众人，\n" +
                "        则其受于人者不至也。彼其受之天也，如此其贤也，不受之人，且为众人；今夫不受之天，\n" +
                "            固众人，又不受之人，得为众人而已耶？</p>";

        String c = Html.fromHtml(s).toString();

        selectableTextView.setText(c + c);
        selectableTextView.clearFocus();

        selectableTextView.setOnContextMenuClickListener(new SelectableTextView.OnContextMenuClickListener() {
            @Override
            public void onMenuItemClick(View v, int type, String selectedText) {

            }
        });

//        selectedTextView.showWebFont(getResources().getString(R.string.large_text0), 16, "#ff5185", "#ffffff");
//        selectedTextView.showWebFont(c, 16, "#ff5185", "#ffffff");
//        selectedTextView.showWebFont(s, 16, "#ff5185", "#ffffff");
    }
}

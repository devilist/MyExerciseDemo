package app.zengpu.com.myexercisedemo.demolist.selected_textview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2016/11/18.
 */

public class SelectedTextView extends WebView {
    public SelectedTextView(Context context) {
        this(context, null);
    }

    public SelectedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        WebSettings setting = getSettings();
//        setting.setJavaScriptEnabled(true);
//
//        //设置自适应
//        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL.SINGLE_COLUMN);
//        setting.setDefaultTextEncodingName("UTF-8");

    }

    /**
     * 显示文字
     *
     * @param content         显示的文字
     * @param fontSize        文字大小
     * @param fontColor       文字颜色
     * @param backgroundColor 显示文字的背景
     */
    public void showWebFont(String content, int fontSize, String fontColor, String backgroundColor) {

        String format_content = "<![CDATA[" +
                "<html><head></head>" +
                "<body " +
                "style=\"background-color:" + backgroundColor +
                ";text-align:justify" +
                ";text-indent:0em" +
                ";font-size:" + fontSize + "px" +
                ";color:" + fontColor +
                ";line-height:" + 2.0f + "\"" +
                ">" +
                content +
                "</body>" +
                "</html>";

        this.loadDataWithBaseURL(null, format_content, "text/html", "utf-8", null);

    }


    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {

        CustomizedSelectActionModeCallback actionModeCallback = new CustomizedSelectActionModeCallback();

        return super.startActionMode(actionModeCallback);
    }




    static class CustomizedSelectActionModeCallback implements ActionMode.Callback {

//        private ActionMode.Callback callback;
//
//        public CustomizedSelectActionModeCallback(ActionMode.Callback callback) {
//            this.callback = callback;
//        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("查询");
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.menu_activity_main,menu);
            return true;
//            return callback.onCreateActionMode(mode,menu);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Remove the "select all" option
            menu.removeItem(android.R.id.selectAll);
            // Remove the "cut" option
            menu.removeItem(android.R.id.copy);
            // Remove the "copy all" option
            menu.removeItem(android.R.id.redo);
            menu.removeItem(android.R.id.undo);
            menu.removeItem(android.R.id.cut);
            menu.removeItem(android.R.id.paste);
            menu.removeItem(android.R.id.shareText);
            menu.removeItem(android.R.id.pasteAsPlainText);
            menu.removeItem(android.R.id.pasteAsPlainText);

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if (!item.getTitle().toString().contains("查询"))
                    item.setVisible(false);
            }

            return true;

//            return callback.onPrepareActionMode(mode,menu);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
//            return callback.onActionItemClicked(mode, item);
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            callback.onDestroyActionMode(mode);
        }
    }

}

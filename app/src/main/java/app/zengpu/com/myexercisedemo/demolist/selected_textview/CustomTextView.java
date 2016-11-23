package app.zengpu.com.myexercisedemo.demolist.selected_textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.text.Layout;
import android.text.Selection;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by zengpu on 2016/11/23.
 */

public class CustomTextView extends EditText {

    private final int TRIGGER_LONGPRESS_TIME_THRESHOLD = 300; // 触发长按事件的时间阈值
    private final int TRIGGER_LONGPRESS_DISTANCE_THRESHOLD = 10; // 触发长按事件的位移阈值

    private Context mContext;
    private int mScreenHeight;  // 屏幕高度
    private int mStatusBarHeight; // 状态栏高度
    private int mPopWindowHeight; // 弹出菜单高度

    private float mTouchDownX = 0;
    private float mTouchDownY = 0;
    private float mTouchDownRawY = 0;
    private int mWordStartOffset; //action_down触摸事件 字符串开始位置的偏移值

    private boolean isLongPress = false; // 是否触发了长按事件
    private boolean isVibrator = false;  // 是否触发过长按震动

    private Vibrator mVibrator;

    private PopupWindow mContextMenuPopupWindow; // 长按弹出菜单

    private CustomActionMenuCallBack mCustomActionMenuCallBack;

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        mStatusBarHeight = getStatusBarHeight(mContext);
        mPopWindowHeight = dip2px(mContext, 45);

        mVibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);

        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
        setTextIsSelectable(true);
        setCursorVisible(false);
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Layout layout = getLayout();
        int currentLine = 0; // 当前所在行

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.d("SelectableTextView", "ACTION_DOWN");
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mTouchDownRawY = event.getRawY();
                isLongPress = false;
                isVibrator = false;

                currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                mWordStartOffset = layout.getOffsetForHorizontal(currentLine, (int) event.getX());
                Selection.setSelection(getEditableText(), mWordStartOffset);
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d("SelectableTextView", "ACTION_MOVE");
                // 判断是否触发长按事件
                if (event.getEventTime() - event.getDownTime() >= TRIGGER_LONGPRESS_TIME_THRESHOLD
                        && Math.abs(event.getX() - mTouchDownX) < TRIGGER_LONGPRESS_DISTANCE_THRESHOLD
                        && Math.abs(event.getY() - mTouchDownY) < TRIGGER_LONGPRESS_DISTANCE_THRESHOLD) {
                    LogUtil.d("SelectableTextView", "ACTION_MOVE 长按");
                    isLongPress = true;
                    // 每次触发长按时，震动提示一次
                    if (!isVibrator) {
                        mVibrator.vibrate(30);
                        isVibrator = true;
                    }
                }
                if (isLongPress) {
                    // 手指移动过程中的字符偏移
                    currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    int mWordOffset_move = layout.getOffsetForHorizontal(currentLine, (int) event.getX());
                    // 通知父布局不要拦截触摸事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                    // 选择字符
                    Selection.setSelection(getEditableText(), mWordStartOffset, mWordOffset_move);
                }

                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d("SelectableTextView", "ACTION_UP");
                if (isLongPress) {
                    currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    int mWordOffsetEnd = layout.getOffsetForHorizontal(currentLine, (int) event.getX());
                    // 至少选中一个字符
                    if (mWordOffsetEnd == mWordStartOffset)
                        mWordOffsetEnd += 1;
                    Selection.setSelection(getEditableText(), mWordStartOffset, mWordOffsetEnd);

                    // 计算菜单显示位置
                    int mPopWindowOffsetY = calculatorPopWindowYPosition((int) mTouchDownRawY, (int) event.getRawY());
                    // 弹出菜单
                    showContextActionMenu(mPopWindowOffsetY);
                }
                // 通知父布局继续拦截触摸事件
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    private int mViewWidth; // TextView的总宽度
    private int mLineY; // 行高

    private boolean isDraw = false;

    @Override
    protected void onDraw(Canvas canvas) {

        Paint mHighlightPaint = new Paint();
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(getHighlightColor());
        mHighlightPaint.setAntiAlias(true);
        if (!isDraw) {
            int h = 48;
            RectF rect_all = new RectF(0, h, mViewWidth, 6 * h);
            RectF rect_lt = new RectF(0, h, mViewWidth / 2, 2 * h);
            RectF rect_rb = new RectF(mViewWidth / 2, 5 * h, mViewWidth, 6 * h);

            Path path_all = new Path();
            path_all.addRect(rect_all, Path.Direction.CCW);
            Path path_lt = new Path();
            path_lt.addRect(rect_lt, Path.Direction.CCW);
            Path path_rb = new Path();
            path_rb.addRect(rect_rb, Path.Direction.CCW);

            path_all.op(path_lt, Path.Op.DIFFERENCE);
            path_all.op(path_rb, Path.Op.DIFFERENCE);

//        Path path = new Path();
//        path.moveTo(300, 500);
//        path.lineTo(300, 400);
//        path.lineTo(600, 400);
//        path.lineTo(600, 800);
//        path.lineTo(100, 800);
//        path.lineTo(100, 500);
//        path.lineTo(300, 500);

            canvas.drawPath(path_all, mHighlightPaint);
            isDraw = true;
        }


        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        mViewWidth = getMeasuredWidth();//拿到textview的实际宽度
        String text = getText().toString();
        mLineY = 0;
        mLineY += getTextSize();
        Layout layout = getLayout();
        for (int i = 0; i < layout.getLineCount(); i++) {//每行循环
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String line = text.substring(lineStart, lineEnd);//获取到TextView每行中的内容
            float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());

            if (i < 5) {
//                LogUtil.d("onDraw", "textview :" + mViewWidth);
                LogUtil.d("onDraw", "line " + i + " width :" + width);
            }

            if (needScale(line)) {
                if (i == layout.getLineCount() - 1) {//最后一行不需要重绘
                    canvas.drawText(line, 0, mLineY, paint);
                } else {
                    drawScaleText(canvas, lineStart, line, width);
                }
            } else {
                canvas.drawText(line, 0, mLineY, paint);
                setHintTextColor(Color.parseColor("#26CEAD53"));
            }
            mLineY += getLineHeight();//写完一行以后，高度增加一行的高度
            System.out.println("lineHeight---" + getLineHeight());
        }
    }

    /**
     * 重绘此行
     *
     * @param canvas    画布
     * @param lineStart 行头
     * @param line      该行所有的文字
     * @param lineWidth 该行每个文字的宽度的总和
     */
    private void drawScaleText(Canvas canvas, int lineStart, String line,
                               float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, mLineY, getPaint());// 以 (x, mLineY) 为起点，画出blanks
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());// 画出一个空格需要的宽度
            x += bw;
            line = line.substring(3);
        }
        // 比如说一共有5个字，中间隔了4个空儿，
        //	那就用整个TextView的宽度 - 这5个字的宽度，
        //然后除以4，填补到这4个空儿中
        float d = (mViewWidth - lineWidth) / (line.length() - 1);

        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    /**
     * 判断是不是段落的第一行。
     * 一个汉字相当于一个字符，此处判断是否为第一行的依据是：
     * 字符长度大于3且前两个字符为空格
     *
     * @param lineStart
     * @param line
     * @return
     */
    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' '
                && line.charAt(1) == ' ';
    }


    /**
     * 判断需不需要缩放
     * 该行最后一个字符不是换行符的时候返回true，
     * 该行最后一个字符是换行符的时候返回false
     *
     * @param line
     * @return
     */
    private boolean needScale(String line) {
        if (line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';//该行最后一个字符不是换行符的时候返回true，是换行符的时候返回false
        }
    }


    /**
     * 长按弹出菜单
     *
     * @param offsetY
     */
    private void showContextActionMenu(int offsetY) {
        // 创建菜单
        ActionMenu actionMenu = new ActionMenu(mContext);
        // 是否需要移除默认item
        boolean isRemoveDefaultItem = false;
        if (null != mCustomActionMenuCallBack) {
            isRemoveDefaultItem = mCustomActionMenuCallBack.onCreateCustomActionMenu(actionMenu);
        }
        if (!isRemoveDefaultItem)
            actionMenu.addDefaultMenuItem(); // 添加默认item

        actionMenu.addCustomItem();  // 添加自定义item
        actionMenu.setFocusable(true); // 获取焦点
        actionMenu.setFocusableInTouchMode(true);
        // item监听
        actionMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);

        mContextMenuPopupWindow = new PopupWindow(actionMenu, WindowManager.LayoutParams.WRAP_CONTENT,
                mPopWindowHeight, true);
        mContextMenuPopupWindow.setFocusable(true);
        mContextMenuPopupWindow.setOutsideTouchable(false);
        mContextMenuPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mContextMenuPopupWindow.showAtLocation(this, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, offsetY);

        mContextMenuPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Selection.removeSelection(getEditableText());
            }
        });
    }

    /**
     * 隐藏菜单
     */
    private void hideContextActionMenu() {
        if (null != mContextMenuPopupWindow) {
            mContextMenuPopupWindow.dismiss();
            mContextMenuPopupWindow = null;
        }
    }

    /**
     * 菜单点击事件监听
     */
    private ActionMenu.OnMenuItemClickListener mOnMenuItemClickListener = new ActionMenu.OnMenuItemClickListener() {
        @Override
        public void onMenuItemClick(String menuItemTitle) {
            // 选中的字符的开始和结束位置
            int start = getSelectionStart();
            int end = getSelectionEnd();
            // 获得选中的字符
            String selected_str;
            if (start < 0 || end < 0 || end <= start) {
                selected_str = "";
            } else
                selected_str = getText().toString().substring(start, end);

            if (menuItemTitle.equals(ActionMenu.DEFAULT_MENU_ITEM_TITLE_SELECT_ALL)) {
                //全选事件
                Selection.selectAll(getEditableText());

            } else if (menuItemTitle.equals(ActionMenu.DEFAULT_MENU_ITEM_TITLE_COPY)) {
                // 复制事件
                copy(mContext, selected_str);
                Toast.makeText(mContext, "复制成功！", Toast.LENGTH_SHORT).show();
                hideContextActionMenu();

            } else {
                // 自定义事件
                if (null != mCustomActionMenuCallBack) {
                    mCustomActionMenuCallBack.onCustomActionItemClicked(menuItemTitle, selected_str);
                }
                hideContextActionMenu();
            }
        }
    };

    /**
     * 计算弹出菜单相对于父布局的Y向偏移
     *
     * @param yOffsetStart 所选字符的起始位置相对屏幕的Y向偏移
     * @param yOffsetEnd   所选字符的结束位置相对屏幕的Y向偏移
     * @return
     */
    private int calculatorPopWindowYPosition(int yOffsetStart, int yOffsetEnd) {
        if (yOffsetStart > yOffsetEnd) {
            int temp = yOffsetStart;
            yOffsetStart = yOffsetEnd;
            yOffsetEnd = temp;
        }
        int actionMenuOffsetY;

        if (yOffsetStart < mPopWindowHeight * 3 / 2 + mStatusBarHeight) {
            if (yOffsetEnd > mScreenHeight - mPopWindowHeight * 3 / 2) {
                // 菜单显示在屏幕中间
                actionMenuOffsetY = mScreenHeight / 2 - mPopWindowHeight / 2;
            } else {
                // 菜单显示所选文字下方
                actionMenuOffsetY = yOffsetEnd + mPopWindowHeight / 2;
            }
        } else {
            // 菜单显示所选文字上方
            actionMenuOffsetY = yOffsetStart - mPopWindowHeight * 3 / 2;
        }
        return actionMenuOffsetY;
    }

    /**
     * 实现文本复制功能
     *
     * @param text
     */
    public static void copy(Context context, String text) {
        // 得到剪贴板管理器
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(text.trim());
        } else {
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(text.trim());
        }
    }

    /**
     * dp2px
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 状态栏高度
     *
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            statusBarHeight = 0;
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 设置ActionMenu菜单内容监听
     *
     * @param callBack
     */
    public void setCustomActionMenuCallBack(CustomActionMenuCallBack callBack) {
        this.mCustomActionMenuCallBack = callBack;
    }

    /**
     * ActionMenu菜单内容监听
     */
    public interface CustomActionMenuCallBack {
        /**
         * 创建ActionMenu菜单。
         * 返回值false，保留默认菜单；返回值true，移除默认菜单
         *
         * @param menu
         * @return 返回false，保留默认菜单；返回true，移除默认菜单
         */
        boolean onCreateCustomActionMenu(ActionMenu menu);

        /**
         * ActionMenu菜单的点击事件
         *
         * @param itemTitle       ActionMenu菜单item的title
         * @param selectedContent 选择的文字
         */
        void onCustomActionItemClicked(String itemTitle, String selectedContent);

    }

    /**
     * 触发长按事件后弹出的ActionMenu菜单
     */
    static class ActionMenu extends LinearLayout {

        private static final String DEFAULT_MENU_ITEM_TITLE_SELECT_ALL = "全选";
        private static final String DEFAULT_MENU_ITEM_TITLE_COPY = "复制";

        private Context mContext;
        private int mMenuItemMargin;
        private int mActionMenuBgColor = 0xff666666; // ActionMenu背景色
        private int mMenuItemTextColor = 0xffffffff; // MenuItem字体颜色
        private List<String> mItemTitleList; // MenuItem 标题
        private ActionMenu.OnMenuItemClickListener mOnMenuItemClickListener;

        public ActionMenu(Context context) {
            this(context, null);
        }

        public ActionMenu(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public ActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.mContext = context;
            init();
        }

        private void init() {
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
            setLayoutParams(params);
            setPadding(15, 0, 15, 0);
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER);
            setActionMenuBackGround(mActionMenuBgColor);
            mMenuItemMargin = 20;
        }

        /**
         * 设置ActionMenu背景
         */
        private void setActionMenuBackGround(int menuBgColor) {
            GradientDrawable gd = new GradientDrawable();//创建drawable
            gd.setColor(menuBgColor);
            gd.setCornerRadius(8);
            setBackground(gd);
        }

        /**
         * 添加默认MenuItem（全选，复制）
         */
        private void addDefaultMenuItem() {
            View item_select_all = createMenuItem(DEFAULT_MENU_ITEM_TITLE_SELECT_ALL);
            View item_copy = createMenuItem(DEFAULT_MENU_ITEM_TITLE_COPY);
            addView(item_select_all);
            addView(item_copy);
            invalidate();
        }

        /**
         * 移除默认MenuItem
         */
        private void removeDefaultMenuItem() {
            if (getChildCount() == 0)
                return;

            View selAllItem = findViewWithTag(DEFAULT_MENU_ITEM_TITLE_SELECT_ALL);
            View copyItem = findViewWithTag(DEFAULT_MENU_ITEM_TITLE_COPY);

            if (null != selAllItem)
                removeView(selAllItem);
            if (null != copyItem)
                removeView(copyItem);
            invalidate();
        }

        /**
         * 添加自定义MenuItem标题
         *
         * @param itemTitleList MenuItem标题
         */
        public void addCustomMenuItem(List<String> itemTitleList) {
            this.mItemTitleList = itemTitleList;
        }

        /**
         * 添加自定义MenuItem
         */
        private void addCustomItem() {
            if (null == mItemTitleList || (null != mItemTitleList && mItemTitleList.size() == 0))
                return;
            // 去重
            List<String> list = new ArrayList();
            for (Iterator it = mItemTitleList.iterator(); it.hasNext(); ) {
                String title = (String) it.next();
                if (!list.contains(title))
                    list.add(title);
            }

            for (int i = 0; i < list.size(); i++) {
                final View menuItem = createMenuItem(list.get(i));
                addView(menuItem);
            }
            invalidate();
        }

        /**
         * 创建MenuItem
         */
        private View createMenuItem(final String itemTitle) {
            final TextView menuItem = new TextView(mContext);
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.leftMargin = params.rightMargin = mMenuItemMargin;
            menuItem.setLayoutParams(params);

            menuItem.setTextSize(14);
            menuItem.setTextColor(mMenuItemTextColor);
            menuItem.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            menuItem.setGravity(Gravity.CENTER);
            menuItem.setText(itemTitle);
            menuItem.setTag(itemTitle);

            menuItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnMenuItemClickListener)
                        mOnMenuItemClickListener.onMenuItemClick(itemTitle);
                }
            });
            return menuItem;
        }

        /**
         * 设置MenuItem文字颜色
         *
         * @param mItemTextColor
         */
        public void setMenuItemTextColor(int mItemTextColor) {
            this.mMenuItemTextColor = mItemTextColor;
        }

        /**
         * 设置ActionMenu背景色
         *
         * @param mMenuBgColor
         */
        public void setActionMenuBgColor(int mMenuBgColor) {
            this.mActionMenuBgColor = mMenuBgColor;
            setActionMenuBackGround(this.mActionMenuBgColor);
        }

        /**
         * 设置MenuItem点击事件
         *
         * @param listener
         * @hiden
         */
        private void setOnMenuItemClickListener(ActionMenu.OnMenuItemClickListener listener) {
            this.mOnMenuItemClickListener = listener;
        }

        /**
         * MenuItem点击事件监听
         */
        public interface OnMenuItemClickListener {

            /**
             * @param menuItemTitle MenuItem的标题
             */
            void onMenuItemClick(String menuItemTitle);

        }
    }
}

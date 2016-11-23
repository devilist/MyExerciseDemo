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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * SelectableTextView 自定义了触发长按事件后弹出的ActionMenu菜单。
 * <p>
 * <p>可以根据需求通过实现CustomActionMenuCallBack接口，
 * <p>在onCreateCustomActionMenu()方法里创建自定义菜单，
 * <p>在onCustomActionItemClicked()方法里监听点击事件
 * <p>
 * Created by zengpu on 2016/11/20.
 */
public class SelectableTextView extends EditText {

    private final int TRIGGER_LONGPRESS_TIME_THRESHOLD = 300; // 触发长按事件的时间阈值
    private final int TRIGGER_LONGPRESS_DISTANCE_THRESHOLD = 10; // 触发长按事件的位移阈值

    private Context mContext;
    private int mScreenHeight;  // 屏幕高度
    private int mStatusBarHeight; // 状态栏高度
    private int mPopWindowHeight; // 弹出菜单高度

    private float mTouchDownX = 0;
    private float mTouchDownY = 0;
    private float mTouchDownRawY = 0;

    private boolean isLongPress = false; // 是否触发了长按事件
    private boolean isVibrator = false;  // 是否触发过长按震动
    private boolean isTextJustify = true;    // 是否需要两端对齐 ，默认true

    private int mStartLine; //action_down触摸事件 起始行
    private int mStartTextOffset; //action_down触摸事件 字符串开始位置的偏移值
    private int mCurrentLine; // action_move触摸事件 当前行
    private int mCurrentTextOffset; //action_move触摸事件 字符串当前位置的偏移值

    private int mViewTextWidth; // SelectableTextView内容的宽度(不包含padding)

    private Vibrator mVibrator;
    private PopupWindow mContextMenuPopupWindow; // 长按弹出菜单
    private ActionMenu mActionMenu = null;

    private CustomActionMenuCallBack mCustomActionMenuCallBack;

    public SelectableTextView(Context context) {
        this(context, null);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        mStatusBarHeight = getStatusBarHeight(mContext);
        mPopWindowHeight = dp2px(mContext, 40);

        mVibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);

        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
        setTextIsSelectable(true);
        setCursorVisible(false);
    }

    @Override
    public boolean getDefaultEditable() {
        // 返回false，屏蔽掉系统自带的ActionMenu
        return false;
    }

    public void setTextJustify(boolean textJustify) {
        isTextJustify = textJustify;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Layout layout = getLayout();
        int currentLine; // 当前所在行

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.d("SelectableTextView", "ACTION_DOWN");

                // 创建菜单，创建不成功，屏蔽长按事件
                if (null == mActionMenu) {
                    mActionMenu = createActionMenu();
                }
                if (mActionMenu.getChildCount() == 0) {
                    return false;
                }

                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mTouchDownRawY = event.getRawY();
                isLongPress = false;
                isVibrator = false;
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d("SelectableTextView", "ACTION_MOVE");

                // 手指移动过程中的字符偏移
                currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                int mWordOffset_move = layout.getOffsetForHorizontal(currentLine, (int) event.getX());


                // 判断是否触发长按事件
                if (event.getEventTime() - event.getDownTime() >= TRIGGER_LONGPRESS_TIME_THRESHOLD
                        && Math.abs(event.getX() - mTouchDownX) < TRIGGER_LONGPRESS_DISTANCE_THRESHOLD
                        && Math.abs(event.getY() - mTouchDownY) < TRIGGER_LONGPRESS_DISTANCE_THRESHOLD) {

                    LogUtil.d("SelectableTextView", "ACTION_MOVE 长按");
                    isLongPress = true;
                    mStartLine = currentLine;
                    mStartTextOffset = mWordOffset_move;

                    // 每次触发长按时，震动提示一次
                    if (!isVibrator) {
                        mVibrator.vibrate(30);
                        isVibrator = true;
                    }
                }

                if (isLongPress) {
                    mCurrentLine = currentLine;
                    mCurrentTextOffset = mWordOffset_move;
                    // 通知父布局不要拦截触摸事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                    // 选择字符
                    Selection.setSelection(getEditableText(), mStartTextOffset, mWordOffset_move);

                    LogUtil.d("SelectableTextView", "ACTION_MOVE：currentLine " + currentLine);
                    LogUtil.d("SelectableTextView", "ACTION_MOVE：mWordOffset_move " + mWordOffset_move);
                }

                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d("SelectableTextView", "ACTION_UP");
                if (isLongPress) {
                    currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    int mWordOffsetEnd = layout.getOffsetForHorizontal(currentLine, (int) event.getX());
                    // 至少选中一个字符
                    if (mWordOffsetEnd == mStartTextOffset) {
                        Selection.removeSelection(getEditableText());
                        isLongPress = false;
                        return false;
                    }

                    Selection.setSelection(getEditableText(), mStartTextOffset, mWordOffsetEnd);

                    LogUtil.d("SelectableTextView", "ACTION_UP：currentLine " + currentLine);
                    LogUtil.d("SelectableTextView", "ACTION_UP：mWordOffset_move " + mWordOffsetEnd);

                    // 计算菜单显示位置
                    int mPopWindowOffsetY = calculatorPopWindowYPosition((int) mTouchDownRawY, (int) event.getRawY());
                    // 弹出菜单
                    showActionMenu(mPopWindowOffsetY, mActionMenu);

                    isLongPress = false;
                }

                // 通知父布局继续拦截触摸事件
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /* ***************************************************************************************** */
    // 创建ActionMenu部分

    /**
     * 创建ActionMenu菜单
     *
     * @return
     */
    private ActionMenu createActionMenu() {
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

        if (actionMenu.getChildCount() != 0) {
            // item监听
            for (int i = 0; i < actionMenu.getChildCount(); i++) {
                actionMenu.getChildAt(i).setOnClickListener(mMenuClickListener);
            }
        }
        return actionMenu;
    }


    /**
     * 长按弹出菜单
     *
     * @param offsetY
     * @param actionMenu
     * @return 菜单创建成功，返回true
     */
    private void showActionMenu(int offsetY, ActionMenu actionMenu) {

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
    private void hideActionMenu() {
        if (null != mContextMenuPopupWindow) {
            mContextMenuPopupWindow.dismiss();
            mContextMenuPopupWindow = null;
        }
    }

    /**
     * 菜单点击事件监听
     */
    private OnClickListener mMenuClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            String menuItemTitle = (String) v.getTag();

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
                if (isTextJustify) {
                    mStartLine = 0;
                    mCurrentLine = getLayout().getLineCount() - 1;
                    mStartTextOffset = 0;
                    mCurrentTextOffset = getLayout().getLineEnd(mCurrentLine);
                    SelectableTextView.this.postInvalidate();
                }
                Selection.selectAll(getEditableText());

            } else if (menuItemTitle.equals(ActionMenu.DEFAULT_MENU_ITEM_TITLE_COPY)) {
                // 复制事件
                copyText(mContext, selected_str);
                Toast.makeText(mContext, "复制成功！", Toast.LENGTH_SHORT).show();
                hideActionMenu();

            } else {
                // 自定义事件
                if (null != mCustomActionMenuCallBack) {
                    mCustomActionMenuCallBack.onCustomActionItemClicked(menuItemTitle, selected_str);
                }
                hideActionMenu();
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


    /* ***************************************************************************************** */
    // 两端对齐部分

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isTextJustify) {
            // 不需要两端对齐
            super.onDraw(canvas);

        } else {
            //textview内容的实际宽度
            mViewTextWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            // 重绘文字，两端对齐
            drawTextWithJustify(canvas);
            // 绘制选中文字的背景
            if (isLongPress) {
                drawSelectedTextBackground(canvas);
                LogUtil.d("SelectableTextView", "onDraw");
            }
        }
    }

    /**
     * 重绘文字，两端对齐
     * @param canvas
     */
    private void drawTextWithJustify(Canvas canvas){

        // 文字画笔
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        String text_str = getText().toString();
        // 当前所在行的Y向偏移
        int currentLineOffsetY = 0;
        currentLineOffsetY += getTextSize();

        Layout layout = getLayout();

        //循环每一行,绘制文字
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            //获取到TextView每行中的内容
            String line_str = text_str.substring(lineStart, lineEnd);
            // 获取每行字符串的宽度(不包括字符间距？)
            float desiredWidth = StaticLayout.getDesiredWidth(text_str, lineStart, lineEnd, getPaint());

            if (needJustify(line_str)) {
                //最后一行不需要重绘
                if (i == layout.getLineCount() - 1) {
                    canvas.drawText(line_str, 0, currentLineOffsetY, textPaint);
                } else {
                    drawJustifyTextForLine(canvas, line_str, desiredWidth, currentLineOffsetY);
                }
            } else {
                canvas.drawText(line_str, 0, currentLineOffsetY, textPaint);
            }
            //更新行Y向偏移
            currentLineOffsetY += getLineHeight();
        }
    }

    /**
     * 绘制选中的文字的背景
     *
     * @param canvas
     */
    private void drawSelectedTextBackground(Canvas canvas) {

        // 文字背景高亮画笔
        Paint highlightPaint = new Paint();
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setColor(getHighlightColor());
        highlightPaint.setAntiAlias(true);

        if (mStartTextOffset == mCurrentTextOffset)
            return;

        // 计算开始位置和结束位置的字符在x方向的偏移
        float startToLeftPosition = calculatorCharPositionToLeft(mStartLine, mStartTextOffset);
        float currentToLeftPosition = calculatorCharPositionToLeft(mCurrentLine, mCurrentTextOffset);

        // 行高
        int h = getLineHeight();

        // 创建三个矩形，分别对应：
        // 所有选中的行对应的矩形，起始行左侧未选中文字的对应的矩形，结束行右侧未选中的位置对应的矩形
        RectF rect_all, rect_lt, rect_rb;

        if (mStartTextOffset < mCurrentTextOffset) {
            rect_all = new RectF(0, mStartLine * h, mViewTextWidth, (mCurrentLine + 1) * h);
            rect_lt = new RectF(0, mStartLine * h, startToLeftPosition, (mStartLine + 1) * h);
            rect_rb = new RectF(currentToLeftPosition, mCurrentLine * h, mViewTextWidth, (mCurrentLine + 1) * h);
        } else {
            rect_all = new RectF(0, mCurrentLine * h, mViewTextWidth, (mStartLine + 1) * h);
            rect_lt = new RectF(0, mCurrentLine * h, currentToLeftPosition, (mCurrentLine + 1) * h);
            rect_rb = new RectF(startToLeftPosition, mStartLine * h, mViewTextWidth, (mStartLine + 1) * h);
        }

        // 创建三个路径，分别对应上面三个矩形
        Path path_all = new Path();
        path_all.addRect(rect_all, Path.Direction.CCW);
        Path path_lt = new Path();
        path_lt.addRect(rect_lt, Path.Direction.CCW);
        Path path_rb = new Path();
        path_rb.addRect(rect_rb, Path.Direction.CCW);

        // 将左上角和右下角的矩形从 所有选中的行对应的矩形 中减去
        path_all.op(path_lt, Path.Op.DIFFERENCE);
        path_all.op(path_rb, Path.Op.DIFFERENCE);

        canvas.drawPath(path_all, highlightPaint);
        canvas.restore();
    }

    /**
     * 重绘此行,两端对齐
     *
     * @param canvas
     * @param line_str           该行所有的文字
     * @param desiredWidth       该行每个文字的宽度的总和
     * @param currentLineOffsetY 该行的Y向偏移
     */
    private void drawJustifyTextForLine(Canvas canvas, String line_str, float desiredWidth, int currentLineOffsetY) {

        // 画笔X方向的偏移
        float lineOffsetX = 0;
        if (isFirstLineOfParagraph(line_str)) {
            String blanks = "  ";
            // 画出blanks
            canvas.drawText(blanks, lineOffsetX, currentLineOffsetY, getPaint());
            // 空格需要的宽度
            float blank_witdh = StaticLayout.getDesiredWidth(blanks, getPaint());
            // 更新画笔X方向的偏移
            lineOffsetX += blank_witdh;
            line_str = line_str.substring(3);
        }
        // 计算相邻字符之间需要填充的宽度
        // (TextView内容的实际宽度 - 该行字符串的宽度)/（字符个数-1）
        float insert_blank = (mViewTextWidth - desiredWidth) / (line_str.length() - 1);
        for (int i = 0; i < line_str.length(); i++) {
            String char_i = String.valueOf(line_str.charAt(i));
            float char_i_width = StaticLayout.getDesiredWidth(char_i, getPaint());
            canvas.drawText(char_i, lineOffsetX, currentLineOffsetY, getPaint());
            // 更新画笔X方向的偏移
            lineOffsetX += char_i_width + insert_blank;
        }
    }

    /**
     * 计算字符距离控件左侧的位移
     *
     * @param line       字符所在行
     * @param charOffset 字符偏移量
     */
    private float calculatorCharPositionToLeft(int line, int charOffset) {

        String text_str = getText().toString();

        Layout layout = getLayout();
        int lineStart = layout.getLineStart(line);
        int lineEnd = layout.getLineEnd(line);

        String line_str = text_str.substring(lineStart, lineEnd);

        float desiredWidth = StaticLayout.getDesiredWidth(text_str, lineStart, lineEnd, getPaint());

        // 计算相邻字符之间需要填充的宽度
        // (TextView内容的实际宽度 - 该行字符串的宽度)/（字符个数-1）
        float insert_blank = (mViewTextWidth - desiredWidth) / (line_str.length() - 1);

        // 做左侧
        if (lineStart == charOffset)
            return 0;
        // 最右侧
        if (charOffset == lineEnd - 1)
            return mViewTextWidth;
        // 中间位置
        float position = 0;
        // 当前字符左侧所有字符的宽度
        float allLeftCharWidth = StaticLayout.getDesiredWidth(text_str.substring(lineStart, charOffset), getPaint());
        // 相邻字符之间需要填充的宽度 + 当前字符左侧所有字符的宽度
        position = insert_blank * (charOffset - lineStart) + allLeftCharWidth;

        return position;
    }

    /**
     * 判断是不是段落的第一行。一个汉字相当于一个字符，此处判断是否为第一行的依据是：
     * 字符长度大于3且前两个字符为空格
     *
     * @param line
     * @return
     */
    private boolean isFirstLineOfParagraph(String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }


    /**
     * 判断该行需不需要缩放；该行最后一个字符不是换行符的时候返回true，
     * 该行最后一个字符是换行符的时候返回false
     *
     * @param line_str 该行的文字
     * @return
     */
    private boolean needJustify(String line_str) {
        if (line_str.length() == 0) {
            return false;
        } else {
            return line_str.charAt(line_str.length() - 1) != '\n';
        }
    }

    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();

        float lineSpacingMultiplier = getLineSpacingMultiplier();
        return (int) Math.ceil((fm.descent - fm.ascent) * lineSpacingMultiplier);
    }

    /* ***************************************************************************************** */

    /**
     * 实现文本复制功能
     *
     * @param text
     */
    public static void copyText(Context context, String text) {
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
    public int dp2px(Context context, float dpValue) {
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

    /* ***************************************************************************************** */
    // 接口

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

    /* ***************************************************************************************** */
    // 内部类

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
            setPadding(20, 0, 20, 0);
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
    }

    /**
     * 通反射显示选择控制工具
     */
    private void showSelectionModifierCursorController() {
        try {
            //找到 TextView中的成员变量mEditor
            Field mEditor = TextView.class.getDeclaredField("mEditor");
            mEditor.setAccessible(true);
            //根具持有对象拿到mEditor变量里的值 （android.widget.Editor类的实例）
            Object object = mEditor.get(this);
            //--------------------显示选择控制工具------------------------------//
            //拿到隐藏类Editor；
            Class mClass = Class.forName("android.widget.Editor");
            //取得方法  getSelectionController
            Method method = mClass.getDeclaredMethod("getSelectionController");
            //取消访问私有方法的合法性检查
            method.setAccessible(true);
            //调用方法，返回SelectionModifierCursorController类的实例
            Object resultobject = method.invoke(object);
            //查找 SelectionModifierCursorController类中的show方法
            Method show = resultobject.getClass().getDeclaredMethod("show");
            //执行SelectionModifierCursorController类的实例的show方法
            show.invoke(resultobject);

            this.setHasTransientState(true);
            // Method stopActionModeMethod = mClass.getDeclaredMethod("stopTextActionMode");
            // stopActionModeMethod.setAccessible(true);
            // stopActionModeMethod.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

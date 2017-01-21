package app.zengpu.com.myexercisedemo.demolist.vertical_textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Vibrator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.zengpu.com.myexercisedemo.R;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.view.MotionEvent.ACTION_MOVE;
import static app.zengpu.com.myexercisedemo.R.attr.charSpacingExtra;
import static app.zengpu.com.myexercisedemo.demolist.selected_textview.SelectableTextView.copyText;


/**
 * Created by zengpu on 2017/1/20.
 */

public class VerticalTextView extends TextView {

    private static String TAG = VerticalTextView.class.getSimpleName();


    private final int TRIGGER_LONGPRESS_TIME_THRESHOLD = 300;    // 触发长按事件的时间阈值
    private final int TRIGGER_LONGPRESS_DISTANCE_THRESHOLD = 10; // 触发长按事件的位移阈值


    private Context mContext;
    private int mScreenWidth;      // 屏幕宽度
    private int mScreenHeight;      // 屏幕高度

    private int mMaxTextLine = 0; // 最大行数

    private boolean isLeftToRight; // 竖排方向，是否从左到右；默认从右到左
    private float mLineSpacingExtra; // 行距
    private float mCharSpacingExtra; // 字符间距

    private boolean isUnderLineText; // 是否需要下划线，默认false
    private int mUnderLineColor; // 下划线颜色
    private float mUnderLineWidth;// 下划线线宽
    private float mUnderLineOffset;// 下划线偏移

    private SparseArray<Float[]> mLinesOffsetArray; // 记录每一行的X,Y偏移量
    private SparseArray<int[]> mLinesTextIndex; // 记录每一行文字开始和结束字符的index

    private int mStatusBarHeight;   // 状态栏高度
    private int mActionMenuHeight;  // 弹出菜单高度
    private int mTextHighlightColor;// 选中文字背景高亮颜色
    private String mSelectedText;

    private float mTouchDownX = 0;
    private float mTouchDownY = 0;
    private float mTouchDownRawY = 0;

    private boolean isLongPress = false;               // 是否发触了长按事件
    private boolean isLongPressTouchActionUp = false;  // 长按事件结束后，标记该次事件
    private boolean isVibrator = false;                // 是否触发过长按震动


    private boolean isForbiddenActionMenu = false;     // 是否禁用 ，默认true
    private boolean isActionSelectAll = false;         // 是否触发全选事件


    private int mStartLine;             //action_down触摸事件 起始行
    private float mStartTextOffset;       //action_down触摸事件 字符串开始位置的偏移值
    private int mCurrentLine;           // action_move触摸事件 当前行
    private float mCurrentTextOffset;     //action_move触摸事件 字符串当前位置的偏移值

    private Vibrator mVibrator;
    private PopupWindow mActionMenuPopupWindow; // 长按弹出菜单
    private ActionMenu mActionMenu = null;

    private OnClickListener mOnClickListener;
    private CustomActionMenuCallBack mCustomActionMenuCallBack;

    public VerticalTextView(Context context) {
        this(context, null);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
        mLineSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_lineSpacingExtra, 6);
        mCharSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_charSpacingExtra, 6);
        isLeftToRight = mTypedArray.getBoolean(R.styleable.VerticalTextView_textLeftToRight, false);
        isUnderLineText = mTypedArray.getBoolean(R.styleable.VerticalTextView_underLineText, false);
        mUnderLineColor = mTypedArray.getColor(R.styleable.VerticalTextView_underLineColor, Color.RED);
        mUnderLineWidth = mTypedArray.getFloat(R.styleable.VerticalTextView_underLineWidth, 1.5f);
        mUnderLineOffset = mTypedArray.getDimension(R.styleable.VerticalTextView_underlineOffset, 3);
        mTypedArray.recycle();

        mLineSpacingExtra = Math.max(6, mLineSpacingExtra);
        mCharSpacingExtra = Math.max(6, mCharSpacingExtra);
        if (isUnderLineText) {
            mUnderLineWidth = Math.abs(mUnderLineWidth);
            mUnderLineOffset = Math.min(Math.abs(mUnderLineOffset), Math.abs(mLineSpacingExtra) / 2);
        }

        this.mContext = context;

        init();

    }

    private void init() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();

        setTextIsSelectable(false);

        mLinesOffsetArray = new SparseArray<>();
        mLinesTextIndex = new SparseArray<>();

        mStatusBarHeight = getStatusBarHeight(mContext);
        mActionMenuHeight = dp2px(mContext, 45);

        mVibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);

    }

    public void setLeftToRight(boolean leftToRight) {
        isLeftToRight = leftToRight;
    }

    public void setLineSpacingExtra(float lineSpacingExtra) {
        this.mLineSpacingExtra = lineSpacingExtra;
    }

    public void setCharSpacingExtra(float charSpacingExtra) {
        this.mCharSpacingExtra = charSpacingExtra;
    }

    public void setUnderLineText(boolean underLineText) {
        isUnderLineText = underLineText;
    }

    public void setUnderLineColor(int underLineColor) {
        this.mUnderLineColor = underLineColor;
    }

    public void setUnderLineWidth(float underLineWidth) {
        this.mUnderLineWidth = underLineWidth;
    }

    public void setUnderLineOffset(float underLineOffset) {
        this.mUnderLineOffset = underLineOffset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // view测量的的宽高(包含padding)
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 文字的宽度
        String[] subTextStr = getText().toString().split("\n");
        int textLines = subTextStr.length;
        for (int i = 0; i < subTextStr.length; i++) {
            textLines += (int) Math.ceil(subTextStr[i].length() * getTextSize()
                    / (heightSize - getPaddingTop() - getPaddingBottom()));
        }

        int textWidth = getPaddingLeft() + getPaddingRight()
                + (int) (textLines * getTextSize() + mLineSpacingExtra * (textLines - 1));

        int measuredWidth;
        if (widthSize == 0) {
            // 当嵌套在HorizontalScrollView时，MeasureSpec.getSize(widthMeasureSpec)返回0，因此需要特殊处理
            measuredWidth = textWidth;

        } else if (widthSize <= mScreenWidth) {
            measuredWidth = textWidth <= mScreenWidth ?
                    Math.max(widthSize, textWidth) : widthSize;
        } else {
            measuredWidth = textWidth <= mScreenWidth ?
                    mScreenWidth : Math.min(widthSize, textWidth);
        }

        int measureHeight;
        if (heightSize == 0) {
            // 当嵌套在ScrollView时，MeasureSpec.getSize(widthMeasureSpec)返回0，因此需要特殊处理
            measureHeight = mScreenHeight;
        } else {
            measureHeight = heightSize;
        }

        setMeasuredDimension(measuredWidth, measureHeight);

        Log.d(TAG, "widthSize is : " + widthSize);
        Log.d(TAG, "heightSize is : " + heightSize);
        Log.d(TAG, "textWidth is : " + textWidth);
        Log.d(TAG, "measuredWidth is : " + measuredWidth);
        Log.d(TAG, "textLines is : " + textLines);

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        if (null != l) {
            mOnClickListener = l;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int currentLine; // 当前所在行
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");

                // 每次按下时，创建ActionMenu菜单，创建不成功，屏蔽长按事件
                if (null == mActionMenu) {
                    mActionMenu = createActionMenu();
                }
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mTouchDownRawY = event.getRawY();
                isLongPress = false;
                isVibrator = false;
                isLongPressTouchActionUp = false;
                break;
            case ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE");
                // 先判断是否禁用了ActionMenu功能，以及ActionMenu是否创建失败，
                // 二者只要满足了一个条件，退出长按事件
                if (!isForbiddenActionMenu || mActionMenu.getChildCount() == 0) {
                    // 手指移动过程中的字符偏移
                    currentLine = getCurrentTouchLine(event.getX(), isLeftToRight);
                    float mWordOffset_move = event.getY();
                    // 判断是否触发长按事件
                    if (event.getEventTime() - event.getDownTime() >= TRIGGER_LONGPRESS_TIME_THRESHOLD
                            && Math.abs(event.getX() - mTouchDownX) < TRIGGER_LONGPRESS_DISTANCE_THRESHOLD
                            && Math.abs(event.getY() - mTouchDownY) < TRIGGER_LONGPRESS_DISTANCE_THRESHOLD) {

                        Log.d(TAG, "ACTION_MOVE 长按");
                        isLongPress = true;
                        isLongPressTouchActionUp = false;
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
//                        // 选择字符
//                        selectText(Math.min(mStartTextOffset, mWordOffset_move),
//                                Math.max(mStartTextOffset, mWordOffset_move),
//                                mStartLine, mCurrentLine, isLeftToRight);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                // 处理长按事件
                if (isLongPress) {
                    currentLine = getCurrentTouchLine(event.getX(), isLeftToRight);
                    float mWordOffsetEnd = event.getY();
                    mCurrentLine = currentLine;
                    mCurrentTextOffset = mWordOffsetEnd;
                    selectText(Math.min(mStartTextOffset, mCurrentTextOffset),
                            Math.max(mStartTextOffset, mCurrentTextOffset),
                            mStartLine, mCurrentLine);

                    // 计算菜单显示位置
                    int mPopWindowOffsetY = calculatorActionMenuYPosition((int) mTouchDownRawY, (int) event.getRawY());
                    // 弹出菜单
                    showActionMenu(mPopWindowOffsetY, mActionMenu);
                    isLongPressTouchActionUp = true;
                    isLongPress = false;

                } else if (event.getEventTime() - event.getDownTime() < TRIGGER_LONGPRESS_TIME_THRESHOLD) {
                    // 由于onTouchEvent最终返回了true,onClick事件会被屏蔽掉，因此在这里处理onClick事件
                    if (null != mOnClickListener)
                        mOnClickListener.onClick(this);
                }
                // 通知父布局继续拦截触摸事件
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /**
     * 计算触摸位置所在行,最小值为1
     *
     * @param offsetX
     * @param isLeftToRight
     * @return
     */
    private int getCurrentTouchLine(float offsetX, boolean isLeftToRight) {

        int currentLine = 1;
        float lineWidth = getTextSize() + mLineSpacingExtra;
        if (isLeftToRight) {
            // 边界控制
            if (offsetX >= getWidth() - getPaddingRight())
                currentLine = mMaxTextLine;
            else
                currentLine = (int) Math.ceil((offsetX - getPaddingLeft()) / lineWidth);
        } else {
            if (offsetX <= getPaddingLeft())
                currentLine = mMaxTextLine;
            else
                currentLine = (int) Math.ceil((getWidth() - offsetX - getPaddingRight()) / lineWidth);
        }
        return currentLine;
    }

    /**
     * 选择字符
     *
     * @param startOffsetY
     * @param endOffsetY
     * @param startLine
     * @param endLine
     */
    private void selectText(float startOffsetY, float endOffsetY, int startLine, int endLine) {

        int index_start = getSelectTextIndex(startOffsetY, startLine, true);
        int index_end = Math.min(getText().length(), getSelectTextIndex(endOffsetY, endLine, false));

        if (index_start >= index_end)
            mSelectedText = "";
        else
            mSelectedText = getText().toString().substring(index_start, index_end);

        Log.d(TAG, "mSelectedText  " + mSelectedText);
    }

    /**
     * 计算所选文字起始或结束字符对应的index
     *
     * @param offsetY
     * @param isStartIndex 是否是计算起始的位置
     */
    private int getSelectTextIndex(float offsetY, int targetLine, boolean isStartIndex) {

        int[] lineIndex = mLinesTextIndex.get(targetLine);

        int index = lineIndex[0];

        float tempY = getPaddingTop();

        for (int i = lineIndex[0]; i <= lineIndex[1]; i++) {
            String char_i = String.valueOf(getText().toString().charAt(i));
            if (isSymbolNeedOffset(char_i))
                tempY += 1.4f * getCharHeight(char_i, getTextPaint()) + charSpacingExtra;

            if (tempY >= offsetY) {
                if (isStartIndex) {
                    index = Math.max(lineIndex[0], i - 1);
                    Log.d(TAG, "index start " + index);
                } else {
                    index = Math.min(lineIndex[1], i);
                    Log.d(TAG, "index end " + index);
                }
                break;
            }
        }

        return index;
    }


    /**
     * 计算弹出菜单相对于父布局的Y向偏移
     *
     * @param yOffsetStart 所选字符的起始位置相对屏幕的Y向偏移
     * @param yOffsetEnd   所选字符的结束位置相对屏幕的Y向偏移
     * @return
     */
    private int calculatorActionMenuYPosition(int yOffsetStart, int yOffsetEnd) {
        if (yOffsetStart > yOffsetEnd) {
            int temp = yOffsetStart;
            yOffsetStart = yOffsetEnd;
            yOffsetEnd = temp;
        }
        int actionMenuOffsetY;

        if (yOffsetStart < mActionMenuHeight * 3 / 2 + mStatusBarHeight) {
            if (yOffsetEnd > mScreenHeight - mActionMenuHeight * 3 / 2) {
                // 菜单显示在屏幕中间
                actionMenuOffsetY = mScreenHeight / 2 - mActionMenuHeight / 2;
            } else {
                // 菜单显示所选文字下方
                actionMenuOffsetY = yOffsetEnd + mActionMenuHeight / 2;
            }
        } else {
            // 菜单显示所选文字上方
            actionMenuOffsetY = yOffsetStart - mActionMenuHeight * 3 / 2;
        }
        return actionMenuOffsetY;
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

        mActionMenuPopupWindow = new PopupWindow(actionMenu, WindowManager.LayoutParams.WRAP_CONTENT,
                mActionMenuHeight, true);
        mActionMenuPopupWindow.setFocusable(true);
        mActionMenuPopupWindow.setOutsideTouchable(false);
        mActionMenuPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mActionMenuPopupWindow.showAtLocation(this, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, offsetY);

        mActionMenuPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 清理已选的文字
                mSelectedText = "";

            }
        });
    }

    /**
     * 隐藏菜单
     */
    private void hideActionMenu() {
        if (null != mActionMenuPopupWindow) {
            mActionMenuPopupWindow.dismiss();
            mActionMenuPopupWindow = null;
        }
    }

    /**
     * 菜单点击事件监听
     */
    private OnClickListener mMenuClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            String menuItemTitle = (String) v.getTag();

            if (menuItemTitle.equals(ActionMenu.DEFAULT_MENU_ITEM_TITLE_SELECT_ALL)) {
                //全选事件
                copyText(mContext, getText().toString());

            } else if (menuItemTitle.equals(ActionMenu.DEFAULT_MENU_ITEM_TITLE_COPY)) {
                // 复制事件
                copyText(mContext, mSelectedText);
                Toast.makeText(mContext, "复制成功！", Toast.LENGTH_SHORT).show();
                hideActionMenu();

            } else {
                // 自定义事件
                if (null != mCustomActionMenuCallBack) {
                    mCustomActionMenuCallBack.onCustomActionItemClicked(menuItemTitle, mSelectedText);
                }
                hideActionMenu();
            }
        }
    };

    /* ***************************************************************************************** */
    // 绘制部分

    @Override
    protected void onDraw(Canvas canvas) {

        drawVerticalText(canvas, mLineSpacingExtra, mCharSpacingExtra, isLeftToRight);
        drawTextUnderline(canvas, isLeftToRight, mUnderLineOffset);

    }

    /**
     * 绘制竖排文字
     *
     * @param canvas
     */
    private void drawVerticalText(Canvas canvas, float lineSpacingExtra,
                                  float charSpacingExtra, boolean isLeftToRight) {
        // 文字画笔
        TextPaint textPaint = getTextPaint();
        String[] subTextStr = getText().toString().split("\n");
        mMaxTextLine = 0;
        int currentIndex = 0;//当前所绘制的的字符所在位置
        int currentLineStartIndex = currentIndex; // 行首标记

        // 当前竖行的X向偏移初始值
        float currentLineOffsetX = isLeftToRight ?
                getPaddingLeft() - getTextSize() - lineSpacingExtra
                : getWidth() - getPaddingRight() + lineSpacingExtra;
        float currentLineOffsetY;

        // 绘制每一个subtext
        for (int i = 0; i < subTextStr.length; i++) {

            // 更新总的行数
            mMaxTextLine++;
            // 每次开始绘制subtext时，需要另起一竖行，因此必须初始化偏移量
            currentLineOffsetY = getPaddingTop() + getTextSize();
            currentLineOffsetX = isLeftToRight ?
                    currentLineOffsetX + getTextSize() + lineSpacingExtra
                    : currentLineOffsetX - getTextSize() - lineSpacingExtra;
            String subText_i = subTextStr[i];
            for (int j = 0; j < subText_i.length(); j++) {

                String char_j = String.valueOf(subText_i.charAt(j));

                // 先判定该竖行是否已经写满，判定条件为：
                // 1.y向剩余的空间已经不够填下一个文字；
                // 2.且当前要绘制的文字不是标点符号；
                // 3.或当前要绘制的文字是标点符号，但标点符号的高度大于y向剩余的空间
                // 注意：文字是从左下角开始向上绘制的
                if (currentLineOffsetY > getHeight() - getPaddingBottom()
                        && (!isUnicodeSymbol(char_j) || (isUnicodeSymbol(char_j) &&
                        currentLineOffsetY + getCharHeight(char_j, textPaint) > getHeight() - getPaddingBottom() + getTextSize()))) {
                    // 该行写满了，记录偏移量,和行首行末字符的index；
                    mLinesOffsetArray.put(mMaxTextLine, new Float[]{currentLineOffsetX, currentLineOffsetY});
                    mLinesTextIndex.put(mMaxTextLine, new int[]{currentLineStartIndex, currentIndex + j});
                    Log.d(TAG, "currentline  is : " + mMaxTextLine);
                    Log.d(TAG, "currentline start is : " + currentLineStartIndex);
                    Log.d(TAG, "currentline end is : " + currentIndex + j);
                    // 另起一竖行，更新偏移量
                    currentLineOffsetX = isLeftToRight ?
                            currentLineOffsetX + getTextSize() + lineSpacingExtra
                            : currentLineOffsetX - getTextSize() - lineSpacingExtra;
                    currentLineOffsetY = getPaddingTop() + getTextSize();
                    mMaxTextLine++;
                }
                //判断是否是行首，记录行首字符位置；
                // 判断行首的条件为：currentLineOffsetY == getPaddingTop()+getTextSize()
                if (currentLineOffsetY == getPaddingTop() + getTextSize()) {
                    currentLineStartIndex = currentIndex + j;
                }

                // 绘制第j个字符
                if (isUnicodeSymbol(char_j)) {
                    // 如果是Y向需要补偿标点符号，加一个补偿 getTextSize() - getCharHeight.
                    // 注意：如果该竖行第一个字符是标点符号的话，不加补偿;
                    // 判断是否是第一个字符的条件为：offsetY == getPaddingTop() + getTextSize()
                    float offsetY = currentLineOffsetY;
                    if (isSymbolNeedOffset(char_j))
                        offsetY = offsetY - (getTextSize() - 1.4f * getCharHeight(char_j, textPaint));
                    // 文字从左向右，标点符号靠右绘制
                    float offsetX = currentLineOffsetX;
                    if (isLeftToRight)
                        offsetX = offsetX + getTextSize() / 2;

                    canvas.drawText(char_j, offsetX, offsetY, textPaint);
                    currentLineOffsetY += 1.4f * getCharHeight(char_j, textPaint) + charSpacingExtra;

                } else {
                    canvas.drawText(char_j, currentLineOffsetX, currentLineOffsetY, textPaint);
                    currentLineOffsetY += getTextSize() + charSpacingExtra;
                }
            }
            // 最后一行的偏移量和行首行末字符的index；
            mLinesOffsetArray.put(mMaxTextLine, new Float[]{currentLineOffsetX, currentLineOffsetY});
            mLinesTextIndex.put(mMaxTextLine, new int[]{currentLineStartIndex, currentIndex + subText_i.length() - 1});
            // 每一个subText绘制完后，更新currentIndex。注意：每一个subText是由换行符分割出来的，
            // 因此currentIndex需要补偿一个换行符
            currentIndex += subText_i.length() - 1 + 1;
        }

        Log.d(TAG, "mMaxTextLine is : " + mMaxTextLine);
    }

    private TextPaint getTextPaint() {
        // 文字画笔
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        return textPaint;
    }

    /**
     * 下划线
     *
     * @param canvas
     */
    private void drawTextUnderline(Canvas canvas, boolean isLeftToRight, float underLineOffset) {

        if (!isUnderLineText || mUnderLineWidth == 0)
            return;

        // 下划线
        Paint underLinePaint = getPaint();
        underLinePaint.setColor(mUnderLineColor);
        underLinePaint.setAntiAlias(true);
        underLinePaint.setStyle(Paint.Style.FILL);
        underLinePaint.setStrokeWidth(mUnderLineWidth);

        for (int i = 0; i < mMaxTextLine; i++) {

            float yStart = getPaddingTop();
            float yEnd = mLinesOffsetArray.get(i + 1)[1] - getTextSize();

            if (yEnd <= yStart)
                continue;

            if (yEnd > getHeight() - getPaddingBottom() - getTextSize())
                yEnd = getHeight() - getPaddingBottom();

            float xStart = mLinesOffsetArray.get(i + 1)[0];
            if (isLeftToRight)
                xStart += getTextSize() + underLineOffset;
            else
                xStart -= underLineOffset;

            float xEnd = xStart;

            canvas.drawLine(xStart, yStart, xEnd, yEnd, underLinePaint);
        }
    }

    /**
     * 获取一个字符的高度
     *
     * @param target_char
     * @param paint
     * @return
     */
    private float getCharHeight(String target_char, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(target_char, 0, 1, rect);
        return rect.height();
    }

    /**
     * 获取一个字符的宽度
     *
     * @param target_char
     * @param paint
     * @return
     */
    private float getCharWidth(String target_char, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(target_char, 0, 1, rect);
        return rect.width();
    }

    /**
     * 判断是否是标点符号
     * - - —— = + ~ 这几个不做判断
     *
     * @param str
     * @return
     */
    private boolean isUnicodeSymbol(String str) {
        String regex = ".*[_\"`!@#$%^&*()|{}':;,\\[\\].<>/?！￥…（）【】‘’；：”“。，、？]$+.*";
        Matcher m = Pattern.compile(regex).matcher(str);
        return m.matches();
    }

    /**
     * 需要补偿的标点符号
     * - - —— = + ~ 这几个不做补偿
     *
     * @param str
     * @return
     */
    private boolean isSymbolNeedOffset(String str) {
        String regex = ".*[_!@#$%&()|{}:;,\\[\\].<>/?！￥…（）【】；：。，、？]$+.*";
        Matcher m = Pattern.compile(regex).matcher(str);
        return m.matches();
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


    /* ***************************************************************************************** */
    // 接口

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


    /* ***************************************************************************************** */
    // 内部类

    /**
     * 触发长按事件后弹出的ActionMenu菜单
     *
     * @hiden
     */
    public static class ActionMenu extends LinearLayout {

        private static final String DEFAULT_MENU_ITEM_TITLE_SELECT_ALL = "全选";
        private static final String DEFAULT_MENU_ITEM_TITLE_COPY = "复制";

        private Context mContext;
        private int mMenuItemMargin;
        private int mActionMenuBgColor = 0xff666666; // ActionMenu背景色
        private int mMenuItemTextColor = 0xffffffff; // MenuItem字体颜色
        private List<String> mItemTitleList;         // MenuItem 标题

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
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 45);
            setLayoutParams(params);
            setPadding(25, 0, 25, 0);
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER);
            setActionMenuBackGround(mActionMenuBgColor);
            mMenuItemMargin = 25;
        }

        /**
         * 设置ActionMenu背景
         */
        private void setActionMenuBackGround(int menuBgColor) {
            GradientDrawable gd = new GradientDrawable();//创建drawable
            gd.setColor(menuBgColor);
            gd.setCornerRadius(8);
            setBackgroundDrawable(gd);
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
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
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

}

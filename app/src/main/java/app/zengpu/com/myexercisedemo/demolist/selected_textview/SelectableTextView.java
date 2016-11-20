package app.zengpu.com.myexercisedemo.demolist.selected_textview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by zengpu on 2016/11/20.
 */

public class SelectableTextView extends EditText {

    public static final int CONTEXT_MENU_TYPE_SELECT_ALL = 0; // 全选
    public static final int CONTEXT_MENU_TYPE_COPY = 1; // 复制
    public static final int CONTEXT_MENU_TYPE_TRANS = 2; // 翻译
    public static final int CONTEXT_MENU_TYPE_SHARE = 3; // 分享

    private Context context;

    private int mWordStartOffset; //字符串开始位置的偏移值

    float mTouchDownX = 0;
    float mTouchDownY = 0;
    float mTouchDownRawY = 0;

    boolean isLongPress = false;
    boolean isVibrator = false;

    private int mScreenHeight;
    private int mStatusBarHeight;
    private int mPopWindowHeight;

    private Vibrator mVibrator;

    private PopupWindow mContextMenuPopupWindow;

    private OnContextMenuClickListener mOnContextMenuClickListener;

    public SelectableTextView(Context context) {
        this(context, null);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {


        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        mStatusBarHeight = getStatusBarHeight(context);
        mPopWindowHeight = dip2px(context, 45);

        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
        setTextIsSelectable(true);
        setCursorVisible(false);

        mVibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);


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

                currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                int mWordOffset_move = layout.getOffsetForHorizontal(currentLine, (int) event.getX());

                if (event.getEventTime() - event.getDownTime() >= 300
                        && Math.abs(event.getX() - mTouchDownX) < 10
                        && Math.abs(event.getY() - mTouchDownY) < 10) {
                    LogUtil.d("SelectableTextView", "ACTION_MOVE 长按");
                    isLongPress = true;

                    if (!isVibrator) {
                        mVibrator.vibrate(30);
                        isVibrator = true;
                    }
                }

                if (isLongPress) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    Selection.setSelection(getEditableText(), mWordStartOffset, mWordOffset_move);
                }

                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d("SelectableTextView", "ACTION_UP");
                if (isLongPress) {
                    currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    int mWordOffsetEnd = layout.getOffsetForHorizontal(currentLine, (int) event.getX());

                    if (mWordOffsetEnd == mWordStartOffset)
                        mWordOffsetEnd += 1;

                    Selection.setSelection(getEditableText(), mWordStartOffset, mWordOffsetEnd);

                    int mPopWindowOffsetY = calculatorPopWindowYPosition((int) mTouchDownRawY, (int) event.getRawY());

                    showContextMenu(mPopWindowOffsetY);

                }

                getParent().requestDisallowInterceptTouchEvent(false);

                break;
        }
        return true;
    }


    /**
     * 显示长按后弹出的菜单
     *
     * @param offsetY
     */
    private void showContextMenu(int offsetY) {

        View contentView = LayoutInflater.from(context).inflate(R.layout.menu_popwindow, null, false);
        contentView.setFocusable(true); // 这个很重要
        contentView.setFocusableInTouchMode(true);

        TextView tv_select_all = (TextView) contentView.findViewById(R.id.tv_select_all);
        TextView tv_copy = (TextView) contentView.findViewById(R.id.tv_copy);
        TextView tv_trans = (TextView) contentView.findViewById(R.id.tv_trans);
        TextView tv_share = (TextView) contentView.findViewById(R.id.tv_share);

        tv_select_all.setOnClickListener(mMenuClickListener);
        tv_copy.setOnClickListener(mMenuClickListener);
        tv_trans.setOnClickListener(mMenuClickListener);
        tv_share.setOnClickListener(mMenuClickListener);


        mContextMenuPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT,
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

    private void hideContextMenu() {
        if (null != mContextMenuPopupWindow) {
            mContextMenuPopupWindow.dismiss();
            mContextMenuPopupWindow = null;
        }

    }

    private OnClickListener mMenuClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            int start = getSelectionStart();
            int end = getSelectionEnd();
            LogUtil.d("SelectableTextView", "start: " + start);
            LogUtil.d("SelectableTextView", "end: " + end);


            String selected_str;
            if (start < 0 || end < 0 || end <= start) {
                selected_str = "";
            } else
                selected_str = getText().toString().substring(start, end);

            int id = v.getId();
            if (id == R.id.tv_select_all) {
                //全选
                Selection.selectAll(getEditableText());

                if (null != mOnContextMenuClickListener) {
                    mOnContextMenuClickListener.onMenuItemClick(v, CONTEXT_MENU_TYPE_SELECT_ALL, getText().toString());
                }
            }

            if (id == R.id.tv_copy) {

                copy(context, selected_str);
                Toast.makeText(context, "复制成功！", Toast.LENGTH_SHORT).show();

                if (null != mOnContextMenuClickListener) {
                    mOnContextMenuClickListener.onMenuItemClick(v, CONTEXT_MENU_TYPE_COPY, selected_str);
                }

                hideContextMenu();
            }

            if (id == R.id.tv_trans) {

                if (null != mOnContextMenuClickListener) {
                    mOnContextMenuClickListener.onMenuItemClick(v, CONTEXT_MENU_TYPE_TRANS, selected_str);
                    Toast.makeText(context, "翻译", Toast.LENGTH_SHORT).show();
                }

                hideContextMenu();

            }

            if (id == R.id.tv_share) {
                if (null != mOnContextMenuClickListener) {
                    mOnContextMenuClickListener.onMenuItemClick(v, CONTEXT_MENU_TYPE_SHARE, selected_str);
                    Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
                }

                hideContextMenu();
            }

        }
    };


    /**
     * 计算popWindow Y向的显示位置
     *
     * @param y_down
     * @param y_up
     * @return
     */
    private int calculatorPopWindowYPosition(int y_down, int y_up) {
        if (y_down > y_up) {
            int temp = y_down;
            y_down = y_up;
            y_up = temp;
        }
        int offsetY;

        if (y_down < mPopWindowHeight * 3 / 2 + mStatusBarHeight) {

            if (y_up > mScreenHeight - mPopWindowHeight * 3 / 2) {
                offsetY = mScreenHeight / 2 - mPopWindowHeight / 2;
            } else {
                offsetY = y_up + mPopWindowHeight / 2;
            }
        } else {
            offsetY = y_down - mPopWindowHeight * 3 / 2;
        }

        return offsetY;
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

    private int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            sbar = 0;
            e1.printStackTrace();
        }
        return sbar;
    }


    public interface OnContextMenuClickListener {

        void onMenuItemClick(View v, int type, String selectedText);

    }

    public void setOnContextMenuClickListener(OnContextMenuClickListener listener) {
        this.mOnContextMenuClickListener = listener;
    }

    /**
     * 通过反射显示选择控制工具
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

//            Method stopActionModeMethod = mClass.getDeclaredMethod("stopTextActionMode");
//            stopActionModeMethod.setAccessible(true);
//            stopActionModeMethod.invoke(object);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

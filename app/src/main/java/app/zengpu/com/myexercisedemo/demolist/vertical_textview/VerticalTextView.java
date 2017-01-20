package app.zengpu.com.myexercisedemo.demolist.vertical_textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zengpu on 2017/1/20.
 */

public class VerticalTextView extends TextView {

    private boolean isLeftToRight = false;
    private int mMaxTextLine = 0;
    private float mLineSpacingExtra = 5; // 行距
    private float mCharSpacingExtra = 5; // 字符间距

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawText(canvas, mLineSpacingExtra, mCharSpacingExtra, isLeftToRight);

    }

    /**
     * 绘制竖排文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas, float lineSpacingExtra, float charSpacingExtra, boolean isLeftToRight) {
        // 文字画笔
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.setTextAlign(Paint.Align.CENTER);
        String[] subTextStr = getText().toString().split("\n");

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
                // 先判定该竖行是否已经写满
                if (currentLineOffsetY > getHeight() - getPaddingBottom() - getTextSize()) {
                    // 如果写满，另起一竖行，更新偏移量
                    currentLineOffsetX = isLeftToRight ?
                            currentLineOffsetX + getTextSize() + lineSpacingExtra
                            : currentLineOffsetX - getTextSize() - lineSpacingExtra;
                    currentLineOffsetY = getPaddingTop()+ getTextSize();
                    mMaxTextLine++;
                }
                // 绘制第j个字符
                String char_j = String.valueOf(subText_i.charAt(j));
                if (isUnicodeSymbol(char_j)) {
                    // 如果是标点符号，加一个补偿 getTextSize() - getCharHeight

                    canvas.drawText(char_j, currentLineOffsetX,
                            currentLineOffsetY - (getTextSize() - getCharHeight(char_j, textPaint)), textPaint);
                    currentLineOffsetY += getCharHeight(char_j, textPaint) + charSpacingExtra;
                } else {
                    canvas.drawText(char_j, currentLineOffsetX, currentLineOffsetY, textPaint);
                    currentLineOffsetY += getTextSize() + charSpacingExtra;
                }


            }
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
     * 判断是否是中文标点符号
     *
     * @param str ,./?;:'"[]{}+=
     * @return
     */
    private boolean isUnicodeSymbol(String str) {
        String regex = ".*[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]$+.*";
        Matcher m = Pattern.compile(regex).matcher(str);
        return m.matches();
    }

}

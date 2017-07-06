package app.zengpu.com.myexercisedemo.demolist.rich_textview;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PrefixEditText is a custom EditText that can add more than one prefix-text such as the sample of
 * <p>"#some hot topic#" , at the beginning of the content text.
 * Created by zengpu on 2017/7/5.
 */
public class PrefixEditText extends EditText implements TextWatcher {

    private static final String ATTRIBUTE_SPACE = "http://schemas.android.com/apk/res/android";

    private String mLastPrefixText = "";         // all the prefix text before the latest text changed
    private String mCurrentPrefixText = "";      // all the current prefix text
    private String mPrefixSeparateChar = "#";    // char separator surrounding the prefix item text
    private String mPrefixTextColor = "#f26d85"; // prefix item text color
    private int mMaxPrefixCount = 3;             // max count of prefix item text
    private List<String> mCurrentPrefixItems = new ArrayList<>();

    private String mContentText = "";   // content text without the prefix
    private int mLastTextLength = 0;

    private int mMaxLength = -1;
    // the text max length whether exclude the prefix length, default is true
    private boolean isMaxLengthExcludePrefix = true;
    private TextView mLeftTextCountView;

    public PrefixEditText(Context context) {
        super(context);
    }

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMaxLength = attrs.getAttributeIntValue(ATTRIBUTE_SPACE, "maxLength", -1);
    }

    public PrefixEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMaxLength = attrs.getAttributeIntValue(ATTRIBUTE_SPACE, "maxLength", -1);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addTextChangedListener(this);
    }

    public void addPrefixText(String prefixText) {
        if (TextUtils.isEmpty(prefixText))
            return;

        if (!prefixText.startsWith(mPrefixSeparateChar)
                || !prefixText.endsWith(mPrefixSeparateChar))
            return;

        mCurrentPrefixText = getPrefixText(getText().toString());

        if (mCurrentPrefixText.contains(prefixText))
            return;

        if (getPrefixCount(getText().toString()) >= mMaxPrefixCount)
            return;

        mContentText = getText().toString().replace(mCurrentPrefixText, "");
        mCurrentPrefixText = mCurrentPrefixText + prefixText;
        mCurrentPrefixText += " ";
        updateText(mCurrentPrefixText, mContentText);
        mCurrentPrefixItems = getPrefixItems(getText().toString());
    }

    private void updateText(String prefixText, String contentText) {
        // update maxLength
        if (mMaxLength != -1) {
            if (isMaxLengthExcludePrefix) {
                // prefix length is excluded from the MaxLength, therefore once the prefix length is changed,
                // the real total length must be update so that the content text max length is always not changed.
                int maxLength = mMaxLength + prefixText.length();
                updateMaxLength(maxLength);
            } else {
                updateMaxLength(mMaxLength);
            }
            if (null != mLeftTextCountView) {
                int contentLength = isMaxLengthExcludePrefix ?
                        contentText.length() : contentText.length() + prefixText.length();
                mLeftTextCountView.setText(contentLength + "/" + mMaxLength);
            }
        }
        // update text
        String html_str = "<font color =" + mPrefixTextColor + ">" + prefixText + "</font>" + contentText;
        setText(Html.fromHtml(html_str));
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        // current selection's start position must be behind the PrefixText !
        mCurrentPrefixText = getPrefixText(getText().toString());
        if (!TextUtils.isEmpty(mCurrentPrefixText)) {
            int prefixLength = mCurrentPrefixText.length();
            int currentSelection = getSelectionStart();
            if (currentSelection < prefixLength)
                setSelection(prefixLength);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (null != s)
            mLastTextLength = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (null == s)
            return;
        String result = s.toString();
        if (result.length() < mLastTextLength) {
            // text reduces; delete prefix item text one by one
            if (getSelectionStart() == 0) {
                setSelection(Math.min(mCurrentPrefixText.length(), result.length()));
            }
            if (getSelectionStart() < mCurrentPrefixText.length()) {
                if (mCurrentPrefixItems.size() > 0) {
                    String del_prefix = mCurrentPrefixItems.get(mCurrentPrefixItems.size() - 1);
                    mCurrentPrefixText = mCurrentPrefixText.replace(del_prefix, "");
                    mCurrentPrefixItems.remove(del_prefix);
                    updateText(mCurrentPrefixText, mContentText);
                    mLastPrefixText = mCurrentPrefixText;
                }
            } else {
                mContentText = result.replace(mCurrentPrefixText, "");
            }
        } else {
            // text increases. update current selection start position after a new prefix text is added
            mCurrentPrefixText = getPrefixText(result);
            mContentText = result.replace(mCurrentPrefixText, "");
            if (!mLastPrefixText.equals(mCurrentPrefixText)) {
                int increase_length = mCurrentPrefixText.length() - mLastPrefixText.length();
                if (increase_length > 0) {
                    setSelection(getText().length());
                } else if (increase_length < 0) {
                    setSelection(mCurrentPrefixText.length());
                }
                mLastPrefixText = mCurrentPrefixText;
            }
        }
        if (null != mLeftTextCountView && mMaxLength != -1) {
            int contentLength = isMaxLengthExcludePrefix ?
                    getText().length() - mCurrentPrefixText.length() : getText().length();
            mLeftTextCountView.setText(contentLength + "/" + mMaxLength);
        }
    }

    public void setPrefixSeparateChar(String prefixSeparateChar) {
        this.mPrefixSeparateChar = prefixSeparateChar;
    }

    public void setMaxPrefixCount(int maxPrefixCount) {
        if (maxPrefixCount < 0) {
            Log.w("PrefixEditText", "maxPrefixCount must be not less than zero !");
            return;
        }
        this.mMaxPrefixCount = maxPrefixCount;
    }

    public void setPrefixTextColor(String prefixTextColor) {
        if (TextUtils.isEmpty(prefixTextColor)) {
            Log.w("PrefixEditText", "prefixTextColor must be not empty !");
            return;
        }
        if (!prefixTextColor.startsWith("#")) {
            Log.w("PrefixEditText", " the color is invalidate ! it must be start with char '#' !");
            return;
        }
        if (prefixTextColor.length() != 7 || prefixTextColor.length() != 9) {
            Log.w("PrefixEditText", " the color is invalidate ! it must follow Color Format like '#ffffff' or '#ffffffff' !");
            return;
        }
        this.mPrefixTextColor = prefixTextColor;
    }

    public void setMaxLengthExcludePrefix(boolean maxLengthExcludePrefix) {
        isMaxLengthExcludePrefix = maxLengthExcludePrefix;
        updateText(mCurrentPrefixText, mContentText);
    }

    public void setMaxLength(int maxLength) {
        if (maxLength <= 0 && maxLength != -1) {
            Log.w("PrefixEditText", "maxLength must be not less zero !");
            return;
        }
        mMaxLength = maxLength;
        if (!isMaxLengthExcludePrefix) {
            if (maxLength < mCurrentPrefixText.length())
                mCurrentPrefixText = "";
        }
        updateText(mCurrentPrefixText, mContentText);
    }

    public String getTextWithoutPrefix() {
        return mContentText;
    }

    public void setLeftTextCountView(TextView view) {
        mLeftTextCountView = view;
    }

    private String getPrefixText(String text) {
        String reg = mPrefixSeparateChar + "(.*)" + mPrefixSeparateChar + " ";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(text);
        while (m.find()) {
            return mPrefixSeparateChar + m.group(1) + mPrefixSeparateChar + " ";
        }
        return "";
    }

    public int getPrefixCount(String text) {
        int count = 0;
        String reg = mPrefixSeparateChar + "(\\S*)" + mPrefixSeparateChar;
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(text);
        while (m.find()) {
            count++;
        }
        return count;
    }

    private List<String> getPrefixItems(String text) {
        List<String> currentPrefixItems = new ArrayList<>();
        String reg = mPrefixSeparateChar + "(\\S*)" + mPrefixSeparateChar;
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(text);
        while (m.find()) {
            currentPrefixItems.add(mPrefixSeparateChar + m.group(1) + mPrefixSeparateChar + " ");
        }
        return currentPrefixItems;
    }

    private void updateMaxLength(int maxLength) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        setFilters(FilterArray);
    }
}

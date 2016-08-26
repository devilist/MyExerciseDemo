package app.zengpu.com.myexercisedemo.demolist.design_support_library.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import app.zengpu.com.myexercisedemo.R;


/**
 * A login screen that offers login via email/password.
 */
public class DSLLoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView; // 邮箱
    private EditText mPasswordView; // 密码
    private View mProgressView; // 进度条
    private ScrollView mLoginFormView; // 登录布局
    private Button mEmailSignInButton; // 登录btn
    private ImageView shiftView; // 转场动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dsl_activity_login);
        initView();
    }

    private void initView() {

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = (ScrollView) findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        shiftView = (ImageView) findViewById(R.id.iv_shift_anmi);

        mEmailView.setText("foo@example.com");
        mPasswordView.setText("hello");

        // edittext编辑完之后的错误监听
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * 判断登录
     */
    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 判断邮箱
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // 判断密码
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // 获取焦点
            focusView.requestFocus();
        } else {
            showShiftAnimation();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showShiftAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shiftView.setVisibility(View.VISIBLE);
            shiftView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    shiftView, shiftView.getWidth() / 2, shiftView.getHeight() / 2,
                    0, shiftView.getHeight());
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(2000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DSLMainActivity.actionStart(DSLLoginActivity.this);
                            finish();
                        }
                    },3000);
                }
            });
            animator.start();
        } else {
            shiftView.setVisibility(View.GONE);
            showProgress(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DSLMainActivity.actionStart(DSLLoginActivity.this);
                    finish();
                }
            }, 3000);
        }
    }

    private void hiddenShiftAnmimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressView.setVisibility(View.GONE);
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    shiftView, shiftView.getWidth() / 2, shiftView.getHeight() / 2,
                    shiftView.getHeight(), 0);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(2000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    shiftView.setVisibility(View.GONE);
                }
            });
            animator.start();
        }else {
            showProgress(false);
        }
    }

    /**
     * 显示进度条
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            if (show) {
                shiftView.setVisibility(View.VISIBLE);
            }


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DSLLoginActivity.class);
        context.startActivity(intent);
    }
}


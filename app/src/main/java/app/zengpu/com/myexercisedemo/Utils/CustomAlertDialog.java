package app.zengpu.com.myexercisedemo.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import app.zengpu.com.myexercisedemo.R;

/**
 * 自定义dialog
 * <p/>
 * Created by zengpu on 2016/7/1.
 */
public class CustomAlertDialog extends Dialog {

    public interface AlertDialogClickListener {
        public void onResult(boolean confirmed, Bundle bundle);
    }

    private String title;
    private String msg;
    private AlertDialogClickListener listener;
    private Bundle bundle;
    private boolean showCancel = false;

    public CustomAlertDialog(Context context, int msgId) {
        super(context);
        this.title = "提示";
        this.msg = context.getResources().getString(msgId);
        this.setCanceledOnTouchOutside(true);
    }

    public CustomAlertDialog(Context context, String msg) {
        super(context);
        this.title = "提示";
        this.msg = msg;
        this.setCanceledOnTouchOutside(true);
    }

    public CustomAlertDialog(Context context, int titleId, int msgId) {
        super(context);
        this.title = context.getResources().getString(titleId);
        this.msg = context.getResources().getString(msgId);
        this.setCanceledOnTouchOutside(true);
    }

    public CustomAlertDialog(Context context, String title, String msg) {
        super(context);
        this.title = title;
        this.msg = msg;
        this.setCanceledOnTouchOutside(true);
    }

    public CustomAlertDialog(Context context, int titleId, int msgId, Bundle bundle, AlertDialogClickListener listener, boolean showCancel) {
        super(context);
        this.title = context.getResources().getString(titleId);
        this.msg = context.getResources().getString(msgId);
        this.listener = listener;
        this.bundle = bundle;
        this.showCancel = showCancel;
        this.setCanceledOnTouchOutside(true);
    }

    public CustomAlertDialog(Context context, int titleId, int msgId, Bundle bundle, AlertDialogClickListener listener, boolean showCancel, int style) {
        super(context, style);
        this.title = context.getResources().getString(titleId);
        this.msg = context.getResources().getString(msgId);
        this.listener = listener;
        this.bundle = bundle;
        this.showCancel = showCancel;
        this.setCanceledOnTouchOutside(true);
    }

    public CustomAlertDialog(Context context, String title, String msg, Bundle bundle, AlertDialogClickListener listener, boolean showCancel) {
        super(context);
        this.title = title;
        this.msg = msg;
        this.listener = listener;
        this.bundle = bundle;
        this.showCancel = showCancel;
        this.setCanceledOnTouchOutside(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert_custom);
        Button cancel = (Button) findViewById(R.id.btn_cancel);
        Button ok = (Button) findViewById(R.id.btn_ok);
        TextView titleView = (TextView) findViewById(R.id.title);
        setTitle(title);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_ok) {
                    onOk(view);
                } else if (view.getId() == R.id.btn_cancel) {
                    onCancel(view);
                }
            }
        };
        cancel.setOnClickListener(listener);
        ok.setOnClickListener(listener);

        if (title != null)
            titleView.setText(title);

        if (showCancel) {
            cancel.setVisibility(View.VISIBLE);
        }

        if (msg != null)
            ((TextView) findViewById(R.id.alert_message)).setText(msg);
    }

    public void onOk(View view) {
        this.dismiss();
        if (this.listener != null) {
            this.listener.onResult(true, this.bundle);
        }
    }

    public void onCancel(View view) {
        this.dismiss();
        if (this.listener != null) {
            this.listener.onResult(false, this.bundle);
        }
    }
}
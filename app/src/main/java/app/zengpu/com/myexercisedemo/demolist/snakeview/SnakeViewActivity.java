package app.zengpu.com.myexercisedemo.demolist.snakeview;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengp on 2017/11/6.
 */

public class SnakeViewActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_attach, tv_dettach;
    private ImageView imageView;
    private RelativeLayout relativeLayout;
    private SnakeViewMaker snakeViewMaker, snakeViewMaker1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snakeview);

        tv_attach = (TextView) findViewById(R.id.tv_attach);
        tv_dettach = (TextView) findViewById(R.id.tv_dettach);
        imageView = (ImageView) findViewById(R.id.iv_snake);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_snake);

        tv_attach.setOnClickListener(this);
        tv_dettach.setOnClickListener(this);
        imageView.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);

        String url = "https://moneycp.oss-cn-hangzhou.aliyuncs.com/image/9b4c3036ca5636e8961bb43a408ca53b.jpg";
        Glide.with(this).load(url).asBitmap()
                .transform(new RoundTransformation(this)).into(imageView);
        Glide.with(this).load(url).asBitmap()
                .transform(new RoundTransformation(this)).into((ImageView) findViewById(R.id.iv_snake_inner));

        snakeViewMaker = new SnakeViewMaker(this);
        snakeViewMaker.addTargetView(imageView)
                .attachToRootLayout((ViewGroup) findViewById(R.id.root));

        snakeViewMaker1 = new SnakeViewMaker(this);
        snakeViewMaker1.addTargetView(relativeLayout)
                .attachToRootLayout((ViewGroup) findViewById(R.id.root));

        LogUtil.d("SnakeViewActivity", "actionbar " + (getSupportActionBar() == null));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_attach:
                snakeViewMaker.attachToRootLayout((ViewGroup) findViewById(R.id.root));
                snakeViewMaker1.attachToRootLayout((ViewGroup) findViewById(R.id.root));
                break;
            case R.id.tv_dettach:
                snakeViewMaker.detachSnake();
                snakeViewMaker1.detachSnake();
                break;
            case R.id.iv_snake:
                Toast.makeText(this, "target imageview", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_snake:
                Toast.makeText(this, "target relativelayout", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}

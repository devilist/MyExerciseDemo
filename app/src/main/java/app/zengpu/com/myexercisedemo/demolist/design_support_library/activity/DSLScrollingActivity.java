package app.zengpu.com.myexercisedemo.demolist.design_support_library.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import app.zengpu.com.myexercisedemo.R;

public class DSLScrollingActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsl_activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setImageBitmap((Bitmap) getIntent().getParcelableExtra("drawable"));


    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DSLScrollingActivity.class);
        context.startActivity(intent);
    }

    public static void actionStart(AppCompatActivity activity, View transitionView, Bitmap drawable) {

        String transitionName = activity.getString(R.string.transition_string);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, transitionName);

        Intent intent = new Intent(activity, DSLScrollingActivity.class);
        intent.putExtra("drawable", (Parcelable) drawable);


        ActivityCompat.startActivity(activity, intent, options.toBundle());

    }
}

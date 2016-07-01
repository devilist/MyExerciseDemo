package app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PhotoPreviewActivity;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 图片选择器
 * Created by zengpu on 2016/6/28.
 */
public class GalleryFinalActivity extends AppCompatActivity {

    private Button button;
    private ArrayList<PhotoInfo> mPhotoList;

    CheckBox mRbSingleSelect;
    CheckBox mCbCrop;
    CheckBox mRbMutiSelect;
    CheckBox mCbTakePhoto;

    boolean isSingle = false;
    boolean isCrop = false;
    boolean isMuti = false;
    boolean isTakePhoto = false;
    int maxSelect;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garelly_final);

        mPhotoList = new ArrayList<>();
        maxSelect = 9;

        button = (Button) findViewById(R.id.btn_button);
        mRbSingleSelect = (CheckBox) findViewById(R.id.cb_single_select);
        mCbCrop = (CheckBox) findViewById(R.id.cb_crop);
        mRbMutiSelect = (CheckBox) findViewById(R.id.cb_muti_select);
        mCbTakePhoto = (CheckBox) findViewById(R.id.cb_take_photo);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSingle = mRbSingleSelect.isChecked();
                isCrop = mCbCrop.isChecked();
                isTakePhoto = mCbTakePhoto.isChecked();

                String path = "/storage/emulated/0/DCIM/Camera/IMG_20160102_120534.jpg";

                if (isTakePhoto)
                    GalleryFinalConfigUtil.openGalleryFinal_TakePhoto(GalleryFinalActivity.this, mPhotoList, isCrop, mOnHanlderResultCallback);
                else if (isCrop && !isSingle && !isMuti)
                    GalleryFinalConfigUtil.openGalleryFinal_Crop(GalleryFinalActivity.this, mPhotoList, path, mOnHanlderResultCallback);
                else
                    GalleryFinalConfigUtil.openGalleryFinal(GalleryFinalActivity.this, mPhotoList, isSingle, isCrop, 9, mOnHanlderResultCallback);

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_photolist);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mAdapter = new RecyclerViewAdapter(this, mPhotoList);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(GalleryFinalActivity.this, "打开照片墙", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GalleryFinalActivity.this, PhotoPreviewActivity.class);
                intent.putExtra("photo_list", mPhotoList);
                startActivity(intent);
            }
        });

    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                mPhotoList.addAll(resultList);
                for (int i = 0; i < mPhotoList.size(); i++) {
//                    Log.d("GalleryFinalActivity", "getPhotoId " + i + "is: " + mPhotoList.get(i).getPhotoId());
                    Log.d("GalleryFinalActivity", "getPhotoPath " + i + "is: " + mPhotoList.get(i).getPhotoPath());
                }

                Log.d("GalleryFinalActivity", "mPhotoList size is: " + mPhotoList.size());

                mAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(GalleryFinalActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<PhotoInfo> mPhotoList;
        private OnItemClickListener mOnItemClickListener;

        private int photoWidth = 0;             // 图片显示宽度
        private int photoHeight = 0;


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView itemIv;
            private OnItemClickListener mListener;

            public ViewHolder(View itemView, OnItemClickListener listener) {
                super(itemView);
                mListener = listener;

                itemIv = (ImageView) itemView.findViewById(R.id.iv_photo_item);

                itemIv.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, getLayoutPosition());
            }
        }

        public RecyclerViewAdapter(Context context, List<PhotoInfo> mPhotoList) {
            this.context = context;
            this.mPhotoList = mPhotoList;

            // 得到屏幕宽度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            photoWidth = (wm.getDefaultDisplay().getWidth()) / 3;
            photoHeight = photoWidth;
//            if (spanCount == 2) {
//                photoHeight = photoWidth * 2 / 3;
//            } else {
//                photoHeight = photoWidth;
//            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_galleryfinalactivity_recyclerview, parent, false);
            return new ViewHolder(v, mOnItemClickListener);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder mViewHolder = (ViewHolder) holder;
            Uri uri = Uri.fromFile(new File(mPhotoList.get(position).getPhotoPath()));
            Glide.with(context).load(uri).override(photoWidth, photoHeight).into(mViewHolder.itemIv);

        }

        @Override
        public int getItemCount() {
            return mPhotoList.size();
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }
    }


}

package com.xsx.samer.adapter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * 选择图片的gridview的数据适配器
 * @author XSX
 */
public class ChosePicaAdapter extends CommonBaseAdapter<String> {
    private static final String TAG = "ChosePicaAdapter";

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public List<String> mSelectedImages = new LinkedList<String>();

    //public static String mSelectedImage;

    /**
     * 文件夹路径
     */
    private String mDirPath;

    /**
     * @param files
     * @param dirPath
     * @param context
     *            可选择的图片的数量
     */
    public ChosePicaAdapter(List<String> files, String dirPath, Context context) {
        super(files, context);
        this.mDirPath = dirPath;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView,
                parent, R.layout.grid_item, position);
        final String filename = list.get(position);
        final ImageView iv_image = (ImageView) viewHolder
                .getView(R.id.iv_image);
        iv_image.setImageResource(R.drawable.pictures_no);

        final ImageButton ib_select = (ImageButton) viewHolder
                .getView(R.id.ib_select);
        ib_select.setImageResource(R.drawable.picture_unselected);

        ImageLoader.getInstance().displayImage(
                "file:///" + mDirPath + "/" + filename, iv_image);
        iv_image.setColorFilter(null);
        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImages.contains(mDirPath + "/" + filename)) {
            ib_select.setImageResource(R.drawable.pictures_selected);
            iv_image.setColorFilter(Color.parseColor("#77000000"));
        }

        // 设置ImageView的点击事件
        iv_image.setOnClickListener(new OnClickListener() {
            // 选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // 已经选择过该图片
                if (mSelectedImages.contains(mDirPath + "/" + filename)) {
                    mSelectedImages.remove(mDirPath + "/" + filename);
                    ib_select.setImageResource(R.drawable.picture_unselected);
                    iv_image.setColorFilter(null);
                } else
                // 未选择该图片
                {
                    mSelectedImages.add(mDirPath + "/" + filename);
                    ib_select.setImageResource(R.drawable.pictures_selected);
                    iv_image.setColorFilter(Color.parseColor("#77000000"));
                }
                // 通过广播实现向activity传送数据
                Log.i(TAG, "发送广播");
                intent.putExtra("mSelectedImages",
                        (Serializable) mSelectedImages);
                intent.setAction("com.xsx.samer.ui.ChosePicActivity");
                context.sendBroadcast(intent);
            }

        });

        return viewHolder.getConvertView();
    }

}

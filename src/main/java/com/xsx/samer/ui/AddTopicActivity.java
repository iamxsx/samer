package com.xsx.samer.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.xsx.samer.R;
import com.xsx.samer.config.BmobConstancts;
import com.xsx.samer.model.Topic;
import com.xsx.samer.utils.CommonUtil;
import com.xsx.samer.utils.PhotoUtil;
import com.xsx.samer.utils.PixelUtil;
import com.xsx.samer.widget.EmoticonsEditText;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by XSX on 2015/10/12.
 */
public class AddTopicActivity extends AddActivity {

    private static final String TAG = "AddTopicActivity";
    /**
     * 输入框
     */
    protected EmoticonsEditText et_edit_content;
    /**
     * 照片按钮
     */
    protected ImageView btn_camera;
    /**
     * 总的布局
     */
    protected LinearLayout layout_all;
    /**
     * 放置图片的layout
     */
    private LinearLayout layout_pics;
    /**
     * 单张已上传的图片返回的图片url
     */
    protected String uploadImageUrl;
    /**
     * 单张所要上传的图片的路径
     */
    protected String uploadImagePath;

    private Topic topic;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_item);
        initViews();
    }

    protected void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //标题的文字要在setSupportActionBar之前，否则会无效
        toolbar.setTitle("发表话题");
        setSupportActionBar(toolbar);
        //得到toolbar设置相应的属性
        ActionBar ab = getSupportActionBar();
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        layout_pics = (LinearLayout) findViewById(R.id.layout_pics);
        et_edit_content = (EmoticonsEditText) findViewById(R.id.et_edit_content);
        btn_camera = (ImageView) findViewById(R.id.btn_more);
        btn_camera.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sure:
                send();
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    ProgressBar progressBar;

    protected void send() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        String content = et_edit_content.getText().toString();
        if ("".equals(content) || "".equals(content.trim())) {
            ShowToast("话题内容不能为空");
            return;
        }
        if (uploadImagePath==null) {
            ShowToast("话题图片不能为空");
            return;
        }
        topic = new Topic();
        topic.setTitle(content);
        PhotoUtil.saveBitmap(BmobConstancts.MyUploadDir, filename, bitmap, true);
        uploadImage(uploadImagePath);
    }

    /**
     * 将发布的单张图片上传
     */
    private void uploadImage(String uploadImagePath) {
        // 先将图片保存到sd卡
        BmobProFile.getInstance(this).upload(
                uploadImagePath, new UploadListener() {
                    @Override
                    public void onError(int statuscode, String errormsg) {
                        Log.i(TAG, "单一文件上传出错：" + statuscode + "--" + errormsg);
                    }

                    @Override
                    public void onSuccess(String arg0, String arg1,
                                          BmobFile file) {
                        topic.setTitleImg(file
                                .getFileUrl(AddTopicActivity.this));
                        topic.save(AddTopicActivity.this,
                                new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        ShowToast("发布成功");
                                        Log.i(TAG, Thread.currentThread()
                                                .getId() + "");
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int arg0, String arg1) {
                                        ShowToast("发布失败" + arg1);
                                    }
                                });
                    }

                    @Override
                    public void onProgress(int arg0) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more:
                Intent intent = new Intent(AddTopicActivity.this,
                        ChosePicActivity.class);
                startActivityForResult(intent, BmobConstancts.UPLOADIMAGE_LOCATION);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BmobConstancts.UPLOADIMAGE_LOCATION:
                // 当选中了图片
                if (data == null) {
                    ShowToast("没有选择图片");
                    return;
                }
                if (resultCode == RESULT_OK) {
                    // 如果内存卡不可用
                    if (!CommonUtil.checkSdCard()) {
                        ShowToast("SD卡不可用");
                        return;
                    }
                    showImage(data);
                }
                break;
        }

    }

    /**
     * 通过当前时间生成的文件名
     */
    public String filename;
    public Bitmap bitmap;
    /**
     * 动态添加的图片
     */
    private ImageView imageView;
    final String[] mItems = {"查看大图", "取消选择"};


    private String pathName;
    /**
     * 将从图库选择的图片显示界面上
     *
     * @param data
     */
    public void showImage(Intent data) {
        List<String> mSelectedImages = (List<String>) data
                .getSerializableExtra("mSelectedImages");
        // 将返回的图片显示在界面上
        ShowToast("选择了" + mSelectedImages.size() + "张图片");
        //主题图片只取一张
        pathName = mSelectedImages.get(0);
        Log.i(TAG, "pathName=" + pathName);
        imageView = new ImageView(this);
        bitmap = BitmapFactory.decodeFile(pathName);
        imageView.setImageBitmap(bitmap);
        // 图片点击的时候显示大图
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                builder.create().show();
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                PixelUtil.dp2px(60), PixelUtil.dp2px(60));
        params.setMargins(PixelUtil.dp2px(10), 0, 0, 0);
        layout_pics.addView(imageView, params);

        // 保存图片，记得自己添上后缀
        filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date())
                + "." + Bitmap.CompressFormat.PNG;
        // 拼凑要上传文件的完整路径名
        uploadImagePath = BmobConstancts.MyUploadDir + filename;
        Log.i(TAG, "uploadImagePath=" + uploadImagePath);
        // uploadImagePaths.add(uploadImagePath);

    }


    AlertDialog.Builder builder;

    /**
     * 点击图片后显示的对话框
     */
    public void showDialog() {
        builder = new AlertDialog.Builder(AddTopicActivity.this);
        builder.setTitle("列表选择框");
        builder.setItems(mItems,
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(
                                    AddTopicActivity.this,
                                    ImageBrowserActivity.class);
                            ArrayList<String> photos = new ArrayList<String>();
                            photos.add(pathName);
                            intent.putStringArrayListExtra("photos", photos);
                            intent.putExtra("position", 0);
                            startActivity(intent);
                        } else {
                            layout_pics.removeView(imageView);
                            btn_camera.setEnabled(true);
                        }
                    }
                });
    }
}

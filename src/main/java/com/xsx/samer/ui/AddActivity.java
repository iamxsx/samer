package com.xsx.samer.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xsx.samer.R;
import com.xsx.samer.adapter.EmoAdapter;
import com.xsx.samer.adapter.EmoViewPagerAdapter;
import com.xsx.samer.config.BmobConstancts;
import com.xsx.samer.model.FaceText;
import com.xsx.samer.utils.CommonUtil;
import com.xsx.samer.utils.FaceTextUtil;
import com.xsx.samer.utils.PixelUtil;
import com.xsx.samer.widget.EmoticonsEditText;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.inteface.OnRecordChangeListener;

/**
 * Created by XSX on 2015/10/12.
 */
public class AddActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AddActivity";
    protected Toolbar toolbar;
    /**
     * 滑动的两页表情
     */
    protected ViewPager pager_emo;
    /**
     * 表情集合
     */
    protected List<FaceText> emos;
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
     * 表情按钮
     */
    protected Button btn_emo;
    /**
     * 语音按钮
     */
    protected Button btn_voice;
    /**
     * 弹出框
     */
    protected PopupWindow avatorPop;
    /**
     * 表情面板
     */
    protected LinearLayout layout_emo;
    /**
     * 语音面板
     */
    protected LinearLayout layout_voice;
    /**
     * 录音按钮
     */
    protected TextView start_record_voice;
    /**
     * 话筒动画
     */
    protected Drawable[] drawable_Anims;
    /**
     * 话筒图片
     */
    protected ImageView iv_record;
    /**
     * 录音动画的提示文字
     */
    protected TextView tv_voice_tips;
    /**
     * 录音框
     */
    protected RelativeLayout layout_record;
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
    /**
     * 多张所要上传的图片的路径的集合
     */
    protected List<String> uploadImagePaths=new ArrayList<String>();
    /**
     * 多张所要已上传的图片的路径的集合
     */
    protected List<String> uploadImageUrls=new ArrayList<String>();
    /**
     * 拍照文件保存路径
     */
    private String filePath;

    protected void initViews() {
        initEmoView();
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_pics = (LinearLayout) findViewById(R.id.layout_pics);
        et_edit_content = (EmoticonsEditText) findViewById(R.id.et_edit_content);
        btn_camera = (ImageView) findViewById(R.id.btn_more);
        btn_camera.setOnClickListener(this);
        btn_emo = (Button) findViewById(R.id.btn_emo);
        btn_emo.setOnClickListener(this);
        btn_voice = (Button) findViewById(R.id.btn_voice);
        btn_voice.setOnClickListener(this);
    }

    /**
     * 初始化表情布局,可以滑动的两页
     */
    protected void initEmoView() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtil.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }


    /**
     * 初始化表情
     *
     * @param i
     * @return
     */
    protected View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoAdapter gridAdapter = new EmoAdapter(this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.getText().toString();
                try {
                    if (et_edit_content != null && !TextUtils.isEmpty(key)) {
                        int start = et_edit_content.getSelectionStart();
                        CharSequence content = et_edit_content.getText()
                                .insert(start, key);
                        et_edit_content.setText(content);
                        // 定位光标位置
                        CharSequence info = et_edit_content.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        return view;
    }

    // 显示软键盘
    protected void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(et_edit_content, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more:
                if (btn_camera.isEnabled()) {
                    showSelectPicPop();
                } else {
                    ShowToast("只允许发送一张图片");
                }
                break;
            case R.id.btn_voice:
                if (layout_voice.getVisibility() == View.GONE) {
                    layout_voice.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.INVISIBLE);
                } else {
                    layout_voice.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.INVISIBLE);
                }

            case R.id.btn_emo:
                if (layout_emo.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    showEditState(false);
                }
                break;
            default:
                break;
        }
    }



    /**
     * 显示选择图片的PopupWindow
     */
    private void showSelectPicPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
                null);
        final RelativeLayout layout_choose = (RelativeLayout) view
                .findViewById(R.id.layout_choose);
        final RelativeLayout layout_photo = (RelativeLayout) view
                .findViewById(R.id.layout_photo);
        avatorPop = new PopupWindow(view, mScreenWidth, 600);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });
        layout_choose
                .setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddActivity.this,
                                ChosePicActivity.class);
                        startActivityForResult(intent,
                                BmobConstancts.UPLOADIMAGE_LOCATION);
                    }
                });
        // 拍照
        layout_photo
                .setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        File dir = new File(BmobConstancts.MyUploadDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        // 拍照图片的路径
                        filename=new SimpleDateFormat(
                                "yyMMddHHmmss").format(new Date())
                                + "."
                                + Bitmap.CompressFormat.PNG;
                        File file = new File(dir, filename);
                        filePath = file.getAbsolutePath();// 获取相片的保存路径
                        Uri imageUri = Uri.fromFile(file);
                        // 打开相机
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent,
                                BmobConstancts.UPLOADIMAGE_CAMERA);
                    }
                });
        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param @param isEmo: 用于区分文字和表情
     */
    private void showEditState(boolean isEmo) {
        et_edit_content.setVisibility(View.VISIBLE);
        et_edit_content.requestFocus();
        if (isEmo) {
            layout_emo.setVisibility(View.VISIBLE);
            hideSoftInputView();
        } else {
            layout_emo.setVisibility(View.GONE);
            showSoftInputView();
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
            // 拍照上传的图片返回
            case BmobConstancts.UPLOADIMAGE_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (!CommonUtil.checkSdCard()) {
                        ShowToast("SD不可用");
                        return;
                    }
                    File file = new File(filePath);
                    startImageAction(BmobConstancts.UPLOADIMAGE_CROP,
                            Uri.fromFile(file), 200, 200, true);
                }
                break;
            // 裁剪头像返回
            case BmobConstancts.UPLOADIMAGE_CROP:
                if (data == null) {
                    ShowToast("无数据");
                    return;
                }
                showImageOnView(data);
                break;
        }
    }

    AlertDialog.Builder builder;
    public void showDialog(){
        builder = new AlertDialog.Builder(
                AddActivity.this);
        builder.setTitle("列表选择框");
        builder.setItems(
                mItems,
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == 0) {
                            Intent intent = new Intent(
                                    AddActivity.this,
                                    ImageBrowserActivity.class);
                            ArrayList<String> photos = new ArrayList<String>();
                            photos.add("file://"+filePath);
                            intent.putStringArrayListExtra(
                                    "photos", photos);
                            intent.putExtra("position", 0);
                            startActivity(intent);
                        } else {
                            layout_pics.removeView(imageView);
                            uploadImagePaths.clear();
                            btn_camera.setEnabled(true);
                        }
                    }
                });
    }


    /**
     * 将拍照返回的照片显示在界面上
     *
     * @param data
     */
    private void showImageOnView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                // 动态地往layout里添加控件
                final ImageView imageView = new ImageView(this);
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
                btn_camera.setEnabled(false);
                avatorPop.dismiss();
                // 得到要上传文件的完整路径名
                uploadImagePath = filePath;
                uploadImagePaths.add(uploadImagePath);
            }
        }
    }

    /**
     * 根据图片的路径生成的位图
     */
    protected Bitmap bitmap;
    /**
     * 通过当前时间生成的文件名
     */
    public String filename;

    public List<String> filenames=new ArrayList<String>();
    /**
     * 动态添加的图片
     */
    private ImageView imageView;
    final String[] mItems = { "查看大图", "取消选择" };
    /**
     * 从选择相片界面返回的图片url
     */
    String imgUrl;

    /**
     * 将从图库选择的图片显示界面上
     *
     * @param data
     */
    public void showImage(Intent data) {
        List<String> mSelectedImages = (List<String>) data.getSerializableExtra("mSelectedImages");
        // 将返回的图片显示在界面上
        ShowToast("选择了" + mSelectedImages.size() + "张图片");
        ShowToast(mSelectedImages.get(0));
        filePath=mSelectedImages.get(0);
        imageView = new ImageView(this);
        bitmap = BitmapFactory.decodeFile(filePath);
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
        layout_pics.addView(imageView, params);
        // 保存图片，记得自己添上后缀
        filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date())
                + "." + Bitmap.CompressFormat.PNG;
        // 拼凑要上传文件的完整路径名
        uploadImagePath = BmobConstancts.MyUploadDir + filename;
        uploadImagePaths.add(uploadImagePath);


//        int i=0;
//        for (String filename : mSelectedImages) {
//            imageView = new ImageView(this);
//            bitmap = BitmapFactory.decodeFile(filename);
//            imageView.setImageBitmap(bitmap);
//            // 图片点击的时候显示大图
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showDialog();
//                    builder.create().show();
//                }
//            });
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    PixelUtil.dp2px(60), PixelUtil.dp2px(60));
//            params.setMargins(PixelUtil.dp2px(10), 0, 0, 0);
//            layout_pics.addView(imageView, params);
//
//            // 保存图片，记得自己添上后缀
//            filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date())
//                    +i+ "." + Bitmap.CompressFormat.PNG;
//            Log.i(TAG, "filename=" + filename);
//            filenames.add(filename);
//            // 拼凑要上传文件的完整路径名
//            uploadImagePath = BmobConstancts.MyUploadDir + filename;
//            Log.i(TAG, "uploadImagePath="+uploadImagePath);
//            uploadImagePaths.add(uploadImagePath);
//        }
        avatorPop.dismiss();

    }

    public void startImageAction(int requestCode, Uri uri, int outputX,
                                 int outputY, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }
}

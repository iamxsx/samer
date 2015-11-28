package com.xsx.samer.ui;

import android.app.ProgressDialog;
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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.xsx.samer.R;
import com.xsx.samer.adapter.MyPostAdapter;
import com.xsx.samer.config.BmobConstancts;
import com.xsx.samer.fragment.MyInfoFragment;
import com.xsx.samer.fragment.MyPostFragment;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.User;
import com.xsx.samer.utils.CollectionUtil;
import com.xsx.samer.utils.CommonUtil;
import com.xsx.samer.utils.ImageLoadOptions;
import com.xsx.samer.utils.PhotoUtil;
import com.xsx.samer.utils.PixelUtil;
import com.xsx.samer.widget.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.xsx.samer.R.id.tabs;

/**
 * 个人中心
 * Created by XSX on 2015/10/14.
 */
public class MyDetailActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "MyDetailActivity";
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    /**
     * 个人中心的背景图
     */
    private ImageView backdrop;
    /**
     * 圆形头像
     */
    private CircleImageView iv_avatar;

    private String filePath;
    private String avatorPath;
    private int pos;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private MyInfoFragment mMyInfoFragment;
    private MyPostFragment mMyPostFragment;
    private User currentUser;
    private String from;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.mine_detail);
        from=getIntent().getStringExtra("from");
        if(from==null||from.equals("me")){
            currentUser= (User) userManager.getCurrentUser(User.class);
        }else if("other".equals(from) ||"friend".equals(from)){
            currentUser= (User) getIntent().getSerializableExtra("target_user");
        }
        initViews();
        //initDatas();
    }



    private void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentUser.getNick());
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();

        collapsingToolbar= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        backdrop= (ImageView) findViewById(R.id.backdrop);
        //设置个人中心背景图
        if(currentUser.getBgUrl()!=null){
            ImageLoader.getInstance().displayImage(currentUser.getBgUrl(), backdrop);
        }
        //设置头像
        iv_avatar= (CircleImageView) findViewById(R.id.iv_avatar);
        if(currentUser.getAvatar()!=null){
            ImageLoader.getInstance().displayImage(currentUser.getAvatar(), iv_avatar);
        }else{
            if(currentUser.getSex().equals("男")){
                iv_avatar.setImageResource(R.mipmap.male_default_icon);
            }else{
                iv_avatar.setImageResource(R.mipmap.female_default_icon);

            }
        }
        iv_avatar.setOnClickListener(this);
        backdrop.setOnClickListener(this);


        mViewPager= (ViewPager) findViewById(R.id.viewpager);
        mTabLayout= (TabLayout) findViewById(tabs);
        //与viewpager相关联

        mMyInfoFragment=new MyInfoFragment();
        mMyPostFragment=new MyPostFragment();
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(mMyInfoFragment, "个人信息");
        String s;
        if(from.equals("me")){
            s="我发的帖子";
        }else{
            s="他的帖子";
        }
        adapter.addFragment(mMyPostFragment, s);
        mViewPager.setAdapter(adapter);
        //控制viewpager可以显示的页面数，阻止fragment刷新
        mViewPager.setOffscreenPageLimit(1);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * ViewPager的适配器
     */
    class MyPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments = new ArrayList<Fragment>();
        List<String> fragmentTitles = new ArrayList<String>();


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_avatar:
                pos=0;
                showUpdateDialog();
                break;
            case R.id.backdrop:
                pos=1;
                showUpdateDialog();
                break;
        }
    }


    final String[] mItems = { "拍照","本地相片" };
    final String[] mItems2 = { "本地相片" };
    AlertDialog.Builder builder;

    /**
     */
    public void showUpdateDialog(){
        builder = new AlertDialog.Builder(
                MyDetailActivity.this);
        if(pos==0){
            builder.setTitle("更改头像");
            builder.setItems(
                    mItems,
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            //拍照
                            if (which == 0) {
                                File dir = new File(BmobConstancts.MyAvatarDir);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                // 原图
                                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                                        .format(new Date()));
                                filePath = file.getAbsolutePath();// 获取相片的保存路径
                                Uri imageUri = Uri.fromFile(file);
                                //打开相机
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent,
                                        BmobConstancts.REQUESTCODE_UPLOADAVATAR_CAMERA);
                            }
                            //图库选择
                            else {
                                Intent intent = new Intent(MyDetailActivity.this,
                                        ChosePicActivity.class);
                                startActivityForResult(intent, BmobConstancts.REQUESTCODE_UPLOADAVATAR_LOCATION);
                            }
                        }
                    });
        }else{
            builder.setTitle("更改相片墙");
            builder.setItems(
                    mItems2,
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            //拍照
                            if (which == 0) {
                                Intent intent = new Intent(MyDetailActivity.this,
                                        ChosePicActivity.class);
                                startActivityForResult(intent, BmobConstancts.REQUESTCODE_UPLOADAVATAR_LOCATION);
                            }
                        }
                    });
        }

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //裁剪头像返回
            case BmobConstancts.REQUESTCODE_UPLOADAVATAR_CROP:
                if (data == null) {
                    ShowToast("无数据");
                    return;
                }
                // 保存剪切后的图片到内存
                saveCropAvator(data);
                uploadAvator();
                break;

            case BmobConstancts.REQUESTCODE_UPLOADAVATAR_LOCATION:
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
                    saveImage(data);
                    //修改头像
                    if(pos==0){
                        uploadAvator();
                    }
                    //修改相片墙
                    else{
                        uploadAvatorBg();
                    }

                }
                break;
            //拍照上传的图片返回
            case BmobConstancts.REQUESTCODE_UPLOADAVATAR_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (!CommonUtil.checkSdCard()) {
                        ShowToast("SD不可用");
                        return;
                    }
                    File file = new File(filePath);
                    startImageAction(BmobConstancts.REQUESTCODE_UPLOADAVATAR_CROP,Uri.fromFile(file), 200, 200,
                            true);
                }
                break;

        }
    }

    private void uploadAvatorBg() {
        // 得到要上传的图片
        //找到的文件没有后缀
        final BmobFile file = new BmobFile(new File(avatorPath));
        file.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                // 得到上传图片的路径
                String fileUrl = file.getFileUrl(MyDetailActivity.this);
                // 更新用户的头像
                updateUserAvatorBg(fileUrl);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("相片墙上传失败");
            }
        });

    }

    private void updateUserAvatorBg(final String fileUrl) {
        currentUser.setBgUrl(fileUrl);
        currentUser.update(MyDetailActivity.this, new UpdateListener() {
            @Override
            public void onSuccess() {
                ShowToast("更新头像成功 ");
                // 刷新用户头像
                refreshUserAvatorBg(fileUrl);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("更新头像失败 " + arg1);
            }
        });
    }

    public void refreshUserAvatorBg(String fileUrl){
        if (fileUrl != null && !fileUrl.equals("")) {
            ImageLoader.getInstance().displayImage(fileUrl, backdrop,
                    ImageLoadOptions.getOptions());
        } else {
            backdrop.setImageResource(R.mipmap.ic_launcher);
        }
    }

    /**
     * 将从图库选择的图片显示界面上
     *
     * @param data
     */
    public void saveImage(Intent data) {
        List<String> mSelectedImages = (List<String>) data.getSerializableExtra("mSelectedImages");
        // 将返回的图片显示在界面上
        ShowToast("选择了" + mSelectedImages.size() + "张图片");
        Log.i(TAG,"imageUrl="+mSelectedImages.get(0));
        String filename = new SimpleDateFormat("yyMMddHHmmss")
                .format(new Date())+"."+Bitmap.CompressFormat.PNG;
        Bitmap bitmap = BitmapFactory.decodeFile(mSelectedImages.get(0));
        //这里少了后缀
        avatorPath = BmobConstancts.MyAvatarDir + filename;
        PhotoUtil.saveBitmap(BmobConstancts.MyAvatarDir, filename,
                bitmap, true);
    }

    /**
     * 上传头像的文件到服务器
     */
    private void uploadAvator() {
        // 得到要上传的图片
        //找到的文件没有后缀
        final BmobFile file = new BmobFile(new File(avatorPath));
        file.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                // 得到上传图片的路径
                String fileUrl = file.getFileUrl(MyDetailActivity.this);
                // 更新用户的头像
                updateUserAvator(fileUrl);
            }
            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("头像上传失败");
            }
        });

    }

    /**
     * 根据上传的头像路径更新头像
     *
     * @param fileUrl
     */
    protected void updateUserAvator(final String fileUrl) {
        currentUser.setAvatar(fileUrl);
        currentUser.update(MyDetailActivity.this, new UpdateListener() {
            @Override
            public void onSuccess() {
                ShowToast("更新头像成功 ");
                // 刷新用户头像
                refreshUserAvator(fileUrl);
            }
            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("更新头像失败 " + arg1);
            }
        });
    }

    /**
     * 刷新头像
     *
     * @param fileUrl
     */
    protected void refreshUserAvator(String fileUrl) {
        if (fileUrl != null && !fileUrl.equals("")) {
            ImageLoader.getInstance().displayImage(fileUrl, iv_avatar,
                    ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.mipmap.ic_launcher);
        }
    }

    /**
     * 保存剪切后的图片到sd卡
     *
     * @param data
     * @throws FileNotFoundException
     */
    private void saveCropAvator(Intent data){
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                iv_avatar.setImageBitmap(bitmap);
                // 保存图片，记得自己添上后缀
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date())+"."+Bitmap.CompressFormat.PNG;
                //这里少了后缀
                avatorPath = BmobConstancts.MyAvatarDir + filename;
                PhotoUtil.saveBitmap(BmobConstancts.MyAvatarDir, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }

    }

    /**
     * 为intent设置相应属性并启动activity
     * @param requestCode
     * @param uri
     * @param outputX
     *            裁剪图片宽高
     * @param outputY
     * @param isCrop
     *            crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
     */
    public void startImageAction(int requestCode, Uri uri,
                                 int outputX, int outputY, boolean isCrop) {
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

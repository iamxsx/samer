package com.xsx.samer.ui;

import java.io.Serializable;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.xsx.samer.R;
import com.xsx.samer.config.BmobConstancts;
import com.xsx.samer.model.Post;
import com.xsx.samer.model.Topic;
import com.xsx.samer.model.User;
import com.xsx.samer.utils.CollectionUtil;
import com.xsx.samer.utils.PhotoUtil;
import com.xsx.samer.utils.TimeUtil;

/**
 * 发布一个帖子
 * 帖子分两种：
 * 1.发布在主界面的帖子
 * 2.发布在各个话题版块里的帖子
 * 添加时将帖子添加到所属的版块中
 *
 * @author XSX
 */
public class AddPostActivity extends AddActivity {
    private static final String TAG = "AddPartTimeJobActivity";
    private Post post;
    private Toolbar toolbar;
    private Topic topic;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_item);
        initView();
    }

    private void initView() {
        super.initViews();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //标题的文字要在setSupportActionBar之前，否则会无效
        toolbar.setTitle("发表帖子");
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
                String content = et_edit_content.getText().toString();
                if ("".equals(content) || "".equals(content.trim())) {
                    ShowToast("内容不能为空");
                }
                send();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    ProgressBar progressBar;

    private void send() {
        topic= (Topic) getIntent().getSerializableExtra("topic");
        boolean isMain=getIntent().getBooleanExtra("isMain",false);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        User currentUser = userManager.getCurrentUser(User.class);
        post = new Post();
        if(topic!=null){
            post.setTopic(topic);
        }
        if (isMain){
            post.setIsMain(true);
        }
        post.setPraiseCount(0);
        post.setAuthor(currentUser);
        post.setTime(TimeUtil
                .getDescriptionTimeFromTimestamp(new Date().getTime()));
        post.setAvator(currentUser.getAvatar());
        post.setContent(et_edit_content.getText().toString());
        post.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ShowToast("发布成功");
                Log.i(TAG, "uploadImagePaths="+uploadImagePaths);
                if (uploadImagePaths != null && uploadImagePaths.size() > 0) {
                    uploadImage();
                }
                setResult(200);
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                ShowToast("发布失败" + arg1);
            }
        });
    }

    /**
     * 图片上传并发布
     */
    private void uploadImage() {
        // 先将图片保存到sd卡
        //for (String filename : filenames) {
            PhotoUtil.saveBitmap(BmobConstancts.MyUploadDir, filename, bitmap, true);
        //}
        String[] files = uploadImagePaths.toArray(new String[uploadImagePaths
                .size()]);
        BmobProFile.getInstance(this).uploadBatch(files,
                new UploadBatchListener() {
                    @Override
                    public void onSuccess(boolean isFinish, String[] fileNames,
                                          String[] urls, BmobFile[] files) {
                        if (isFinish) {
                            for (int i = 0; i < files.length; i++) {
                                uploadImageUrls
                                        .add(files[i].getFileUrl(AddPostActivity.this));
                            }
                            post.setImages(uploadImageUrls);
                            post.update(AddPostActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    ShowToast("文字加图发布成功");
                                }

                                @Override
                                public void onFailure(int arg0, String arg1) {
                                    ShowToast("发布失败" + arg1);
                                }
                            });
                        }
                    }

                    @Override
                    public void onProgress(int curIndex, int curPercent,
                                           int total, int totalPercent) {
                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        Log.i("bmob", "批量上传出错：" + statuscode + "--" + errormsg);
                    }
                });
    }

}
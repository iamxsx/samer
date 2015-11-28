package com.xsx.samer.config;

import android.os.Environment;

/**
 * Created by XSX on 2015/10/11.
 */
public class BmobConstancts {

    /**
     * 存放发送图片的目录
     */
    public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory()	+ "/bmobimdemo/image/";

    /**
     * 我的头像保存目录
     */
    public static String MyAvatarDir = "/sdcard/samer/avatar/";
    /**
     * 上传图片文件的保存目录
     */
    public static String MyUploadDir="/sdcard/samer/upload/";
    /**
     * 代表拍照上传图片
     */
    public static final int UPLOADIMAGE_CAMERA = 10;
    /**
     * 代表本地相册选择上传图片
     */
    public static final int UPLOADIMAGE_LOCATION = 20;
    /**
     * 代表系统裁剪图片
     */
    public static final int UPLOADIMAGE_CROP = 30;//系统裁剪头像
    /**
     * 代表拍照修改头像
     */
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
    /**
     * 代表本地相册修改头像
     */
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
    /**
     * 代表系统裁剪头像
     */
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像
    /**
     * 拍照
     */
    public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
    /**
     * 本地图片
     */
    public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
    /**
     * 位置
     */
    public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置
    public static final String EXTRA_STRING = "extra_string";


    public static final String ACTION_REGISTER_SUCCESS_FINISH ="register.success.finish";//注册成功之后登陆页面退出

}

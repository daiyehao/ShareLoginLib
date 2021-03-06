package com.liulishuo.share;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by echo on 5/18/15.
 *
 * QQ sdk:
 *
 * @see :"http://wiki.open.qq.com/wiki/mobile/SDK%E4%B8%8B%E8%BD%BD"
 */
public class ShareBlock {

    private static Config config;

    private ShareBlock() {
    }

    public static void init(Application application, @NonNull Config cfg) {
        config = cfg;

        if (TextUtils.isEmpty(Config.pathTemp)) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                try {
                    Config.pathTemp = application.getExternalCacheDir() + File.separator;
                    File dir = new File(Config.pathTemp);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } catch (Exception e) {
                    if (Config.isDebug) {
                        throw e;
                    }
                    Config.pathTemp = null;
                }
            }
        }
    }

    public static Config getConfig() {
        return config;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 判断第三方客户端是否安装
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isQQInstalled(@NonNull Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        if (pm == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if ("com.tencent.mobileqq".equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        IWeiboShareAPI shareAPI = WeiboShareSDK.createWeiboAPI(context, Config.weiBoAppId);
        return shareAPI.isWeiboAppInstalled();
    }

    public static boolean isWeiXinInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, Config.weiXinAppId, true);
        return api.isWXAppInstalled();
    }

    public static class Config {

        private static boolean isDebug = false;

        public static String appName;

        public static String pathTemp = null;

        public static String weiXinAppId;

        public static String weiXinSecret;

        public static String weiBoAppId;

        public static String weiBoRedirectUrl;

        public static String weiBoScope;

        public static String qqAppId;

        public static String qqScope;

        private static Config mInstance = null;

        public static Config getInstance() {
            if (mInstance == null) {
                mInstance = new Config();
            }
            return mInstance;
        }

        public Config appName(@NonNull String appName) {
            Config.appName = appName;
            return this;
        }

        /**
         * 初始化临时文件地址，这里仅仅是为了qq分享用的。
         * 这里必须用外部存储器，因为qq会读取这个目录下的图片文件!!!
         */
        public Config picTempFile(@Nullable String tempPath) {
            pathTemp = tempPath;
            return this;
        }

        public Config debug(boolean debug) {
            isDebug = debug;
            return this;
        }

        public Config qq(@NonNull String qqAppId, @NonNull String scope) {
            Config.qqAppId = qqAppId;
            qqScope = scope;
            return this;
        }

        public Config weiBo(@NonNull String weiBoAppId, @NonNull String redirectUrl, @NonNull String scope) {
            Config.weiBoAppId = weiBoAppId;
            weiBoRedirectUrl = redirectUrl;
            weiBoScope = scope;
            return this;
        }

        public Config weiXin(@NonNull String weiXinAppId, @NonNull String weiXinSecret) {
            Config.weiXinAppId = weiXinAppId;
            Config.weiXinSecret = weiXinSecret;
            return this;
        }
    }

}

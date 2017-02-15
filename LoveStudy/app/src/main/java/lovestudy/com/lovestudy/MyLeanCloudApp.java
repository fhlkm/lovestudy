package lovestudy.com.lovestudy;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;


public class MyLeanCloudApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"ihgnVPv2AVbqa5wEhnN05Bux-gzGzoHsz","xYke2ooHVcACLVnziDjVbJPF");
    }
}

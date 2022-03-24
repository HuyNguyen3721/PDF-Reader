//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package office.file.ui;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.ez.EzApplication;

import viewx.j.b;

public class MyLibApplication extends EzApplication {
    private static Context context;

    public MyLibApplication() {
    }

    private void a() {
        BaseOpenFileActivity.b();
        BaseOpenFileActivity.c();
    }

    public static Context getAppContext() {
        return context;
    }

    public void onCreate() {
        this.a();
        super.onCreate();
        context = this;
    }
}

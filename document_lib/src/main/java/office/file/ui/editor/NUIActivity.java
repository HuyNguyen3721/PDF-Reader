package office.file.ui.editor;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.KeyEvent;

import com.artifex.solib.ConfigOptions;
import com.artifex.solib.k;

import office.file.ui.editor.NUIView.OnDoneListener;
import office.file.ui.editor.R.id;
import office.file.ui.editor.R.layout;
import office.file.ui.editor.R.string;
import office.file.ui.editor.SODocSession.SODocSessionLoadListenerCustom;

public class NUIActivity extends BaseActivity {
    private static SODocSession useSession;
    private Intent mChildIntent = null;
    private Configuration mLastConfiguration;
    private int mLastKeyCode = -1;
    private long mLastKeyTime = 0L;
    protected NUIView mNUIView;

    public NUIActivity() {
    }

    private void a() {
        try {
            Intent var1 = this.getIntent();
            boolean var3;
            Bundle var2 = var1 != null ? var1.getExtras() : null;
            if (var2 != null) {
                var3 = var2.getBoolean("SESSION", false);
                this.setCustomConfiguration(var1);
            } else {
                var3 = false;
            }

            if (var3 && useSession == null) {
                super.finish();
            } else {
                this.a(var1, false);
            }
        } catch (Exception ex) {
            finish();
        }
    }

    private void a(Intent var1, boolean var2) {
        Bundle var3 = var1.getExtras();
        if (k.c().B()) {
            this.mNUIView = null;
            Utilities.showMessageAndFinish(this, this.getString(string.sodk_editor_error), this.getString(string.sodk_editor_has_no_permission_to_open));
        } else {
            boolean var4 = false;
            boolean var5;
            if (var3 != null) {
                var5 = var3.getBoolean("SESSION", false);
//                var5 = false;
            } else {
                var5 = false;
            }

            SODocSession var6 = useSession;
            this.setContentView(layout.sodk_editor_doc_view_activity);
            NUIView var7 = (NUIView) this.findViewById(id.doc_view);
            this.mNUIView = var7;
            var7.setOnDoneListener(new OnDoneListener() {
                public void done() {
//                    if(!Objects.requireNonNull(MainActivity.Companion.getInstance()).isMainRunning()){
                    /*if (MainActivity.Companion.getInstance() == null) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }*/
                    NUIActivity.super.finish();
                }
            });
            int var8;
            String var9;
            String var11;
            SOFileState var13;
            String var16;
            if (var3 != null) {
                boolean var10;
                label35:
                {
                    var8 = var3.getInt("START_PAGE");
                    SOFileState var15 = SOFileState.fromString(var3.getString("STATE"), SOFileDatabase.getDatabase());
                    var9 = var3.getString("FOREIGN_DATA", (String) null);
                    var10 = var3.getBoolean("IS_TEMPLATE", true);
                    var11 = var3.getString("CUSTOM_DOC_DATA");
                    var13 = var15;
                    if (var15 == null) {
                        var13 = null;
                        if (!var2) {
                            var15 = SOFileState.getAutoOpen(this);
                            var13 = var15;
                            if (var15 != null) {
                                var2 = var4;
                                var13 = var15;
                                break label35;
                            }
                        }
                    }

                    var2 = var5;
                }

                var16 = var11;
                var5 = var2;
                var2 = var10;
            } else {
                var11 = null;
                var16 = null;
                var2 = true;
                var8 = 1;
                var9 = null;
                var13 = null;
            }

            if (var16 == null) {
                Utilities.setSessionLoadListener((SODocSessionLoadListenerCustom) null);
            }

            if (var5) {
                this.mNUIView.start(var6, var8, var9);
            } else if (var13 != null) {
                this.mNUIView.start(var13, var8);
            } else {
                Uri var14 = var1.getData();
                String var12 = var1.getType();
                this.mNUIView.start(var14, var2, var8, var16, var12);
            }

            this.checkIAP();
        }
    }

    private void a(Configuration var1) {
        if (VERSION.SDK_INT >= 28 && var1.uiMode != this.mLastConfiguration.uiMode) {
            this.onPause();
            super.finish();
            this.startActivity(this.getIntent());
        }

        this.mLastConfiguration = this.getResources().getConfiguration();
    }

    public static void setSession(SODocSession var0) {
        useSession = var0;
    }

    protected void checkIAP() {
    }

    public Intent childIntent() {
        return this.mChildIntent;
    }

    protected void doResumeActions() {
        NUIView var1 = this.mNUIView;
        if (var1 != null) {
            var1.onResume();
        }

        this.a(this.getResources().getConfiguration());
    }

    public void finish() {
        if (this.mNUIView == null) {
            super.finish();
        } else {
            Utilities.dismissCurrentAlert();
            this.onBackPressed();
        }

    }

    protected void initialise() {
        this.a();
    }

    public boolean isDocModified() {
        NUIView var1 = this.mNUIView;
        return var1 != null && var1.isDocModified();
    }

    protected void onActivityResult(int var1, int var2, Intent var3) {
        NUIView var4 = this.mNUIView;
        if (var4 != null) {
            var4.onActivityResult(var1, var2, var3);
        }

    }

    public void onBackPressed() {
        NUIView var1 = this.mNUIView;
        if (var1 != null) {
            var1.onBackPressed(false);
        }
    }

    public void onConfigurationChanged(Configuration var1) {
        super.onConfigurationChanged(var1);
        this.a(var1);
        this.mNUIView.onConfigurationChange(var1);
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.mLastConfiguration = this.getResources().getConfiguration();
        this.initialise();
    }

    protected void onDestroy() {
        //sendBroadcast(new Intent(ActivityExtKt.getSHOW_RATE()));
        NUIView var1 = this.mNUIView;
        if (var1 != null) {
            var1.onDestroy();
        }
        super.onDestroy();

    }

    public boolean onKeyDown(int var1, KeyEvent var2) {
        long var3 = var2.getEventTime();
        if (this.mLastKeyCode == var1 && var3 - this.mLastKeyTime <= 100L) {
            return true;
        } else {
            this.mLastKeyTime = var3;
            this.mLastKeyCode = var1;
            return this.mNUIView.doKeyDown(var1, var2);
        }
    }

    public void onNewIntent(final Intent var1) {
        k.c();
        if (this.isDocModified()) {
            Utilities.yesNoMessage(this, this.getString(string.sodk_editor_new_intent_title), this.getString(string.sodk_editor_new_intent_body), this.getString(string.sodk_editor_new_intent_yes_button), this.getString(string.sodk_editor_new_intent_no_button), new Runnable() {
                public void run() {
                    if (NUIActivity.this.mNUIView != null) {
                        NUIActivity.this.mNUIView.endDocSession(true);
                    }

                    NUIActivity.this.setCustomConfiguration(var1);
                    NUIActivity.this.a(var1, true);
                }
            }, new Runnable() {
                public void run() {
                    SODocSessionLoadListenerCustom var1 = Utilities.getSessionLoadListener();
                    if (var1 != null) {
                        var1.onSessionReject();
                    }

                    Utilities.setSessionLoadListener((SODocSessionLoadListenerCustom) null);
                }
            });
        } else {
            NUIView var2 = this.mNUIView;
            if (var2 != null) {
                var2.endDocSession(true);
            }

            this.setCustomConfiguration(var1);
            this.a(var1, true);
        }

    }

    public void onPause() {
        NUIView var1 = this.mNUIView;
        if (var1 != null) {
            var1.onPause();
            this.mNUIView.releaseBitmaps();
        }

        if (Utilities.isChromebook(this)) {
            PrintHelperPdf.setPrinting(false);
        }

        super.onPause();
    }

    protected void onResume() {
        this.mChildIntent = null;
        super.onResume();
        this.doResumeActions();
    }


    protected void setConfigurableButtons() {
        NUIView var1 = this.mNUIView;
        if (var1 != null) {
            var1.setConfigurableButtons();
        }

    }

    protected void setCustomConfiguration(Intent var1) {
        try {
            ConfigOptions var2 = k.c();
            Log.e("TAGGGGGGGGGGGGGG", "setCustomConfiguration: " + var2);

            Bundle var3 = var1.getExtras();
            if (var1.hasExtra("ENABLE_SAVE")) {
                var2.o(var3.getBoolean("ENABLE_SAVE", true));
            }

            if (var1.hasExtra("ENABLE_SAVEAS")) {
                var2.b(var3.getBoolean("ENABLE_SAVEAS", true));
            }

            if (var1.hasExtra("ENABLE_SAVEAS_PDF")) {
                var2.c(var3.getBoolean("ENABLE_SAVEAS_PDF", true));
            }

            if (var1.hasExtra("ENABLE_CUSTOM_SAVE")) {
                var2.p(var3.getBoolean("ENABLE_CUSTOM_SAVE", true));
            }

            if (var1.hasExtra("ALLOW_AUTO_OPEN")) {
                var2.r(var3.getBoolean("ALLOW_AUTO_OPEN", true));
            }

        } catch (Exception ex) {

        }
    }

    public void startActivity(Intent var1) {
        this.mChildIntent = var1;
        super.startActivity(var1, (Bundle) null);
    }

    public void startActivityForResult(Intent var1, int var2) {
        this.mChildIntent = var1;
        super.startActivityForResult(var1, var2);
    }

    public void startActivityForResult(Intent var1, int var2, Bundle var3) {
        this.mChildIntent = var1;
        super.startActivityForResult(var1, var2, var3);
    }
}

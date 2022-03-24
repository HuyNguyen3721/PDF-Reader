package office.file.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.ez.EzAdControl;

public class OpenFileActivity extends BaseOpenFileActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        EzAdControl.getInstance(this).showAds();
        Uri uri = getIntent().getData();
        String path = uri.getPath();
        /*setTheme(R.style.ThemeTextDark);

        if (path.endsWith(FileExtKt.getDOC()) || path.endsWith(FileExtKt.getDOCX())) {
            setStatusBarColor(R.color.color_doc_toolbar);

        } else if (path.endsWith(FileExtKt.getXLS()) || path.endsWith(FileExtKt.getXLSX())) {
            setStatusBarColor(R.color.color_xls_toolbar);


        } else if (path.endsWith(FileExtKt.getPPT()) || path.endsWith(FileExtKt.getPPTX())) {
            setStatusBarColor(R.color.color_ppt_toolbar);

        } else if (path.endsWith(FileExtKt.getPDF())) {
            setStatusBarColor(R.color.color_pdf_toolbar);

        } else {
            setStatusBarColor(R.color.color_other_toolbar);
        }*/

        super.onCreate(bundle);
    }


    private void setStatusBarColor(@ColorRes int colorResId) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, colorResId));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package io.blacklagoonapps.myquotes;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends ThemedActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView licenseView = (TextView) findViewById(R.id.textview_about_licenses);
        licenseView.setPaintFlags(licenseView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        TextView versionView = (TextView)findViewById(R.id.textview_about_version_number);
        versionView.setText(BuildConfig.VERSION_NAME);
    }

    public void onRateUsClick(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + this.getPackageName()));

        try{
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    public void onOpenSourceLicensesClick(View view){
        Intent intent = new Intent(this, LicensesActivity.class);
        startActivity(intent);
    }
}

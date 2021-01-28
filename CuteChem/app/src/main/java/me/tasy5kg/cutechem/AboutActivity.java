package me.tasy5kg.cutechem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        MaterialToolbar toolbar = findViewById(R.id.material_about_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MaterialTextView) findViewById(R.id.about_material_text_view_version))
                .setText(getString(R.string.version, BuildConfig.VERSION_NAME));
        findViewById(R.id.about_donate_button).setOnClickListener(this);
        findViewById(R.id.about_rate_button).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.about_donate_button) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://qr.alipay.com/fkx19871rnswjfeth8a9tfb")));
            Toast.makeText(this,
                    getString(R.string.donate_i_am_happy_to_hear),
                    Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.about_rate_button) {
            try {
                startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id="
                                        + getPackageName()))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
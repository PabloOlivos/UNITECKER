package com.aztech.unitecker.activity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.aztech.unitecker.R;
import com.aztech.unitecker.identities.StickerPacksContainer;
import com.aztech.unitecker.utils.StickerPacksManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AddStickerPackActivity extends AppCompatActivity {

    Uri stickerUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_sticker_pack);
        this.stickerUri = this.getIntent().getData();
        StickerPacksManager.stickerPacksContainer = new StickerPacksContainer("", "", StickerPacksManager.getStickerPacks(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(this, stickerUri.getPath(), Toast.LENGTH_LONG).show();

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setMinimumWidth(AdSize.FULL_WIDTH);
        adView.setAdUnitId(getString(R.string.banner1));
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
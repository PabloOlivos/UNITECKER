package com.unitecker.app.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;
import com.orhanobut.hawk.Hawk;
import com.unitecker.app.Manager.PrefManager;
import com.unitecker.app.R;
import com.unitecker.app.StickerPack;
import com.unitecker.app.entity.CategoryApi;
import com.unitecker.app.ui.StickerDetailsActivity;
import com.unitecker.app.ui.UserActivity;
import com.unitecker.app.api.apiClient;
import com.unitecker.app.api.apiRest;
import com.unitecker.app.entity.ApiResponse;
import com.unitecker.app.entity.PackApi;
import com.unitecker.app.entity.SlideApi;
import com.unitecker.app.entity.UserApi;
import com.unitecker.app.ui.views.ClickableViewPager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.unitecker.app.MainActivity;

public class StickerAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private InterstitialAd admobInterstitialAd;

    private View selected_view = null;
    private int selected_position = -1;
    private Intent selected_intent = null;

    private  List<CategoryApi> categoryList = new ArrayList<>();
    public  Boolean favorite =  false;
    // lists
    List<com.unitecker.app.StickerPack> StickerPack;
    private List<SlideApi> slideList= new ArrayList<>();
    private  List<UserApi> userList = new ArrayList<>();
    private Dialog dialog_progress;

    // objects
    private Activity activity;
    private SlideAdapter slide_adapter;
    private FollowAdapter followAdapter;
    private MoPubInterstitial moPubInterstitial;

    private LinearLayoutManager linearLayoutManager;
    public StickerAdapter(Activity activity, ArrayList<StickerPack> StickerPack) {
        this.activity = activity;
        this.StickerPack = StickerPack;
    }
    public StickerAdapter(Activity activity, ArrayList<StickerPack> StickerPack,List<SlideApi> slideList) {
        this.activity = activity;
        this.StickerPack = StickerPack;
        this.slideList = slideList;
    }
    public StickerAdapter(Activity activity, ArrayList<StickerPack> StickerPack,List<SlideApi> slideList,List<CategoryApi> categoryList,Boolean b) {
        this.activity = activity;
        this.StickerPack = StickerPack;
        this.slideList = slideList;
        this.categoryList = categoryList;
    }
    public StickerAdapter(Activity activity, ArrayList<StickerPack> StickerPack,List<SlideApi> slideList,List<UserApi> userList){
        this.activity = activity;
        this.StickerPack = StickerPack;
        this.slideList = slideList;
        this.userList = userList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_pack, parent, false);
                viewHolder = new PackViewHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_slide, parent, false);
                viewHolder = new SlideHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_followings, parent, false);
                viewHolder = new FollowHolder(v3);
                break;
            }

            case 5: {
                View v5 = inflater.inflate(R.layout.item_categories, parent, false);
                viewHolder = new CategoriesHolder(v5 );
                break;
            }
            case 6: {
                View v6 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder = new AdmobNativeHolder(v6);
                break;
            }
            case 7: {
                View v7 = inflater.inflate(R.layout.item_mopub_view_parent, parent, false);
                viewHolder = new MoPubbNativeHolder(v7,parent);
                break;
            }
        }
        return viewHolder;
    }
    @Override
    public int getItemViewType(int position) {
        return StickerPack.get(position).getViewType();
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder_parent, final int position) {

        switch (StickerPack.get(position).getViewType()) {
            case 1: {

                final PackViewHolder viewHolder = (PackViewHolder) holder_parent;


                viewHolder.image_view_whatsapp.setVisibility((StickerPack.get(position).whatsapp.equals("false"))? View.GONE:View.VISIBLE);
                viewHolder.image_view_telegram.setVisibility((StickerPack.get(position).telegram.equals("false"))? View.GONE:View.VISIBLE);
                viewHolder.image_view_signal.setVisibility((StickerPack.get(position).signal.equals("false"))? View.GONE:View.VISIBLE);


                viewHolder.item_pack_name.setText(StickerPack.get(position).name);
                viewHolder.item_pack_publisher.setText(StickerPack.get(position).publisher);
                viewHolder.item_pack_downloads.setText(StickerPack.get(position).downloads);
                viewHolder.item_pack_size.setText(StickerPack.get(position).size);
                viewHolder.item_pack_created.setText(StickerPack.get(position).created);
                viewHolder.item_pack_username.setText(StickerPack.get(position).username);

                if (StickerPack.get(position).premium.equals("true")) {
                    viewHolder.item_pack_premium.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.item_pack_premium.setVisibility(View.GONE);
                }
                if (StickerPack.get(position).review.equals("true")) {
                    viewHolder.item_pack_review.setVisibility(View.VISIBLE);
                    viewHolder.item_pack_delete.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.item_pack_delete.setVisibility(View.GONE);
                }



                Glide.with(activity.getApplicationContext())
                        .load(StickerPack.get(position).userimage)
                        .into(viewHolder.pack_item_image_view_userimage);

                Glide.with(activity.getApplicationContext())
                        .load(StickerPack.get(position).trayImageUrl)
                        .into(viewHolder.pack_try_image);
                Glide.with(activity.getApplicationContext())
                        .load(StickerPack.get(position).getStickers().get(0).imageFileUrlThum)
                        .into(viewHolder.imone);

                Glide.with(activity.getApplicationContext())
                        .load(StickerPack.get(position).getStickers().get(1).imageFileUrlThum)
                        .into(viewHolder.imtwo);

                Glide.with(activity.getApplicationContext())
                        .load(StickerPack.get(position).getStickers().get(2).imageFileUrlThum)
                        .into(viewHolder.imthree);

                if (StickerPack.get(position).getStickers().size() > 3) {
                    Glide.with(activity.getApplicationContext())
                            .load(StickerPack.get(position).getStickers().get(3).imageFileUrlThum)
                            .into(viewHolder.imfour);
                } else {
                    viewHolder.imfour.setVisibility(View.INVISIBLE);
                }
                if (StickerPack.get(position).getStickers().size() > 4) {
                    Glide.with(activity.getApplicationContext())
                            .load(StickerPack.get(position).getStickers().get(4).imageFileUrlThum)
                            .into(viewHolder.imfive);
                } else {
                    viewHolder.imfive.setVisibility(View.INVISIBLE);
                }
                Log.e("trayImageUrl", StickerPack.get(position).trayImageUrl);
                Log.e("trayImageUrl", StickerPack.get(position).trayImageFile);

                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent((activity), StickerDetailsActivity.class).putExtra(MainActivity.EXTRA_STICKERPACK, StickerPack.get(viewHolder.getAdapterPosition()));
                        selected_position = position;
                        selected_intent = intent;
                        selected_view = v;
                        Operation();
                        /*


                        if(checkSUBSCRIBED()){
                            (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                        }else{
                            if( prefManager.getString("ADMIN_INTERSTITIAL_TYPE").equals("ADMOB")){
                                requestAdmobInterstitial();

                                if(prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS")<=prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")){
                                    if (admobInterstitialAd.isLoaded()) {
                                        prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",0);
                                        admobInterstitialAd.show();
                                        admobInterstitialAd.setAdListener(new AdListener() {
                                            @Override
                                            public void onAdClosed() {
                                                requestAdmobInterstitial();
                                                (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                            }
                                        });
                                    }else{
                                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                        requestAdmobInterstitial();
                                    }
                                }else{
                                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                    prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")+1);
                                }
                            }else if(prefManager.getString("ADMIN_INTERSTITIAL_TYPE").equals("FACEBOOK")){
                                requestFacebookInterstitial();
                                if(prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS")<=prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")){
                                    if (facebookInterstitialAd.isAdLoaded()) {
                                        prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",0);
                                        facebookInterstitialAd.show();

                                    }else{
                                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                        requestFacebookInterstitial();
                                    }
                                }else{
                                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                    prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")+1);
                                }
                            }else if(prefManager.getString("ADMIN_INTERSTITIAL_TYPE").equals("BOTH")){
                                requestAdmobInterstitial();
                                requestFacebookInterstitial();
                                if(prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS")<=prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")) {
                                    if (prefManager.getString("AD_INTERSTITIAL_SHOW_TYPE").equals("ADMOB")){
                                        if (admobInterstitialAd.isLoaded()) {
                                            prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",0);
                                            prefManager.setString("AD_INTERSTITIAL_SHOW_TYPE","FACEBOOK");
                                            admobInterstitialAd.show();
                                            admobInterstitialAd.setAdListener(new AdListener(){
                                                @Override
                                                public void onAdClosed() {
                                                    super.onAdClosed();
                                                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                                    requestFacebookInterstitial();
                                                }
                                            });
                                        }else{
                                            (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                            requestFacebookInterstitial();
                                        }
                                    }else{
                                        if (facebookInterstitialAd.isAdLoaded()) {
                                            prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",0);
                                            prefManager.setString("AD_INTERSTITIAL_SHOW_TYPE","ADMOB");
                                            facebookInterstitialAd.show();
                                            selected_position = position;
                                            selected_intent = intent;
                                            selected_view = v;
                                        }else{
                                            (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                            requestFacebookInterstitial();
                                        }
                                    }
                                }else{
                                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                                    prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")+1);
                                }
                            }else{
                                (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                            }


                        }*/
                    }
                });
                List<PackApi> favorites_list =Hawk.get("favorite");
                Boolean exist = false;
                if (favorites_list == null) {
                    favorites_list = new ArrayList<>();
                }

                for (int i = 0; i < favorites_list.size(); i++) {
                    if (favorites_list.get(i).getIdentifier().equals(StickerPack.get(position).packApi.getIdentifier())) {
                        exist = true;
                    }
                }
                if (exist){
                    viewHolder.image_view_item_pack_fav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite_black));
                }else{
                    viewHolder.image_view_item_pack_fav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite_border));
                }

                viewHolder.image_view_item_pack_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        List<PackApi> favorites_list =Hawk.get("favorite");
                        Boolean exist = false;
                        if (favorites_list == null) {
                            favorites_list = new ArrayList<>();
                        }
                        int fav_position = -1;
                        for (int i = 0; i < favorites_list.size(); i++) {
                            if (favorites_list.get(i).getIdentifier().equals(StickerPack.get(position).packApi.getIdentifier())) {
                                exist = true;
                                fav_position = i;
                            }
                        }
                        if (exist == false) {
                            favorites_list.add(StickerPack.get(position).packApi);
                            Hawk.put("favorite",favorites_list);
                            viewHolder.image_view_item_pack_fav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite_black));

                        }else{
                            favorites_list.remove(fav_position);
                            Hawk.put("favorite",favorites_list);
                            viewHolder.image_view_item_pack_fav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite_border));
                            if (favorite) {
                                StickerPack.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            }

                        }

                    }
                });
                viewHolder.image_view_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_progress= ProgressDialog.show(activity, null,activity.getResources().getString(R.string.operation_progress), true);
                        final PrefManager prf= new PrefManager(activity.getApplicationContext());
                        String user_id = prf.getString("ID_USER");
                        String user_key = prf.getString("TOKEN_USER");
                        Retrofit retrofit = apiClient.getClient();
                        apiRest service = retrofit.create(apiRest.class);
                        Call<ApiResponse> call = service.deletePack(Integer.parseInt(user_id),user_key,Integer.parseInt(StickerPack.get(position).identifier));
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if(response.isSuccessful()) {
                                    if (response.body().getCode() ==  200){
                                        Toasty.success(activity,response.body().getMessage(),Toast.LENGTH_LONG).show();
                                        Intent intent  =  new Intent(activity.getApplicationContext(), UserActivity.class);
                                        intent.putExtra("id", Integer.parseInt(prf.getString("ID_USER")));
                                        intent.putExtra("image",prf.getString("IMAGE_USER").toString());
                                        intent.putExtra("name",prf.getString("NAME_USER").toString());
                                        activity.startActivity(intent);
                                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                                        activity.finish();
                                    }else{
                                        Toasty.error(activity,response.body().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toasty.error(activity,activity.getResources().getString(R.string.error_server),Toast.LENGTH_LONG).show();
                                }
                                if (dialog_progress!=null){
                                    dialog_progress.dismiss();
                                }
                            }
                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toasty.error(activity,activity.getResources().getString(R.string.error_server),Toast.LENGTH_LONG).show();
                                if (dialog_progress!=null){
                                    dialog_progress.dismiss();
                                }
                            }
                        });
                    }
                });
            }
            break;
            case 2: {
                final SlideHolder holder = (SlideHolder) holder_parent;

                slide_adapter = new SlideAdapter(activity, slideList);
                holder.view_pager_slide.setAdapter(slide_adapter);
                holder.view_pager_slide.setOffscreenPageLimit(1);

                holder.view_pager_slide.setClipToPadding(false);
                holder.view_pager_slide.setPageMargin(0);
                holder.view_pager_indicator.setupWithViewPager(holder.view_pager_slide);

                holder.view_pager_slide.setCurrentItem(slideList.size() / 2);
            }
            break;
            case 3: {
                final FollowHolder holder = (FollowHolder) holder_parent;
                this.linearLayoutManager=  new LinearLayoutManager((activity.getApplicationContext()),LinearLayoutManager.HORIZONTAL,false);
                this.followAdapter =new FollowAdapter(userList,activity);
                holder.recycle_view_follow_items.setHasFixedSize(true);
                holder.recycle_view_follow_items.setAdapter(followAdapter);
                holder.recycle_view_follow_items.setLayoutManager(linearLayoutManager);
                followAdapter.notifyDataSetChanged();
                Log.v("WE ARE ONE","FollowHolder");
            }
            break;

        }

    }

    @Override
    public int getItemCount() {
        return StickerPack.size();
    }

    public class PackViewHolder extends RecyclerView.ViewHolder {

        TextView item_pack_name,
                item_pack_publisher,
                item_pack_size,
                item_pack_created,
                item_pack_downloads,
                item_pack_username
        ;
        CircularImageView pack_item_image_view_userimage;
        ImageView imone, imtwo, imthree, imfour,imfive,imsix,pack_try_image,image_view_item_pack_fav,image_view_delete;
        CardView cardView;
        RelativeLayout item_pack_premium;
        RelativeLayout item_pack_review;
        RelativeLayout item_pack_delete;

        ImageView image_view_telegram;
        ImageView image_view_whatsapp;
        ImageView image_view_signal;

        public PackViewHolder(@NonNull View itemView) {
            super(itemView);

            item_pack_size      = itemView.findViewById(R.id.item_pack_size);
            item_pack_publisher = itemView.findViewById(R.id.item_pack_publisher);
            item_pack_name      = itemView.findViewById(R.id.item_pack_name);
            item_pack_created   = itemView.findViewById(R.id.item_pack_created);
            item_pack_downloads = itemView.findViewById(R.id.item_pack_downloads);
            item_pack_username  = itemView.findViewById(R.id.item_pack_username);

            pack_item_image_view_userimage  = itemView.findViewById(R.id.pack_item_image_view_userimage);
            item_pack_premium               = itemView.findViewById(R.id.item_pack_premium);
            item_pack_review               = itemView.findViewById(R.id.item_pack_review);
            item_pack_delete               = itemView.findViewById(R.id.item_pack_delete);
            image_view_item_pack_fav        = itemView.findViewById(R.id.image_view_item_pack_fav);
            image_view_whatsapp        = itemView.findViewById(R.id.image_view_whatsapp);
            image_view_telegram        = itemView.findViewById(R.id.image_view_telegram);
            image_view_signal        = itemView.findViewById(R.id.image_view_signal);

            image_view_delete = itemView.findViewById(R.id.image_view_delete);

            imone = itemView.findViewById(R.id.sticker_one);
            imtwo = itemView.findViewById(R.id.sticker_two);
            imthree = itemView.findViewById(R.id.sticker_three);
            imfour = itemView.findViewById(R.id.sticker_four);
            imfive = itemView.findViewById(R.id.sticker_five);
            imsix = itemView.findViewById(R.id.sticker_six);
            pack_try_image = itemView.findViewById(R.id.pack_try_image);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
    private class SlideHolder extends RecyclerView.ViewHolder {
        private final ViewPagerIndicator view_pager_indicator;
        private final ClickableViewPager view_pager_slide;
        public SlideHolder(View itemView) {
            super(itemView);
            this.view_pager_indicator=(ViewPagerIndicator) itemView.findViewById(R.id.view_pager_indicator);
            this.view_pager_slide=(ClickableViewPager) itemView.findViewById(R.id.view_pager_slide);


        }

    }
    public class CategoriesHolder extends RecyclerView.ViewHolder {
        private final LinearLayoutManager linearLayoutManager;
        private final CategoryAdapter categoryVideoAdapter;
        public RecyclerView recycler_view_item_categories;

        public CategoriesHolder(View view) {
            super(view);
            this.recycler_view_item_categories = (RecyclerView) itemView.findViewById(R.id.recycler_view_item_categories);
            this.linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            this.categoryVideoAdapter = new CategoryAdapter(categoryList, activity);
            recycler_view_item_categories.setHasFixedSize(true);
            recycler_view_item_categories.setAdapter(categoryVideoAdapter);
            recycler_view_item_categories.setLayoutManager(linearLayoutManager);
        }
    }
    public static class FollowHolder extends  RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_follow_items;
        public FollowHolder(View view) {
            super(view);
            recycle_view_follow_items = (RecyclerView) itemView.findViewById(R.id.recycle_view_follow_items);
        }
    }
    public class MoPubbNativeHolder extends RecyclerView.ViewHolder {
        RelativeLayout relative_view_mo_pub;
        public MoPubbNativeHolder(@NonNull View itemView, ViewGroup parent) {
            super(itemView);

            this.relative_view_mo_pub = itemView.findViewById(R.id.relative_view_mo_pub);

            PrefManager prefManager= new PrefManager(activity);


            SdkConfiguration.Builder sdkConfiguration = new SdkConfiguration.Builder(prefManager.getString("ADMIN_NATIVE_ADMOB_ID"));
            MoPub.initializeSdk(activity, sdkConfiguration.build(), new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {

                    ViewBinder viewBinder = new ViewBinder.Builder(R.layout.item_mopub_native)
                            .mainImageId(R.id.native_ad_main_image)
                            .iconImageId(R.id.native_ad_icon_image)
                            .titleId(R.id.native_ad_title)
                            .textId(R.id.native_ad_text)

                            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
                            .sponsoredTextId(R.id.native_sponsored_text_view)
                            .callToActionId(R.id.native_cta)

                            .build();
                    MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(viewBinder);


                    MoPubNative moPubNative = new MoPubNative(activity, prefManager.getString("ADMIN_NATIVE_ADMOB_ID"), new MoPubNative.MoPubNativeNetworkListener() {
                        @Override
                        public void onNativeLoad(NativeAd nativeAd) {


                            relative_view_mo_pub.addView(nativeAd.createAdView(activity,parent));
                            nativeAd.getMoPubAdRenderer().renderAdView(activity. getLayoutInflater().inflate(R.layout.item_mopub_native, (ViewGroup) itemView),nativeAd.getBaseNativeAd());

                        }

                        @Override
                        public void onNativeFail(NativeErrorCode nativeErrorCode) {

                        }
                        // We will be populating this below
                    });


                    moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);

                    moPubNative.makeRequest();
                }
            });

        }
    }
    public class AdmobNativeHolder extends RecyclerView.ViewHolder {
        private final AdLoader adLoader;
        private com.google.android.gms.ads.nativead.NativeAd nativeAd;
        private FrameLayout frameLayout;

        public AdmobNativeHolder(@NonNull View itemView) {
            super(itemView);

            PrefManager prefManager= new PrefManager(activity);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_adplaceholder);
            AdLoader.Builder builder = new AdLoader.Builder(activity, prefManager.getString("ADMIN_NATIVE_ADMOB_ID"));



            builder.forNativeAd(
                    nativeAd -> {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.

                        if (nativeAd == null) {
                            nativeAd.destroy();
                            return;
                        }

                        Bundle extras = nativeAd.getExtras();
                        if (extras.containsKey(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET)) {
                            String socialContext = (String) extras.get(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET);

                        }
                        AdmobNativeHolder.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = activity.findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_unified, null);

                        populateNativeAdView(nativeAd, adView);
                        if(frameLayout != null){
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }

                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().setStartMuted(true).build();

            com.google.android.gms.ads.nativead.NativeAdOptions adOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            adLoader =
                    builder
                            .withAdListener(
                                    new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                                            String error =
                                                    String.format(
                                                            "domain: %s, code: %d, message: %s",
                                                            loadAdError.getDomain(),
                                                            loadAdError.getCode(),
                                                            loadAdError.getMessage());

                                            Log.d("ADMOB_TES", error);

                                        }
                                    })
                            .build();

            adLoader.loadAd(new AdRequest.Builder().build());

        }
    }

    /**
     * Populates a {@link NativeAdView} object with data from a given {@link com.google.android.gms.ads.nativead.NativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView the view to be populated
     */
    private void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((com.google.android.gms.ads.nativead.MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.

                    super.onVideoEnd();
                }
            });
        } else {

        }
    }
    private void requestMoPubInterstitial() {
        if (moPubInterstitial==null){
            PrefManager prefManager= new PrefManager(activity);


            SdkConfiguration.Builder sdkConfiguration = new SdkConfiguration.Builder( prefManager.getString("ADMIN_INTERSTITIAL_ADMOB_ID"));
            MoPub.initializeSdk(activity, sdkConfiguration.build(), new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    moPubInterstitial = new MoPubInterstitial(activity, prefManager.getString("ADMIN_INTERSTITIAL_ADMOB_ID"));

                    moPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                        @Override
                        public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {


                            Log.v("mInterstitial","onInterstitialLoaded2222");
                        }

                        @Override
                        public void onInterstitialFailed(MoPubInterstitial moPubInterstitial_, MoPubErrorCode moPubErrorCode) {
                            Log.v("mInterstitial",moPubErrorCode.toString());
                            moPubInterstitial = null;
                        }

                        @Override
                        public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

                        }

                        @Override
                        public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

                        }

                        @Override
                        public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial_) {
                            selectOperation();
                            moPubInterstitial = null;
                            requestMoPubInterstitial();
                        }
                    });

                    moPubInterstitial.load();
                }
            });


        }


    }
    private void requestAdmobInterstitial() {
        if (admobInterstitialAd==null){
            PrefManager prefManager= new PrefManager(activity);
            AdRequest adRequest = new AdRequest.Builder().build();
            admobInterstitialAd.load(activity.getApplicationContext(), prefManager.getString("ADMIN_INTERSTITIAL_ADMOB_ID"), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    admobInterstitialAd = interstitialAd;


                    admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            selectOperation();

                            Log.d("TAG", "The ad was dismissed.");
                        }


                        @Override
                        public void onAdShowedFullScreenContent() {
                            admobInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    admobInterstitialAd = null;
                    Log.d("TAG_ADS", "onAdFailedToLoad: "+loadAdError.getMessage());

                }
            });

        }


    }
    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(activity);
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }
    public void selectOperation() {
            if(selected_view !=  null && selected_position != -1){
                (activity).startActivity(selected_intent, ActivityOptionsCompat.makeScaleUpAnimation(selected_view, (int) selected_view.getX(), (int) selected_view.getY(), selected_view.getWidth(), selected_view.getHeight()).toBundle());
            }
    }

    public void Operation(){

        PrefManager prefManager= new PrefManager(activity);
        if(checkSUBSCRIBED()) {
            selectOperation();
        }else{
            if(prefManager.getString("ADMIN_INTERSTITIAL_TYPE").equals("ADMOB")) {
                requestAdmobInterstitial();
                if(prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS") <= prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")){
                    if (admobInterstitialAd != null) {
                        prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",0);
                        admobInterstitialAd.show(activity);
                    }else{
                        selectOperation();
                    }
                }else{
                    selectOperation();
                    prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")+1);
                }
            }else  if(prefManager.getString("ADMIN_INTERSTITIAL_TYPE").equals("MOPUB")) {
                requestMoPubInterstitial();
                if(prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS") <= prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")){
                    if (moPubInterstitial != null) {
                        if (moPubInterstitial.isReady()) {
                            prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS", 0);
                            moPubInterstitial.show();

                        } else {
                            selectOperation();
                        }
                    } else {
                        selectOperation();
                    }
                }else{
                    selectOperation();
                    prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")+1);
                }
            }
            else{
                selectOperation();
            }
        }
    }

}

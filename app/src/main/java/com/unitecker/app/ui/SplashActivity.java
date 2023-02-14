package com.unitecker.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



import com.unitecker.app.Manager.PrefManager;
import com.unitecker.app.R;
import com.unitecker.app.config.Config;
import com.unitecker.app.api.apiClient;
import com.unitecker.app.api.apiRest;
import com.unitecker.app.entity.ApiResponse;
import com.unitecker.app.entity.CategoryApi;
import com.unitecker.app.services.BillingSubs;
import com.unitecker.app.services.CallBackBilling;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {


    private PrefManager prf;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        check();
        loadLang();
        setContentView(R.layout.activity_splash);
        prf= new PrefManager(getApplicationContext());

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       checkUser();

                    }
                });
            }
        }, 3000);
        prf.setString("ADMIN_REWARDED_ADMOB_ID","");
        prf.setString("ADMIN_NATIVE_BANNER_FACEBOOK_ID","");

        prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID","");
        prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID","");
        prf.setString("ADMIN_INTERSTITIAL_TYPE","FALSE");
        prf.setInt("ADMIN_INTERSTITIAL_CLICKS",3);

        prf.setString("ADMIN_BANNER_ADMOB_ID","");
        prf.setString("ADMIN_BANNER_FACEBOOK_ID","");
        prf.setString("ADMIN_BANNER_TYPE","FALSE");

        prf.setString("ADMIN_NATIVE_FACEBOOK_ID","");
        prf.setString("ADMIN_NATIVE_ADMOB_ID","");
        prf.setString("ADMIN_NATIVE_LINES","6");
        prf.setString("ADMIN_NATIVE_TYPE","FALSE");
        prf.setString("ADMIN_REWARDED_AD_TYPE","FALSE");
    }

    private void checkUser() {
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            Integer user_id =  Integer.parseInt(prf.getString("ID_USER"));
            String user_key = prf.getString("TOKEN_USER");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.checkUser(user_id,user_key);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body().getCode() == 500){
                            logout();
                        }
                    }
                    checkAccount();
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    checkAccount();
                }
            });
        }else{
            checkAccount();
        }
    }
    public void logout() {
        PrefManager prf = new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.remove("LOGGED");
        Toasty.warning(getApplicationContext(),getString(R.string.message_logout),Toast.LENGTH_LONG).show();

    }
    private void checkAccount() {

        Integer version = -1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.check(version);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {


                    if (response.isSuccessful()){

                        for (int i = 0; i < response.body().getValues().size(); i++) {
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_APP_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_APP_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_PUBLISHER_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_PUBLISHER_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_REWARDED_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_REWARDED_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_REWARDED_AD_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_REWARDED_AD_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_BANNER_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_BANNER_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_CLICKS") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setInt("ADMIN_INTERSTITIAL_CLICKS",Integer.parseInt(response.body().getValues().get(i).getValue()));
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_LINES") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_LINES",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_TYPE",response.body().getValues().get(i).getValue());
                            }
                        }

                        if (response.body().getCode().equals(200)) {
                            if (!prf.getString("first").equals("true")){
                                Intent intent = new Intent(SplashActivity.this,SlideActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                                prf.setString("first","true");
                            }else{
                                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }
                        /*}else if (response.body().getCode().equals(202)) {
                            String title_update=response.body().getValues().get(0).getValue();
                            String featurs_update=response.body().getMessage();
                            View v = (View)  getLayoutInflater().inflate(R.layout.update_message,null);
                            TextView update_text_view_title=(TextView) v.findViewById(R.id.update_text_view_title);
                            TextView update_text_view_updates=(TextView) v.findViewById(R.id.update_text_view_updates);
                            update_text_view_title.setText(title_update);
                            update_text_view_updates.setText(featurs_update);
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setTitle("New Update")
                                    //.setMessage(response.body().getValue())
                                    .setView(v)
                                    .setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String appPackageName=getApplication().getPackageName();
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }
                                            finish();
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.skip), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!prf.getString("first").equals("true")){
                                                Intent intent = new Intent(SplashActivity.this,SlideActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                finish();
                                                prf.setString("first","true");
                                            }else{
                                                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                finish();
                                            }
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(R.drawable.ic_update)
                                    .show();*/
                        } else {
                            if (!prf.getString("first").equals("true")){
                                Intent intent = new Intent(SplashActivity.this,SlideActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                                prf.setString("first","true");
                            }else{
                                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }
                        }
                    }else {
                        if (!prf.getString("first").equals("true")){
                            Intent intent = new Intent(SplashActivity.this,SlideActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                            prf.setString("first","true");
                        }else{
                            Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                    if (!prf.getString("first").equals("true")){
                        Intent intent = new Intent(SplashActivity.this,SlideActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                        prf.setString("first","true");
                    }else{
                        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }

                }
            });


    }


    public void check(){
        List<String> listSkuStoreSubs = new ArrayList<>();
        listSkuStoreSubs.add(Config.SUBSCRIPTION_ID);
        new BillingSubs(this, listSkuStoreSubs).checkPurchase( listSkuStoreSubs, new CallBackBilling() {
            @Override
            public void onPurchase() {
                PrefManager prefManager= new PrefManager(getApplicationContext());
                prefManager.setString("SUBSCRIBED","TRUE");
            }

            @Override
            public void onNotPurchase() {
                PrefManager prefManager= new PrefManager(getApplicationContext());
                prefManager.setString("SUBSCRIBED","FALSE");
            }

            @Override
            public void onNotLogin() {
                PrefManager prefManager= new PrefManager(getApplicationContext());
                prefManager.setString("SUBSCRIBED","FALSE");
            }
        });
    }
    public void loadLang(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<CategoryApi>> call = service.AllCategories();
        call.enqueue(new Callback<List<CategoryApi>>() {
            @Override
            public void onResponse(Call<List<CategoryApi>> call, Response<List<CategoryApi>> response) {

            }

            @Override
            public void onFailure(Call<List<CategoryApi>> call, Throwable t) {

            }
        });

    }


}

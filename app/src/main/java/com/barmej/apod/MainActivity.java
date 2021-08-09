package com.barmej.apod;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.barmej.apod.entity.ResponseInfo;
import com.barmej.apod.fragments.DatePickerFragment;
import com.barmej.apod.network.NetworkUtils;
import com.barmej.apod.utils.ResponseDataParser;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String IMAGE = "image";
    private static final String REQUEST_TAG = "RequestTag";
    LinearLayout bottomSheetLayout;
    TextView photoDescription;
    TextView photo_title;
    TouchImageView touchImageView;
    WebView webView;
    ProgressBar progressBar;

    RequestQueue mRequestQueue;

    ResponseInfo responseInfo;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoDescription = findViewById(R.id.photo_description);
        photo_title = findViewById(R.id.photo_title);
        bottomSheetLayout= findViewById(R.id.bottom_sheet);
        touchImageView = findViewById(R.id.img_picture_view);
        webView = findViewById(R.id.wv_video_player);
        progressBar = findViewById(R.id.progressBar);


        mRequestQueue = Volley.newRequestQueue(this);

        // الحصول على الصوره الحاليه
        getCurrentAPOD();
    }


    // الحصول على الصوره الحاليه
    private void getCurrentAPOD() {
        String url = NetworkUtils.getCurrentAPOD_Url(MainActivity.this).toString();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    responseInfo = ResponseDataParser.getResponseInfoFromJson(response);
                    photoDescription.setText(responseInfo.getExplanation());
                    photo_title.setText(responseInfo.getTitle());
                    setMedia(responseInfo);
                    // هنا يتم اخفاء او اضهار العنصر اعتمادا على المحتوى
                    mMenu.findItem(R.id.action_download_hd).setVisible(responseInfo.getMediaType().equals(IMAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);
    }

    // الحصول على صوره في تاريخ معين
    private void getSpecificAPOD(String date) {
        String url = NetworkUtils.getSpecificAPOD_Url(MainActivity.this, date).toString();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("TTT","Response: "+response);

                    responseInfo = ResponseDataParser.getResponseInfoFromJson(response);
                    photoDescription.setText(responseInfo.getExplanation());
                    photo_title.setText(responseInfo.getTitle());
                    setMedia(responseInfo);
                    // هنا يتم اخفاء او اضهار العنصر اعتمادا على المحتوى
                    mMenu.findItem(R.id.action_download_hd).setVisible(responseInfo.getMediaType().equals(IMAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);
    }

    // معالجة نتائج التاريخ التي تاتينا من ال DatePickerFragment
    public void processDatePickerResult(int year, int month, int day) {
        String year_string = Integer.toString(year);
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String dateMessage = (year_string + "-" + month_string + "-" + day_string);
        getSpecificAPOD(dateMessage);
    }

    // تعيين الوسائط
    private void setMedia(ResponseInfo responseInfo) {
        if (responseInfo.getMediaType().equals(IMAGE)) {
            touchImageView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            // set photo
            Glide.with(this).load(responseInfo.getUrl()).into(touchImageView);
        } else {
            webView.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.VISIBLE);
            touchImageView.setVisibility(View.GONE);
            // set video
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.setWebChromeClient(new WebChromeClient());
            webView.loadUrl(responseInfo.getUrl());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_pick_day) {
            // يتم استدعاء منتقي التاريخ
            DialogFragment dialogFragment = new DatePickerFragment();
            dialogFragment.show(getSupportFragmentManager(), getString(R.string.date_picker));
        } else if (id == R.id.action_share) {
            // share image
            shareImage();
        } else if (id == R.id.action_about) {
            // about
            aboutTheApp();
        } else if (id == R.id.action_download_hd) {
            // download image
            downloadImage();
        }
        return super.onOptionsItemSelected(item);
    }

    // تنزيل الصورة
    private void downloadImage() {
        Uri uri = Uri.parse(responseInfo.getHdurl());

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        request.setTitle("Data Download");
        request.setDescription("Downloading image..");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, " AstronomyPictureOfTheDay");
        request.setMimeType("image/*");
        downloadManager.enqueue(request);
    }

    // مشاركة الوصائط
    private void shareImage() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (responseInfo.getMediaType().equals(IMAGE)) {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, responseInfo.getHdurl());
            startActivity(Intent.createChooser(intent, "Share image via: "));

            // Share video url
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, responseInfo.getUrl());
            intent.setType("text/plain");
            startActivity(intent);
        }
    }

    // عن التطبيق
    private void aboutTheApp() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.fragment_about, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        // show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

   // فحص اتجاه الشاشة
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUI();
            bottomSheetLayout.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showSystemUI();
            bottomSheetLayout.setVisibility(View.VISIBLE);
        }
    }

    // اخفاء اشرطة النضام
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // إخفاء شريط التنقل وشريط الحالة
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // اضهار أشرطة النظام عن طريق إزالة جميع العلامات
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(REQUEST_TAG);
        }
    }
}

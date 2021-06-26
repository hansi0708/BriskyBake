package com.hv.briskybake;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PayUMoneyActivity extends AppCompatActivity {

    WebView webView;
    Context activity;


    /**
     *
     *   Intent intent = new Intent(DeliveryOptionActivity.this, PayUMoneyActivity.class);
     *         Bundle bundle = new Bundle();
     *         bundle.putString("name", preferences.getLoginUserName());
     *         bundle.putString("email", preferences.getLoginEmail());
     *         bundle.putString("productInfo", "Payment for "+ getString(R.string.app_name));
     *         bundle.putString("phone", preferences.getLoginMobile());
     *         bundle.putString("amount", amount);
     * //        bundle.putString("amount", "1");
     *
     *         intent.putExtras(bundle);
     *         startActivityForResult(intent, 1234);\
     *
     * Order Id
     * To Request for Updating Payment Status if Payment Successfully Done
     */
    int mId;
    private String mMerchantKey = "0OYLyc";//For merchant and salt key you need to contact payu money tech support otherwise you get error
    private String mSalt = "8h6JU9Ac";//copy and paste works fine
//    private String mBaseURL = "https://secure.payu.in/";
    private String mBaseURL = "https://secure.payu.in/";
    private String mAction = ""; // For Final URL
    private String mTXNId; // This will create below randomly
    private String mHash; // This will create below randomly
    private String mProductInfo; // From Previous Activity
    private String mFirstName; // From Previous Activity
    private String mEmailId; // From Previous Activity
    private double mAmount; // From Previous Activity
    private String mPhone; // From Previous Activity
    private String mServiceProvider = "payu_paisa";



    boolean isFromOrder;
    /**
     * Handler os handler
     */
    Handler mHandler = new Handler();
    private String TAG = "User info";
    private ProgressDialog progressDialog;


    /**
     * @param savedInstanceState
     */
    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        /**
         * Setting WebView to Screen
         */
        setContentView(R.layout.activity_pay_umoney);
        /**
         * start processing Payu dialog
         */
        progressBarVisibilityPayuChrome(View.VISIBLE);

        /**
         * Creating WebView
         */
        webView = (WebView) findViewById(R.id.payumoney_webview);

        /**
         * Context Variable
         */
        activity = this;
        /**
         * Getting Intent Variables...
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mFirstName = bundle.getString("name");
            mEmailId = bundle.getString("email");
            mProductInfo = bundle.getString("productInfo");
            mAmount = Double.parseDouble(bundle.getString("amount", "0"));//in my case amount getting as String so i parse it double
            mPhone = bundle.getString("phone");
            mId = bundle.getInt("id");
            isFromOrder = bundle.getBoolean("isFromOrder", false);

            Log.i(TAG, "" + mFirstName + " : " + mEmailId + " : " + mAmount + " : " + mPhone);

            /**
             * Creating Transaction Id
             */
            Random rand = new Random();
            String randomString = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
            mTXNId = hashCal("SHA-256", randomString).substring(0, 20);

            mAmount = new BigDecimal(mAmount).setScale(0, RoundingMode.UP).intValue();

            /**
             * Creating Hash Key
             */
            mHash = hashCal("SHA-512", mMerchantKey + "|" +
                    mTXNId + "|" +
                    mAmount + "|" +
                    mProductInfo + "|" +
                    mFirstName + "|" +
                    mEmailId + "|||||||||||" +
                    mSalt);

            /**
             * Final Action URL...
             */
            mAction = mBaseURL.concat("/_payment");
//            mAction = mBaseURL;

            /**
             * WebView Client
             */
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Toast.makeText(activity, "Oh no! " + error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    if (url.equals("mSuccessUrl")) {
                        Intent intent = new Intent();
                        intent.putExtra("MESSAGE","success");
                        intent.putExtra("oid", mTXNId);
                        setResult(1234,intent);
                        finish();
                    } else if (url.equals("mFailedUrl")) {
                        Intent intent = new Intent();
                        intent.putExtra("MESSAGE","failed");
                        intent.putExtra("oid", mTXNId);
                        setResult(1234,intent);
                        finish();
                    }

                    /**
                     * wait 10 seconds to dismiss payu money processing dialog in my case
                     */
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBarVisibilityPayuChrome(View.GONE);
                        }
                    }, 10000);
                    super.onPageFinished(view, url);
                }
            });

            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setDomStorageEnabled(true);
            webView.clearHistory();
            webView.clearCache(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setUseWideViewPort(false);
            webView.getSettings().setLoadWithOverviewMode(false);
            webView.addJavascriptInterface(new PayUJavaScriptInterface(PayUMoneyActivity.this), "PayUMoney");

            /**
             * Mapping Compulsory Key Value Pairs
             */
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("key", mMerchantKey);
            mapParams.put("txnid", mTXNId);
            mapParams.put("amount", String.valueOf(mAmount));
            mapParams.put("productinfo", mProductInfo);
            mapParams.put("firstname", mFirstName);
            mapParams.put("email", mEmailId);
            mapParams.put("phone", mPhone);
            mapParams.put("surl", "mSuccessUrl");
            mapParams.put("furl", "mFailedUrl");
            mapParams.put("hash", mHash);
            mapParams.put("service_provider", mServiceProvider);

            webViewClientPost(webView, mAction, mapParams.entrySet());

        } else {
            Toast.makeText(activity, "Something went wrong, Try again.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Posting Data on PayUMoney Site with Form
     *
     * @param webView
     * @param url
     * @param postData
     */
    public void webViewClientPost(WebView webView, String url, Collection<Map.Entry<String, String>> postData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");
        Log.d("TAG", "webViewClientPost called: " + sb.toString());
        webView.loadData(sb.toString(), "text/html", "utf-8");
    }


    /**
     * Hash Key Calculation
     *
     * @param type
     * @param str
     * @return
     */
    public String hashCal(String type, String str) {
        byte[] hashSequence = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashSequence);
            byte messageDigest[] = algorithm.digest();

            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append("0");
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException NSAE) {
        }
        return hexString.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onPressingBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onPressingBack();
    }

    /**
     * On Pressing Back
     * Giving Alert...
     */
    private void onPressingBack() {
        Intent intent=new Intent();
        intent.putExtra("MESSAGE","Payment Declined");
        setResult(1234,intent);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PayUMoneyActivity.this);
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Do you cancel this transaction?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public class PayUJavaScriptInterface {
        Context mContext;

        PayUJavaScriptInterface(Context c) {
            mContext = c;
        }

        public void success(long id, final String paymentId) {

            mHandler.post(new Runnable() {

                public void run() {

                    mHandler = null;
                    Toast.makeText(PayUMoneyActivity.this, "Payment Successfully.", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent();
                    intent.putExtra("MESSAGE","success");
                    intent.putExtra("oid", paymentId);
                    setResult(1234,intent);
                    finish();

                }

            });

        }
    }


    public void progressBarVisibilityPayuChrome(int visibility) {
        if (getApplicationContext() != null && !isFinishing()) {
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            } else if (progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = showProgress(this);
            }
        }
    }

    public ProgressDialog showProgress(Context context) {
        if (getApplicationContext() != null && !isFinishing()) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            final Drawable[] drawables = {getResources().getDrawable(R.drawable.l_icon1),
                    getResources().getDrawable(R.drawable.l_icon2),
                    getResources().getDrawable(R.drawable.l_icon3),
                    getResources().getDrawable(R.drawable.l_icon4)
            };

            View layout = mInflater.inflate(R.layout.prog_dialog, null);
            final ImageView imageView;
            imageView = (ImageView) layout.findViewById(R.id.imageView);
            ProgressDialog progDialog = new ProgressDialog(context);

            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                int i = -1;

                @Override
                synchronized public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            i++;
                            if (i >= drawables.length) {
                                i = 0;
                            }
                            imageView.setImageBitmap(null);
                            imageView.destroyDrawingCache();
                            imageView.refreshDrawableState();
                            imageView.setImageDrawable(drawables[i]);
                        }
                    });

                }
            }, 0, 500);

            progDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    timer.cancel();
                }
            });
            progDialog.show();
            progDialog.setContentView(layout);
            progDialog.setCancelable(true);
            progDialog.setCanceledOnTouchOutside(false);
            return progDialog;
        }
        return null;
    }
}
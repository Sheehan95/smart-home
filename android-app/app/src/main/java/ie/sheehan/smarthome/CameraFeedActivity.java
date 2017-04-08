package ie.sheehan.smarthome;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CameraFeedActivity extends AppCompatActivity {

    // ============================================================================================
    // DEFINING STATIC VARIABLES
    // ============================================================================================
    private final static String ENDPOINT = "http://192.167.1.27:8081/";
    private final static String ENCODING = "UTF-8";
    private final static String ACCEPT = "text/html";



    // ============================================================================================
    // DEFINING CLASS VARIABLES
    // ============================================================================================
    private ProgressBar progressBar;



    // ============================================================================================
    // ACTIVITY LIFECYCLE METHODS
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_feed);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        WebView webView = (WebView) findViewById(R.id.cam_feed);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClientProgressBar());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.loadData(getHTML(ENDPOINT), ACCEPT, ENCODING);
    }



    // ============================================================================================
    // DECLARING PRIVATE METHODS
    // ============================================================================================
    /**
     * Returns the HTML used to display the image retrieved by the web view.
     *
     * @param url of an image to display in the web view
     * @return HTML containing that image
     */
    private String getHTML(String url) {
        String html = "";

        html += "<html style=\"background-color:#000000;\">";
        html += "<center>";
        html += "<img src='" + url + "'/>";
        html += "</center>";
        html += "</html>";

        return html;
    }



    // ============================================================================================
    // DECLARING LISTENER METHODS
    // ============================================================================================
    public void exitCameraFeed(View view) {
        onBackPressed();
    }



    // ============================================================================================
    // PRIVATE INNER CLASSES
    // ============================================================================================
    private class WebViewClientProgressBar extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Toast.makeText(CameraFeedActivity.this, R.string.toast_unable_to_open_camera, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

}

/* Students-App Android
 * made by Francois Weber MMT09 || www.students-app.de
 * 
 * HtmlView
 * Creates a webview, used for static (synchronous) sites 
 * Param: htmlSite -> R.resource, means the /raw/your-site.html
 */

package com.mmt.sap;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;


public class HtmlView extends Activity {
	
	private int htmlSite;
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        
        Long dhtmlSite = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("htmlSite");
		if (dhtmlSite == null) {
			Bundle extras = getIntent().getExtras();
			htmlSite = extras != null ? extras.getInt("htmlSite") : null;
		}
        
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        
        initWebKit(webView, this);
        //webView.bringToFront();        
    }
    
    // ---------------------------------------------------
    private void initWebKit(WebView view, Context context) {
    	final String mimeType = "text/html";
    	final String encoding = "UTF-8";
    	String htmldata;

    	InputStream is = context.getResources().openRawResource(htmlSite);
    	
    	try {
    		if(is != null && is.available() > 0) {
    			final byte[] bytes = new byte[is.available()];
    			is.read(bytes);
    			htmldata = new String(bytes);
    			view.loadDataWithBaseURL("file:///android_res/raw/", htmldata, mimeType, encoding, null);
    		}
    	} catch (IOException e) {}
    }
}

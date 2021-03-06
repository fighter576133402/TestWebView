package cn.com.uangel.test;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity{
	private WebView web;
	private ActionBar actionBar;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initWebView();
		initActionBar();
		setMenuList();
		mContext = this;
	}

	/**
	 * 初始化actionbar
	 */
	private void initActionBar(){
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setBackgroundDrawable() 设置actionbar背景
		// actionBar.setTitle(arg0);设置actionbar标题
	}
	/**
	 *  初始化webview
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" }) 
	private void initWebView(){
		web = (WebView) findViewById(R.id.web_content);
		/**如果访问的页面中有Javascript，则webview必须设置支持Javascript。*/
		web.getSettings().setJavaScriptEnabled(true);
		/**	页面中链接，如果希望点击链接继续在当前browser中响应，而不是新开Android的系统browser中响应该链接，必须覆盖
		 *	webview的setWebViewClient
		 */
		web.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		// webview只是一个承载体，各种内容的渲染需要使用webviewChromClient去实现
		web.setWebChromeClient(new MyWebChromeClient());
		// 注册接口，让javascript能够调用java代码
		 web.addJavascriptInterface(new JsObjectFunction(), "injectedObject"); 
		// 从assets目录下面的加载html
		web.loadUrl("file:///android_asset/wst.html");
		// web.loadUrl("http://www.baidu.com");
	}
	/**
	 * 设置menu能够下拉显示
	 */
	public void setMenuList(){
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			finish();
			return true;
		}else if(id == R.id.action_java_call_js){
			// 无参数调用
			web.loadUrl("javascript:javacalljs()");
			return true;
		}else if(id == R.id.action_java_call_js_with){
			// 传递参数调用
			web.loadUrl("javascript:javacalljswithargs(" + "'hello world'" + ")");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// 如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身，如果希望浏览的网
	// 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件。
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	class JsObjectFunction{
		@JavascriptInterface
		public void startFunction() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(mContext, "没有参数", Toast.LENGTH_SHORT).show();
				}
			});
		}
		@JavascriptInterface
		public void startFunction(final String str) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
				}
			});
		}
		@JavascriptInterface
		public void callIntent(){
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, JsCallActivity.class);
			startActivity(intent);
		}
	}
	
}

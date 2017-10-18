package factory.com.android_html5;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static factory.com.android_html5.R.id.button;

//演示基本操作
public class MainActivity extends AppCompatActivity {
    private Button btn;
    private WebView mWebView;
    private String mCLKPackage="com.aee.groundstation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //我是更新过后的文件
    }

    private void init() {
        btn = (Button) findViewById(button);
        mWebView = (WebView) findViewById(R.id.webView);
        // 得到设置属性的对象
        WebSettings webSettings = mWebView.getSettings();
        // 使能JavaScript
        webSettings.setJavaScriptEnabled(true);
        // 支持中文，否则页面中中文显示乱码
        webSettings.setDefaultTextEncodingName("UTF-8");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadData();
                //change();
                //method3();
                //实现自定义Toast显示时间
//                final Toast toast = Toast.makeText(MainActivity.this, "自定义Toast的时间", Toast.LENGTH_LONG);
//                toast.show();
//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        toast.cancel();
//                    }
//                }, 1000);// 1000表示Toast显示时间为1秒
                showCLKActivity();
            }
        });
    }

    //在Android中加载并显示html
    private void loadData() {
        String data = "<html>"
                + "<head>"
                + "<title>欢迎您</title>"
                + "</head>"
                + "<body>"
                + "<h1>我是一段html代码</h1>"
                + "<h2>我是一段html代码</h2>"
                + "<h3>我是一段html代码</h3>"
                + "<h4>我是一段html代码</h4>"
                + "</body>"
                + "</html>";
        //setWebViewClient()方法设置了打开新连接不会跳转到外部浏览器
        mWebView.setWebViewClient(new WebViewClient());
        //使用简单的loadData()方法总会导致乱码，有可能是Android API的Bug
        //webView.loadData(data, "text/html", "GBK");
        mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    //app操作HTML5页面
    private void change() {
        mWebView.loadUrl("file:///android_asset/test.html");
        String color = "#ff0000";
        mWebView.loadUrl("javascript:changeColor('" + color + "');");
    }

    //HTML5操作app
    private void method3() {
        //加载要显示的HTML文本
        mWebView.loadUrl("file:///android_asset/test2.html");
        //使得JS可以调用Android
        mWebView.addJavascriptInterface(new JavaScriptinterface(this),
                "android");
        mWebView.setWebViewClient(new WebViewClient());
        String ss = "123456";
        Log.d("FHZ", ss.substring(0, 3));
    }

    class JavaScriptinterface {
        Context context;

        public JavaScriptinterface(Context c) {
            context = c;
        }

        /**
         * 与js交互时用到的方法，在js里直接调用的
         */
        @JavascriptInterface
        public void showToast(String ssss) {
            Toast.makeText(context, ssss, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void showTime() {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(date);
            Toast.makeText(context, time, Toast.LENGTH_SHORT).show();
        }
    }

    //序列化对象保存到本地
    private static void saveObject(String path, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        File f = new File(path);
        try {
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //从本地文件读取序列化对象
    private static Object readObject(String path) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object object = null;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
            return object;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    //定义比较器，可以用于排序
    Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1 > o2) {
                return 1;
            } else if (o1 < o2) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    class SafeProgressDialog extends ProgressDialog {
        private Activity mContext;

        public SafeProgressDialog(Context context) {
            super(context);
            mContext = (Activity) context;
        }

        @Override
        public void dismiss() {
            if (mContext != null && !mContext.isFinishing()) {
                super.dismiss();
            }

        }
    }

    private void test1() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //将要在主界面显示的逻辑
            }
        });
    }

    //一个线程操作可以在主线程运行的逻辑
    //必须在逻辑前面加上Looper.prepare()，后面加上Looper.loop()
    private void test2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .show();
                Looper.loop();
            }
        }).start();
    }

    class Toast1 extends Toast {
        public Toast1(Context context) {
            super(context);
        }

        @Override
        public void setDuration(int duration) {
            super.setDuration(duration);
        }

        public void setTime(int duration) {
            setDuration(duration);
            show();
        }
    }

    private void showCLKActivity(){
        try{
            if(isAppInstalled(mCLKPackage)){
                Intent firstRunIntent=getPackageManager().getLaunchIntentForPackage(mCLKPackage);
                if(firstRunIntent!=null){
                    startActivity(firstRunIntent);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("FHZ","出错");
        }
    }
    private boolean isAppInstalled(String pkgName) throws PackageManager.NameNotFoundException{
        PackageInfo info=getPackageManager().getPackageInfo(pkgName,PackageManager.GET_ACTIVITIES);
        return info!=null;
    }
}

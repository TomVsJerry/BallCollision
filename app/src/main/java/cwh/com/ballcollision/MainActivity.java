package cwh.com.ballcollision;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.het.basic.AppDelegate;
import com.het.basic.AppNetDelegate;
import com.het.basic.base.RxBus;
import com.het.basic.data.http.okhttp.interceptor.HeTInterceptor;
import com.het.basic.utils.SharePreferencesUtil;
import com.het.basic.utils.SystemInfoUtils;
import com.het.communitybase.CommunityProxy;
import com.het.hetloginbizsdk.api.login.LoginApi;
import com.het.hetloginuisdk.ui.activity.HetLoginActivity;
import com.het.http.HetHttp;
import com.het.http.cache.converter.GsonDiskConverter;
import com.het.http.model.HttpHeaders;
import com.het.http.model.HttpParams;

import java.io.InputStream;

import cwh.com.ballcollision.widget.PoolBallView;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        RxBus.getInstance().register("login_success", new Action1<Object>() {
            @Override
            public void call(Object o) {
                synchronized (MainActivity.this) {
                    Log.d("cwh","login_success");
                    PoolBallView poolBallView = (PoolBallView)findViewById(R.id.pool_ball);
                    BallCollisionManager manager = BallCollisionManager.getInstance(getApplicationContext(),poolBallView,1);
                }
            }
        });

        if(LoginApi.isLogin()){
            PoolBallView poolBallView = (PoolBallView)findViewById(R.id.pool_ball);
            BallCollisionManager manager = BallCollisionManager.getInstance(getApplicationContext(),poolBallView,1);
        }else{
            HetLoginActivity.startHetLoginActy(this, HetLoginActivity.LaunchMode.NORMAL, null, 0);
        }
    }

    private void init() {
        AppNetDelegate.initOkHttps(getApplication());
        AppDelegate.setAppId("10101");
        AppDelegate.setAppSecret("afd55f877bad4aaeab45fb4ca567d234");
        AppDelegate.setHost("https://dp.clife.net");
        AppDelegate.init(getApplication());
        initHttp();
    }

    /**
     * 初始化网络库请求
     */
    private void initHttp() {
        long timeout = 30000L;
        HetHttp.init(getApplication());
        //全局设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.put("User-Agent", SystemInfoUtils.getUserAgent(this.getApplicationContext(), "10101"));
        headers.put("cookie", SystemInfoUtils.getClifeStrategy());

        //全局设置请求参数
        HttpParams params = new HttpParams();
        params.put("appId", "10101");

        HetHttp.getInstance()
                .setBaseUrl("https://dp.clife.net")
                .debug("HetHttp", BuildConfig.DEBUG)
                .setCertificates(new InputStream[0])
                .setConnectTimeout(timeout)
                .setReadTimeOut(timeout)
                .setWriteTimeOut(timeout)
                .addCommonHeaders(headers)
                .addCommonParams(params)
                .setCacheDiskConverter(new GsonDiskConverter())
                .setCacheVersion(2)
                .addInterceptor(new HeTInterceptor())
                .addInterceptor(new SignInterceptor());
    }
}

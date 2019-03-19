package cwh.com.ballcollision;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.het.communitybase.CommunityProxy;
import com.het.communitybase.IFeedCallback;
import com.het.communitybase.bean.LabelBean;

import java.util.List;

import cwh.com.ballcollision.widget.PoolBallView;


public class BallCollisionManager {

    private static BallCollisionManager mBallCollisionManager;
    private Sensor mDefaultSensor;
    private SensorManager mSensorManager;
    private PoolBallView mPool;
    private Context mContext;
    private int lastX;
    private int lastY;
    private LoadDataListener mLoadDataListener;
    private int mProductType;//产品类型 肌肤秘诀 1，海豚睡眠 2

    private BallCollisionManager(Context context, PoolBallView pool, int productType) {
        this.mContext = context;
        this.mPool = pool;
        this.mProductType = productType;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mDefaultSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        initData();
    }

    public static BallCollisionManager getInstance(Context context, PoolBallView pool, int productType) {
        if (mBallCollisionManager == null) {
            mBallCollisionManager = new BallCollisionManager(context, pool, productType);
        }
        return mBallCollisionManager;
    }

    public void initData() {
        CommunityProxy.getInstance().queryLabels(new IFeedCallback<List<LabelBean>>() {
            @Override
            public void onLocalSuccess(List<LabelBean> labelBeans) {

            }

            @Override
            public void onNetworkSuccess(List<LabelBean> labelBeans) {
                if (mLoadDataListener != null) {
                    mLoadDataListener.onLoadSuccess();
                }
                mPool.initBubbles(labelBeans, mProductType);
            }

            @Override
            public void onFail(int i, String s) {
                if (mLoadDataListener != null) {
                    mLoadDataListener.onLoadFailded();
                }
            }
        });
        //请求网络数据
    }

    private SensorEventListener listerner = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                int x = (int) event.values[0];
                int y = (int) (event.values[1] * 2.0f);
                if (lastX != x || lastY != y) {//防止频繁回调,画面抖动
                    mPool.getBallView().rockBallByImpulse(-x, y);
                }
                Log.e("陀螺仪 ", x + "<----陀螺仪Y: " + y + "<-----");
                lastX = x;
                lastY = y;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void registerSensor() {
        mSensorManager.registerListener(listerner, mDefaultSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterSensor() {
        mSensorManager.unregisterListener(listerner);
    }

    public void setLoadDataListener(LoadDataListener listener) {
        this.mLoadDataListener = listener;
    }

    public void setOnLabelStatusChangedListener(PoolBallView.OnLabelStatusChangedListener listener) {
        mPool.setOnLabelStatusChangedListener(listener);
    }

    public interface LoadDataListener {
        void onLoadFailded();

        void onLoadSuccess();
    }

}

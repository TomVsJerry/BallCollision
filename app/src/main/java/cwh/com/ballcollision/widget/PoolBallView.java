package cwh.com.ballcollision.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.het.communitybase.bean.LabelBean;
import com.het.communitybase.utils.DensityUtil;

import java.util.List;

import cwh.com.ballcollision.R;

/**
 * Created by Administrator on 2018-05-17.
 */

public class PoolBallView extends FrameLayout {

    private BallView ballView;
    private Context mContext;
    private OnLabelStatusChangedListener mOnLabelStatusChangedListener;
    private static final int MAX_CHECKED_COUNT = 3;

    public PoolBallView(Context context) {
        this(context, null);
    }

    public PoolBallView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PoolBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写ondraw需要
        ballView = new BallView(context, this);
        this.mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        ballView.onLayout(changed);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ballView.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ballView.onSizeChanged(w, h);
    }

    public BallView getBallView() {
        return this.ballView;
    }


    public void initBubbles(List<LabelBean> labelBeans, int mProductType) {
        for (int i = 0; i < labelBeans.size(); i++) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            BubbleBallView ballView = new BubbleBallView(mContext, labelBeans.get(i), mProductType);
            ballView.setTag(R.id.circle_tag, true);
            addView(ballView, layoutParams);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int checkedCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            BubbleBallView child = (BubbleBallView) getChildAt(i);
            if (child.isChecked()) {
                checkedCount++;
                if (checkedCount >= MAX_CHECKED_COUNT) {
                    if (mOnLabelStatusChangedListener != null) {
                        mOnLabelStatusChangedListener.onOverThreeLabelChecked();
                    }
                    return true;
                }
            }
        }
        checkedCount = 0;
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            for (int i = 0; i < getChildCount(); i++) {
                BubbleBallView child = (BubbleBallView) getChildAt(i);
                if (child.isChecked()) {
                    checkedCount++;
                }
                if (mOnLabelStatusChangedListener != null) {
                    if (checkedCount == 0) {
                        mOnLabelStatusChangedListener.onOneLabelChecked(false);
                    } else if (checkedCount == 1) {
                        mOnLabelStatusChangedListener.onOneLabelChecked(true);
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setOnLabelStatusChangedListener(OnLabelStatusChangedListener listener) {
        this.mOnLabelStatusChangedListener = listener;
    }

    public interface OnLabelStatusChangedListener {
        void onOneLabelChecked(boolean tag);
        void onOverThreeLabelChecked();
    }
}

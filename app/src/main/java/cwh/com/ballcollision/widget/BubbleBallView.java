package cwh.com.ballcollision.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;

import com.het.communitybase.bean.LabelBean;
import com.het.communitybase.utils.DensityUtil;
import com.het.communitybase.view.BubbleView;

import java.util.ArrayList;


public class BubbleBallView extends AppCompatTextView {

    private LabelBean mLabelBean;
    private boolean isChecked;
    private Context mContext;
    public static final int SIZE = 88;
    private BubbleView.OnTagItemCheckedChangedListener mOnTagItemCheckedChangedListener;
    private GradientDrawable mFillDrawable = new GradientDrawable();
    private GradientDrawable mNormalDrawable = new GradientDrawable();
    private int mTextSlectedColor = Color.WHITE;
    private int mProductType;

    private int mStrokeColor = Color.TRANSPARENT;//Color.parseColor("#593be1ff");
    private int mTextUnslectedColor = Color.parseColor("#333333");
    private static final int normalStartColor = Color.parseColor("#FFF8F8");
    private static final int normalEndColor = Color.parseColor("#FFE7EE");
    private static final int fillStartColor = Color.parseColor("#FFA3BB");
    private static final int fillEndColor = Color.parseColor("#FF4275");

    public BubbleBallView(Context context, LabelBean bean, int productType) {
        super(context);
        this.mContext = context;
        this.mProductType = productType;
        this.mLabelBean = bean;
        setText(bean.getLabelName());
        if (productType == 1) {
            initView();
        } else {
            initDolphinView();
        }
    }

    private void initDolphinView() {
        setDrawableParam(mNormalDrawable, Color.TRANSPARENT, Color.TRANSPARENT);
        setDrawableParam(mFillDrawable, Color.parseColor("#FF3BE1FF"), Color.parseColor("#FF3BE1FF"));
        mStrokeColor = Color.WHITE;
        mTextUnslectedColor = Color.WHITE;
        setTextColor(mTextUnslectedColor);
        setGravity(Gravity.CENTER);
        setBackground(mNormalDrawable);
        setOnClickListener(mOnClickedListener);
    }

    private void initView() {
        setDrawableParam(mNormalDrawable, normalStartColor, normalEndColor);
        setDrawableParam(mFillDrawable, fillStartColor, fillEndColor);
        setTextColor(mTextUnslectedColor);
        setGravity(Gravity.CENTER);
        setBackground(mNormalDrawable);
        setOnClickListener(mOnClickedListener);
    }


    public void setDrawableParam(GradientDrawable drawable, int startColor, int endColor) {
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        drawable.setSize(DensityUtil.dip2px(mContext, SIZE), DensityUtil.dip2px(mContext, SIZE));
        drawable.setColors(new int[]{startColor, endColor});
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(DensityUtil.dip2px(mContext, 2), mStrokeColor);
    }

    public OnClickListener mOnClickedListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isChecked) {
                isChecked = false;
                setTextColor(mTextUnslectedColor);
                if (mOnTagItemCheckedChangedListener != null) {
                    mOnTagItemCheckedChangedListener.onTagItemCheckedChanged(view, isChecked);
                }
                setBackground(mNormalDrawable);
            } else {
                isChecked = true;
                setTextColor(mTextSlectedColor);
                if (mOnTagItemCheckedChangedListener != null) {
                    mOnTagItemCheckedChangedListener.onTagItemCheckedChanged(view, isChecked);
                }
                setBackground(mFillDrawable);
            }
            invalidate();
        }
    };

    private View getTouchTarget(int x, int y) {
        View targetView = null;
        // 判断view是否可以聚焦
        ArrayList<View> TouchableViews = getTouchables();
        for (View child : TouchableViews) {
            if (isTouchPointInView(child, x, y)) {
                targetView = child;
                break;
            }
        }
        return targetView;
    }

    //(x,y)是否在view的区域内
    private boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        //view.isClickable() &&
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    public interface OnTagItemCheckedChangedListener {
        void onTagItemCheckedChanged(View view, boolean check);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public LabelBean getLabelBean() {
        return this.mLabelBean;
    }
}

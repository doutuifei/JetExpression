package com.muzi.jetexpression;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

/**
 * 作者: lipeng
 * 时间: 2018/6/26
 * 邮箱: lipeng@moyi365.com
 * 功能:
 */
public class LikeView extends LinearLayout implements View.OnClickListener {

    private int[] iconInts = new int[]{R.drawable.af0,
            R.drawable.af1,
            R.drawable.af2,
            R.drawable.af3,
            R.drawable.af4,
            R.drawable.af5,
            R.drawable.af6,
            R.drawable.af7,
            R.drawable.af8,
            R.drawable.af9};

    private View ivLike;

    private TextView txNumber;

    private boolean isLike = false;//是否点赞

    private int likeNumber = 0;//点赞数

    private int clickNum = 0;//点击次数
    private Long jetDuration = 800L;
    private boolean animaterRunning = false;//动画是否正在进行

    /**
     * 图片大小改变动画
     */
    private AnimatorSet ivSizeSet;
    private Long sizeDuration = 200L;


    public LikeView(Context context) {
        super(context);
        init(context, null);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LikeView);
        int index = typedArray.getInt(R.styleable.LikeView_android_orientation, LinearLayout.HORIZONTAL);
        typedArray.recycle();
        View view = null;
        switch (index) {
            case LinearLayout.HORIZONTAL:
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_like_horizontal, this, true);
                break;
            case LinearLayout.VERTICAL:
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_like_vertical, this, true);
                break;
        }
        ivLike = view.findViewById(R.id.iv_like);
        txNumber = view.findViewById(R.id.tx_number);
        view.setOnClickListener(this);
    }

    /**
     * 设置初始状态
     *
     * @param isLike
     * @param likeNumber
     */
    public void setLike(boolean isLike, int likeNumber) {
        this.isLike = isLike;
        this.likeNumber = likeNumber;
        txNumber.setText(String.valueOf(likeNumber));
        if (isLike) {
            ivLike.setBackgroundResource(R.drawable.icon_like_pressed);
            txNumber.setTextColor(ContextCompat.getColor(getContext(), R.color.color_like_pressed));
        } else {
            ivLike.setBackgroundResource(R.drawable.icon_like_normal);
            txNumber.setTextColor(ContextCompat.getColor(getContext(), R.color.color_like_normal));
        }
    }


    @Override
    public void onClick(View v) {
        if (isLike) {
            //已点赞,取消点赞或者开始喷射动画
            if (animaterRunning) {
                //动画未结束，开始喷射动画、点赞动画
                startJetAnimator();
                startLikedAnimator();
            } else {
                //取消点赞
                cacelLiked();
            }
        } else {
            //未点赞，开始点赞
            startLiked();
            //开始喷射动画
            startJetAnimator();
        }
    }

    /**
     * 点赞动画
     */
    private void startLikedAnimator() {
        if (ivSizeSet == null) {
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(ivLike, "scaleX", 1f, 1.2f, 1f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(ivLike, "scaleY", 1f, 1.2f, 1f);
            ivSizeSet = new AnimatorSet();
            ivSizeSet.playTogether(animatorX, animatorY);
            ivSizeSet.setDuration(sizeDuration);
            ivSizeSet.setInterpolator(new LinearInterpolator());
        }
        if (!ivSizeSet.isRunning()) {
            ivSizeSet.start();
        }
    }

    /**
     * 点赞
     */
    private void startLiked() {
        ++likeNumber;
        setLike(true, likeNumber);
    }

    /**
     * 取消点赞
     */
    private void cacelLiked() {
        --likeNumber;
        setLike(false, likeNumber);
    }

    /**
     * 开启喷射动画
     */
    private void startJetAnimator() {
        animaterRunning = true;
        ParticleSystem ps = new ParticleSystem((Activity) getContext(), 100, iconInts, jetDuration);
        ps.setScaleRange(0.7f, 1.3f);
        ps.setSpeedModuleAndAngleRange(0.1f, 0.5f, 180, 360);
        ps.setAcceleration(0.0001f, 90);
        ps.setRotationSpeedRange(90, 180);
        ps.setFadeOut(200, new AccelerateInterpolator());
        ps.oneShot(this, 10, new DecelerateInterpolator());

        clickNum++;
        handler.sendEmptyMessageDelayed(clickNum, jetDuration);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == clickNum) {
                animaterRunning = false;
                clickNum = 0;
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (clickNum > 0) {
            for (int i = clickNum; i > 0; i--) {
                handler.removeMessages(i);
            }
        }
    }
}

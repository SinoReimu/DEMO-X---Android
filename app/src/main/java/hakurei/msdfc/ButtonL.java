package hakurei.msdfc;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

/**
 * Created by HakureiSino on 2016/4/17.
 */
public class ButtonL extends Button {
    boolean isGoing = false;
    ValueAnimator a ;
    int r;
    Paint p;
    public ButtonL(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        a = ValueAnimator.ofInt(Dp2Px(getContext(), 70), 0);
        a.setInterpolator(new mIn());
        a.setDuration(1300);
        a.setRepeatCount(1000000);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                 r = (Integer) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isGoing) {
            canvas.drawCircle(Dp2Px(getContext(), 70), Dp2Px(getContext(), 70), r, p);
            canvas.drawCircle(Dp2Px(getContext(), 70), Dp2Px(getContext(), 70), Dp2Px(getContext(), 70) - r, p);
        }
    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public void startD () {
        a.start();
        isGoing = true;
    }
    public void startO () {
        a.setRepeatCount(2);
        a.start();
    }
    public void endD () {
        a.end();
        isGoing = false;
    }
    private class mIn implements Interpolator{

        @Override
        public float getInterpolation(float input) {
            return input*input*input;
        }
    }
}

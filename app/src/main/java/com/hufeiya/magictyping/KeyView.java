package com.hufeiya.magictyping;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by hufeiya on 16/3/24.
 */
public class KeyView extends TextView {

    private boolean isHover = false;
    private boolean isCancelDelay = false;
    private int key;

    private KeyOutputListener keyOutputListener;

    public KeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            isHover = true;
            setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            if(! isCancelDelay){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isCancelDelay || isHover){
                            isHover = false;
                            isCancelDelay = false;
                            setBackgroundColor(getResources().getColor(R.color.gray));
                        }
                    }
                },1000);
            }


        }else if(isHover && event.getAction() == MotionEvent.ACTION_DOWN){
            isHover = false;
            isCancelDelay = true;
            setBackgroundColor(getResources().getColor(R.color.gray));
            keyOutputListener.getOutput(key);
        }
        return true;
    }

    public interface KeyOutputListener{
        public void getOutput(int key);
    }

    public KeyView(Context context) {
        super(context);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isHover() {
        return isHover;
    }

    public void setHover(boolean hover) {
        isHover = hover;
    }

    public KeyOutputListener getKeyOutputListener() {
        return keyOutputListener;
    }

    public void setKeyOutputListener(KeyOutputListener keyOutputListener) {
        this.keyOutputListener = keyOutputListener;
    }
}

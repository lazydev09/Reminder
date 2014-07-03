package com.vf.widget;

import java.io.InputStream;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.vf.reminder.R;





public class GIFView extends View {
	
	 private Movie mMovie;
	 long movieStart;
	 
    public GIFView(Context context) {
        super(context);
    }
 
    public GIFView(Context context, AttributeSet attrs) {
        super(context, attrs);
       
    }
 
    public GIFView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
  
 
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (mMovie != null) {
            int relTime = (int) ((now - movieStart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas, getWidth() - mMovie.width(), getHeight() - mMovie.height());
            this.invalidate();
        }
    }
    private int gifId;
    
    public void setGIFResource(int resId) {
        this.gifId = resId;
        initializeView();
    }
 
    public int getGIFResource() {
        return this.gifId;
    }
 
    private void initializeView() {
        if (gifId != 0) {
            InputStream is = getContext().getResources().openRawResource(gifId);
            mMovie = Movie.decodeStream(is);
            movieStart = 0;
            this.invalidate();
        }
    }
  
 
}
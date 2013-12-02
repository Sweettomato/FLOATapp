package com.ec327.ballon;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.app.Activity;
import android.app.NativeActivity;
import android.os.SystemClock;
import android.os.Handler;

public class GameScreen extends  NativeActivity {
	private ImageView balloon = null;
	private int seconds = 0, minutes = 0;
	private Handler handler = new Handler();
	private TextView textViewTimer;
	private Runnable updateTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen);
		
		balloon = (ImageView)findViewById(R.id.balloon);
		textViewTimer = (TextView)findViewById(R.id.textViewTimer);
		/*updateTime = new Runnable() {
			public void run() {
				seconds++;
				if(seconds >= 60)
				{
					minutes++;
					seconds = 0;
				}
				
				String secondString = Integer.toString(seconds);
				if(seconds < 10)
				{
					secondString = "0" + secondString;
				}
				String minuteString = Integer.toString(minutes);
				if(minutes < 10)
				{
					minuteString = "0" + minuteString;
				}
				textViewTimer.setText(minuteString + ":" + secondString);
				handler.postDelayed(this, 1000);
			}
		};
		
		handler.postDelayed(updateTime, 0);*/
	}
	
    static {
        System.loadLibrary("balloon");
    }
    
    GameScreen _activity;
    PopupWindow _popupWindow;
    TextView _label;
    
    public void showUI()
    {
        if( _popupWindow != null )
            return;

        _activity = this;

        this.runOnUiThread(new Runnable()  {
            @Override
            public void run()  {
                LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.game_screen, null);
                _popupWindow = new PopupWindow(
                        popupView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);

                LinearLayout mainLayout = new LinearLayout(_activity);
                MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                _activity.setContentView(mainLayout, params);

                // Show our UI over NativeActivity window
                _popupWindow.showAtLocation(mainLayout, Gravity.TOP | Gravity.LEFT, 10, 10);
                _popupWindow.update();

                _label = (TextView)popupView.findViewById(R.id.textViewTimer);

            }});
    }
    
/*    public void updateFPS(final float fFPS)
    {
        if( _label == null )
            return;

        _activity = this;
        this.runOnUiThread(new Runnable()  {
            @Override
            public void run()  {
                _label.setText(String.format("%2.2f FPS", fFPS));

            }});
    }*/
}

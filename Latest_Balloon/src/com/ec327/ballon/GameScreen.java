package com.ec327.ballon;

import android.app.NativeActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class GameScreen extends NativeActivity {
	private ImageView balloon = null;
	private int seconds = 0, minutes = 0;
	private Handler handler = new Handler();
	private TextView textViewTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.game_screen);
		balloon = (ImageView) findViewById(R.id.balloon);
		textViewTimer = (TextView) findViewById(R.id.textViewTimer);
/*		updateTime = new Runnable() {
			public void run() {
				seconds++;
				if (seconds >= 60) {
					minutes++;
					seconds = 0;
				}

				String secondString = Integer.toString(seconds);
				if (seconds < 10) {
					secondString = "0" + secondString;
				}
				String minuteString = Integer.toString(minutes);
				if (minutes < 10) {
					minuteString = "0" + minuteString;
				}
				textViewTimer.setText(minuteString + ":" + secondString);
				handler.postDelayed(this, 1000);
			}
		};*/

		//handler.postDelayed(updateTime, 0);
	}

	static {
		System.loadLibrary("balloon");
	}

	GameScreen _activity;
	PopupWindow _popupWindow;
	TextView _label;
	ImageView _balloon;

	public void showUI() {
		if (_popupWindow != null)
			return;

		_activity = this;

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = layoutInflater.inflate(R.layout.game_screen,
						null);
				_popupWindow = new PopupWindow(popupView,
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

				LinearLayout mainLayout = new LinearLayout(_activity);
				MarginLayoutParams params = new MarginLayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				params.setMargins(0, 0, 0, 0);
				_activity.setContentView(mainLayout, params);

				// Show our UI over NativeActivity window
				_popupWindow.showAtLocation(mainLayout, Gravity.TOP
						| Gravity.LEFT, 0, 0);
				_popupWindow.update();
				
				_label = (TextView) popupView.findViewById(R.id.textViewTimer);
				_balloon = (ImageView) popupView.findViewById(R.id.balloon);
			}
		});
	}
	
	public void updateTime() {
		if (_label !=null)
			return;
		_activity = this;
		this.runOnUiThread(new Runnable() {
			public void run() {
				seconds++;
				if (seconds >= 60) {
					minutes++;
					seconds = 0;
				}

				String secondString = Integer.toString(seconds);
				if (seconds < 10) {
					secondString = "0" + secondString;
				}
				String minuteString = Integer.toString(minutes);
				if (minutes < 10) {
					minuteString = "0" + minuteString;
				}
				//_label.setText(minuteString + ":" + secondString);
				_label.setText(String.format("%s: %s", minuteString, secondString));
				handler.postDelayed(this, 1000);
			}
		});
		
	}

}

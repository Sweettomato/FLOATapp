package com.ec327.ballon;

import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.WindowManager;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class HelpScreen extends Activity {
	private TextView textview = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the layout to the help_screen.xml
		setContentView(R.layout.help_screen);
		
		// keep the screen from dimming
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// the textview that the help instructions are in is referenced so that
		// it can scroll if the instuctions are too big for the screen
		textview = (TextView)findViewById(R.id.textview);
		textview.setMovementMethod(new ScrollingMovementMethod());
	}

}

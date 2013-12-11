package com.ec327.ballon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class PopUp extends Activity{
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// set the layout to the pause_popup.xml
		setContentView(R.layout.pause_popup);
		
		// keep the screen from dimming
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	// calls finish on the popup
	public void resumeGame(View v)
	{
		PopUp.this.finish();
	}

}

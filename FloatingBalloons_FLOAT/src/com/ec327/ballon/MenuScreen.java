package com.ec327.ballon;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MenuScreen extends Activity{
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the layout to the menu_screen.xml
		setContentView(R.layout.menu_screen);
		
		// keep the screen from dimming
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	// when the play button is clicked, an intent is created to start the GameScreen
	// activity, and the finish() is called on the MenuScreen activity
	public void playGame(View v)
	{
		Intent mainIntent = new Intent(MenuScreen.this, GameScreen.class);
		MenuScreen.this.startActivity(mainIntent);
		MenuScreen.this.finish();
	}
	
	// when the high scores button is clicked, an intent is created to start the
	// HighScoresScreen activity
	public void viewHighScores(View v)
	{
		Intent mainIntent = new Intent(MenuScreen.this, HighScoresScreen.class);
		MenuScreen.this.startActivity(mainIntent);
	}
	
	// when the help button is clicked, an intent is created to start the Help activity
	public void getHelp(View v)
	{
		Intent mainIntent = new Intent(MenuScreen.this, HelpScreen.class);
		MenuScreen.this.startActivity(mainIntent);
	}
	
	// when the quit button is clicked, finish() is called on the activity to end the game
	public void quitGame(View v)
	{
		MenuScreen.this.finish();
	}

}

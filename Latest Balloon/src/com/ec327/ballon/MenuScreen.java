package com.ec327.ballon;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MenuScreen extends Activity{
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_screen);
	}

	public void playGame(View v)
	{
		Intent mainIntent = new Intent(MenuScreen.this, GameScreen.class);
		MenuScreen.this.startActivity(mainIntent);
	}
	
	public void viewHighScores(View v)
	{
		Intent mainIntent = new Intent(MenuScreen.this, HighScoresScreen.class);
		MenuScreen.this.startActivity(mainIntent);
	}
	
	public void getHelp(View v)
	{
		Intent mainIntent = new Intent(MenuScreen.this, HelpScreen.class);
		MenuScreen.this.startActivity(mainIntent);
	}
	
	public void quitGame(View v)
	{
		MenuScreen.this.finish();
	}

}

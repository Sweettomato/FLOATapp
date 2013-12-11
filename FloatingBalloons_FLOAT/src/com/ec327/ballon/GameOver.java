package com.ec327.ballon;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class GameOver extends Activity{
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// set the layout to the game_over.xml
		setContentView(R.layout.game_over);
		
		// keep the screen from dimming
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	// if the menu button is clicked, an intent is created to go to the main menu
	// and finish is called on the GameOver activity
	public void goToMenu(View v)
	{
		Intent mainIntent = new Intent(GameOver.this, MenuScreen.class);
		GameOver.this.startActivity(mainIntent);
		GameOver.this.finish();
	}
	
	// overwrite the back button to take the user to the GameScreen
	@Override
	public void onBackPressed() {
		Intent mainIntent = new Intent(GameOver.this, GameScreen.class);
		GameOver.this.startActivity(mainIntent);
		GameOver.this.finish();
		}
	
	// if the play again button is clicked, an intent is created to go to the
	// GameScreen activity and play the game again; finish is then called on
	// the GameOver activity
	public void playGame(View v)
	{
		Intent mainIntent = new Intent(GameOver.this, GameScreen.class);
		GameOver.this.startActivity(mainIntent);
		GameOver.this.finish();
	}

}

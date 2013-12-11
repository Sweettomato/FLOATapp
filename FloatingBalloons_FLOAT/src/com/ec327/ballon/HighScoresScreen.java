package com.ec327.ballon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class HighScoresScreen extends Activity {
	
	private TextView one = null, two = null, three = null, four = null, five = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the layout to the high_scores_screen.xml
		setContentView(R.layout.high_scores_screen);
		
		// keep the screen from dimming
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// the different textviews for the screen are defined
		one = (TextView)findViewById(R.id.one);
		two = (TextView)findViewById(R.id.two);
		three = (TextView)findViewById(R.id.three);
		four = (TextView)findViewById(R.id.four);
		five = (TextView)findViewById(R.id.five);
		
		// the high scores arraylist is given a default five scores of 0
		InputStreamReader inScores;
		ArrayList<Integer> highScores = new ArrayList<Integer>();
		for(int x = 0; x < 5; x++)
		{
			highScores.add(0);
		}
		File inFile = getFileStreamPath("high_scores");
		
		// if the "high_scores" file exists, the values are read in to the high scores arraylist
		if(!(inFile.length() == 0))
		{
			try {
				inScores = new InputStreamReader(openFileInput("high_scores"));
				BufferedReader reader = new BufferedReader(inScores);
				String str;
				highScores = new ArrayList<Integer>();
				
				
				while((str = reader.readLine()) != null)
				{
					highScores.add(Integer.parseInt(str));
				}
				inScores.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// the textviews are assigned their respective score
		one.setText(formatScore(highScores.get(0)));
		two.setText(formatScore(highScores.get(1)));
		three.setText(formatScore(highScores.get(2)));
		four.setText(formatScore(highScores.get(3)));
		five.setText(formatScore(highScores.get(4)));
	}
	
	// inserts commas into the score and returns a String
	public String formatScore(Integer num)
	{
		String str = Integer.toString(num);
		for(int x = str.length() - 3; x > 0; x-=3)
		{
			str = str.substring(0, x) + "," + str.substring(x);
		}
		return str;
	}

}

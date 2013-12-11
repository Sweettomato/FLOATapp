package com.ec327.ballon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class GameScreen extends Activity implements SensorEventListener {

	final String tag = "AccLogger";	//Sensor stuff
	SensorManager sensore=null;
	long startMillis = 0, pauseDelay = 0, pauseTime = 0;			//Timing variables
	int seconds = 0, minutes = 0;
	TextView time_text = null;
	Handler handler = new Handler();

	MySurfaceView mySurfaceView;	//Display related things
	Bitmap balloon;
	Canvas canvas;

	Random random = new Random();

	private int mWidthScreen, mHeightScreen; 		//Display Screen width and height

	private final float FACTOR_FRICTION = 2.0f; // imaginary friction on the screen 
	private final float GRAVITY = 9.8f; // acceleration of gravity
	private float mAx, mAy; // acceleration along x axis
	//private float mAy; // acceleration along y axis
	private final float mDeltaT = 0.7f; 
	private final float maxV = 20;

	boolean gamePaused = false;

	public Rect[] rectArray = new Rect[7];{ 		//Collision space for obstacles
		for(int i=0; i<7; i++){
			rectArray[i] = new Rect();
		}
	}
	
	private final Rect emptyRect = new Rect();		//makes sure spikes don't appear in the block opening
	private final Rect balloonRect = new Rect();	//Collision space for balloon
	private final Rect spikeRect = new Rect();		//Collision space for single spikes
	private final Rect spike2Rect = new Rect();		
	private final Rect pauseRect = new Rect(); 		//Pause Button
	
	private MediaPlayer mPlayer;                    //declare MediaPlayer variable

	@Override
	protected void onCreate(Bundle savedInstanceState) {	//What to do during onCreate cycle
		super.onCreate(savedInstanceState);

		final Display display = ((WindowManager) this		//Gets the display as well as its height and width
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mWidthScreen = display.getWidth();
		mHeightScreen = display.getHeight();

		mySurfaceView = new MySurfaceView(this);	//Creates an object of class MySurfaceView
		mySurfaceView.setBalloon((int) (mWidthScreen * 0.4),	//Sets location of balloon to near center of screen
				(int) (mHeightScreen * 0.6));
		mySurfaceView.setBlock(0,mHeightScreen+1);		
		mySurfaceView.setPause(mWidthScreen - (int)(mWidthScreen * 0.05) - 50, mHeightScreen - (int)(mHeightScreen * 0.05) - 50);	//Sets location of pause button
		setContentView(mySurfaceView);		//Sets the view to be MySurfaceView
		mySurfaceView.setSpike(random.nextInt(mWidthScreen),random.nextInt(mHeightScreen));
		mySurfaceView.setSpike2(random.nextInt(mWidthScreen),random.nextInt(mHeightScreen));
		mySurfaceView.setEmpty(0,mHeightScreen+1);

		startMillis = SystemClock.uptimeMillis();	//Start counting

		sensore = (SensorManager) getSystemService(SENSOR_SERVICE);

		// keep the screen from dimming
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	protected void onResume() {		//onResume cycle - turns on accelerometer and screen
		super.onResume();
		Sensor Accel = sensore.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// register this class as a listener for the orientation and accelerometer sensors
		sensore.registerListener((SensorEventListener) this, Accel,SensorManager.SENSOR_DELAY_FASTEST);
		mySurfaceView.onResumeMySurfaceView();
		
		//create the music player and set the looping to true
	    mPlayer = MediaPlayer.create(this, R.raw.float_theme_extended);
	    mPlayer.setLooping(true);
	    
	    // starts the music
	    mPlayer.start();
	}


	protected void onPause() {		//onPause cycle - turns off accelerometer and screen
		super.onPause();
		sensore.unregisterListener(this);	
		mySurfaceView.onPauseMySurfaceView();
		
		// stops the music player
		mPlayer.stop();
	}

	protected void onDestroy()		//onDestroy cycle - gets high scores
	{
		super.onDestroy();

		// get the score from the getScore function
		int score = Math.round(getScore());

		OutputStreamWriter outScores;
		InputStreamReader inScores;

		// give highScores arraylist default values of 0
		ArrayList<Integer> highScores = new ArrayList<Integer>();
		for(int x = 0; x < 5; x++)
		{
			highScores.add(0);
		}
		File inFile = getFileStreamPath("high_scores");


		// read in the high scores if the "high_scores" file exists
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

		// insert the score into the list and shift the rest down if the score
		// is greater than any of the other scores
		int size = highScores.size();
		for(int x = 0; x < size; x++)
		{
			if(score > highScores.get(x))
			{
				for(int n = size - 1; n > x; n--)
				{
					highScores.set(n, highScores.get(n-1));
				}
				highScores.set(x, score);
				break;
			}
		}		

		// overwrite the current high scores file with the highScores arraylist
		try {
			outScores = new OutputStreamWriter(openFileOutput("high_scores", MODE_WORLD_READABLE));
			for(int x = 0; x < size; x++)
			{
				outScores.write(highScores.get(x) + "\n");
			}
			outScores.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// calculate the score based on how long the user has been playing the game and account for any pauses
	public long getScore()
	{
		long passedTime = SystemClock.uptimeMillis() - startMillis;
		seconds = Math.round(TimeUnit.MILLISECONDS.toMillis(passedTime)) - Math.round(pauseDelay);

		if(gamePaused)
		{
			pauseDelay += TimeUnit.MILLISECONDS.toMillis(SystemClock.uptimeMillis() - pauseTime);
			seconds -= pauseDelay;
			gamePaused = false;
		}

		return (long) (seconds * 0.2);
	}

	// overwrite the back button to take the user to the menuscreen
	@Override
	public void onBackPressed() {
		Intent mainIntent = new Intent(GameScreen.this, MenuScreen.class);
		GameScreen.this.startActivity(mainIntent);
		GameScreen.this.finish();
	}

	public void onSensorChanged(SensorEvent event){ //Changes value of acceleration of balloon based on accelerometer values
		mAx = event.values[0];
		mAy = event.values[1];

		final float mAz = event.values[2];

		// taking into account the frictions
		mAx = -Math.signum(mAx) * Math.abs(mAx)
				* (1 - FACTOR_FRICTION * Math.abs(mAz) / GRAVITY);
		mAy = -Math.signum(mAy) * Math.abs(mAy)
				* (1 - FACTOR_FRICTION * Math.abs(mAz) / GRAVITY);     
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) { //Originally used to output to LogCat and testing
		//Log.d(tag,"onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
	}

	class MySurfaceView extends SurfaceView implements Runnable{
		Paint p = new Paint();
		Thread thread = null;
		SurfaceHolder surfaceHolder;
		volatile boolean running = false;
		private final float FACTOR_BOUNCEBACK = 0.50f;

		private int speedCount;

		private int mX, mY;	//Coordinates of balloon
		private int bX, bY;	//Coordinates of left most obstacle
		private int pX, pY;	//Coordinates of pause button
		private int sX, sY; //Coordinates of single spike
		private int sX2, sY2;//Coordinates of 2nd single spike
		private int eX, eY; //Coordinates of the empty block

		private float mVx;		//Horizontal speed of balloon
		private float bVy = 5;	//Vertical speed of obstacle row

		private int missingNo = 3;	//The left empty space in line of obstacles 

		//Gets image from files, renders them, and then scales them according to screen size
		Bitmap bigBalloon = BitmapFactory.decodeResource(getResources(),  R.drawable.yellow_balloon); //balloon
		Bitmap balloon = Bitmap.createScaledBitmap(bigBalloon, mWidthScreen/10, mHeightScreen/8, true);
		Bitmap bigBlock1 = BitmapFactory.decodeResource(getResources(), R.drawable.stone_onev2); //main block obstacle
		Bitmap block1 = Bitmap.createScaledBitmap(bigBlock1, mWidthScreen/7, mHeightScreen/12, true);
		Bitmap bigSpike = BitmapFactory.decodeResource(getResources(), R.drawable.single_spike); //single spike obstacle
		Bitmap spike = Bitmap.createScaledBitmap(bigSpike, mWidthScreen/20, mHeightScreen/9, true);
		Bitmap bigPause = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button); //pause button
		Bitmap pause = Bitmap.createScaledBitmap(bigPause, mWidthScreen/15, mHeightScreen/20, true);

		//Actual images of obstacles 
		Bitmap [] blockImages; {
			blockImages = new Bitmap[7];
			for (int i=0;i<7;i++)
				blockImages[i] = block1;
		}

		public MySurfaceView(Context context) {
			super(context);
			surfaceHolder = getHolder();
		}

		public boolean setBalloon(final int x, final int y) {
			mX = x;
			mY = y;
			return true;
		}
		public boolean setBlock(final int x, final int y) {
			bX = x;
			bY = y;
			return true;
		}

		public boolean setPause(final int x, final int y) {
			pX = x;
			pY = y;
			return true;
		}

		public boolean setSpike(final int x, final int y) {
			sX = x;
			sY = y;
			return true;
		}

		public boolean setSpike2(final int x, final int y) {
			sX = x;
			sY = y;
			return true;
		}
		
		public boolean setEmpty(final int x, final int y){
			eX = x;
			eY = y;
			return true;
		}

		public boolean updateBalloon() { 
			//Updates the location and speed of the balloon according to info from accelerometer
			if(mVx>=maxV && mAx>0){
				mVx = maxV;
			}else if(mVx<=-maxV && mAx<0){
				mVx = -maxV;
			}else{
				mVx += mAx * mDeltaT;
			}

			mX += (int) (mDeltaT * (mVx + 0.6 * mAx * mDeltaT));
			//mYCenter += (int) (mDeltaT * (mVy + 0.6 * mAy * mDeltaT));

			if (mX < 0) {
				mX = 0;
				mVx = -mVx * FACTOR_BOUNCEBACK;
			}

			if (mX + balloon.getWidth() > mWidthScreen) {
				mX = mWidthScreen - balloon.getWidth();
				mVx = -mVx * FACTOR_BOUNCEBACK;
			}
			return true;
		}

		public boolean updateBlock() {		
			//Updates location of block obstacle line
			//If obstacle line goes below the display screen, 
			//it is destroyed and a new line is generated above the screen
			if (bY>mHeightScreen){
				missingNo = random.nextInt(6);	//Chooses randomly which block to not render
				bY = -block1.getHeight();
				eY = -block1.getHeight()-30;
				speedCount++;
				if (bVy<=20 && speedCount==6){	//Sets a limit to how fast block go down
					bVy+=2;
					speedCount = 0;
				}
			}
			bY+=bVy;
			eY+=bVy;
			return true;
		}

		public boolean updateSpike() {
			sY+=bVy;	//Travels at same speed as block obstacles
			if (sY>mHeightScreen) {
				sX = random.nextInt(mWidthScreen)-spike.getWidth();
				sY = -spike.getHeight() - block1.getHeight() - random.nextInt(mHeightScreen - block1.getHeight());
				while(spikeRect.intersect(emptyRect)){
					sX = random.nextInt(mWidthScreen)-spike.getWidth();
					sY = -spike.getHeight() - block1.getHeight() - random.nextInt(mHeightScreen - block1.getHeight());
				}
			}
			sY2+=bVy;	//Travels at same speed as block obstacles
			if (sY2>mHeightScreen) {
				sX2 = random.nextInt(mWidthScreen)-spike.getWidth();
				sY2 = -spike.getHeight() - block1.getHeight() - random.nextInt(mHeightScreen - block1.getHeight());
				while(spike2Rect.intersect(emptyRect)){
					sX2 = random.nextInt(mWidthScreen)-spike.getWidth();
					sY2 = -spike.getHeight() - block1.getHeight() - random.nextInt(mHeightScreen - block1.getHeight());
				}
			}
			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) { //Takes care of clicking the pause button by starting the PopUp activity
			int touchX = (int) event.getX();
			int touchY = (int) event.getY();
			pauseTime = SystemClock.uptimeMillis();
			gamePaused = true;
			if(event.getAction() == MotionEvent.ACTION_DOWN && pauseRect.contains(touchX, touchY))
			{
				Intent mainIntent = new Intent(GameScreen.this, PopUp.class);
				GameScreen.this.startActivity(mainIntent);
			}
			return true;
		}

		public void onResumeMySurfaceView(){ //Used by onResume cycle to resume the screen
			running = true;
			thread = new Thread(this);
			thread.start();
		}

		public void onPauseMySurfaceView(){ //Used by onPause cycle to pause the screen
			boolean retry = true;
			running = false;
			while(retry){
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void render(){	
			//Takes care of setting the locations of Rect's 
			//Renders the objects according to the coordinates
			Canvas canvas = surfaceHolder.lockCanvas();		//lock down Canvas to draw on it
			canvas.drawColor(Color.rgb(138, 225, 228));		//Sets background color
			balloonRect.set(mX, mY, mX + balloon.getWidth(), mY + 68);
			pauseRect.set(pX, pY, pX+pause.getWidth(), pY+pause.getHeight());
			spikeRect.set(sX, sY, sX+spike.getWidth(), sY+spike.getHeight());
			spike2Rect.set(sX2, sY2, sX2+spike.getWidth(), sY2+spike.getHeight());
			emptyRect.set(missingNo*block1.getWidth(), bY-30, (missingNo+2)*block1.getWidth(), bY+block1.getHeight()+30);

			for(int i=0; i<7; i++){		//Draws row of block obstacles with missing two spaces
				if (i!=missingNo && i!=missingNo+1){
					rectArray[i].set(bX+(i*block1.getWidth()), bY, bX+(i*block1.getWidth())+block1.getWidth(), bY+block1.getHeight());
					canvas.drawBitmap(blockImages[i], bX+(i*block1.getWidth()), bY, p);
				}
			}	
			canvas.drawBitmap(balloon, mX, mY, p);
			canvas.drawBitmap(pause, pX, pY, p);
			canvas.drawBitmap(spike, sX, sY, p);
			canvas.drawBitmap(spike, sX2, sY2, p);
			p.setTextSize(80);
			canvas.drawText(getScore() + "", 20, 80, p);	//Displays text for the score
			surfaceHolder.unlockCanvasAndPost(canvas);	//Unlocks Canvas for displaying
		}

		public void checkCollision() {
			if (balloonRect.intersect(rectArray[0]) || balloonRect.intersect(rectArray[1])
					|| balloonRect.intersect(rectArray[2]) || balloonRect.intersect(rectArray[3])
					|| balloonRect.intersect(rectArray[4]) || balloonRect.intersect(rectArray[5])
					|| balloonRect.intersect(rectArray[6])) {
				if (mY<=(bY+block1.getHeight()) && mY>=(bY+block1.getHeight()-bVy)) {
					mY=bY+block1.getHeight();
				} else if (mX<=((missingNo)*block1.getWidth()) && mX >=(missingNo*block1.getWidth()) - block1.getWidth()/3.5) { 	//Balloon's left hits block's right
					mX = missingNo*block1.getWidth();
					mVx = 0;
				} else if (mX+balloon.getWidth()>=(missingNo+2)*block1.getWidth() && mX+balloon.getWidth()<((missingNo+2)*block1.getWidth() + block1.getWidth()/3.5)){	//Balloon's right hits block's left
					mX = (missingNo+2)*block1.getWidth()-balloon.getWidth();
					mVx = 0;
				}
			} else {
				if (mY>=mHeightScreen/3)
					mY-=bVy/2;
			}
			if (balloonRect.intersect(spikeRect) || balloonRect.intersect(spike2Rect)) {
				Intent mainIntent = new Intent(GameScreen.this, GameOver.class);
				GameScreen.this.startActivity(mainIntent);
				GameScreen.this.finish();
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(running){
				if(surfaceHolder.getSurface().isValid()){
					updateBalloon();
					updateBlock();//Updates location of balloon and obstacles
					updateSpike();
					render();
					checkCollision();
					if (mY>=mHeightScreen) {
						Intent mainIntent = new Intent(GameScreen.this, GameOver.class);
						GameScreen.this.startActivity(mainIntent);
						GameScreen.this.finish();
					}
				}
			}
		}
	}
}
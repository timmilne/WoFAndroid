package com.tpm.wofandroid;

import android.app.Activity;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.os.Handler;
import java.util.Hashtable;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity implements OnPreparedListener, MediaController.MediaPlayerControl {
	
	private MediaPlayer 	mMediaPlayer;
	private MediaController	mMediaController;
	private Handler 		mHandler;
	
	// Set up a hashmap to map of view ids to sounds
	protected Hashtable<Integer, Integer> mSoundsHashtable = new Hashtable<Integer, Integer>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        // Set up a hashmap to map of view ids to sounds
    	mSoundsHashtable.put(R.id.button_new_round, 	R.raw.wof_applause);
    	mSoundsHashtable.put(R.id.button_ding, 			R.raw.wof_ding);
    	mSoundsHashtable.put(R.id.button_buzz, 			R.raw.wof_buzz);
    	mSoundsHashtable.put(R.id.button_puzzle_solved,	R.raw.wof_final_spin);
    	mSoundsHashtable.put(R.id.button_applause, 		R.raw.wof_applause);
    	mSoundsHashtable.put(R.id.button_final_spin, 	R.raw.wof_final_spin);
    	mSoundsHashtable.put(R.id.button_double_buzz, 	R.raw.wof_double_buzz);
    	mSoundsHashtable.put(R.id.button_theme, 		R.raw.wof_theme);
    	
    	mHandler = new Handler();
    	
    	// Set up the controller with an anonymous inline that overrides a couple of methods
    	mMediaController = new MediaController(this){
            @Override
            public void hide() {
                // Do Nothing, never goes away once displayed
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

            	// Ignore everything but the back key
                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                    if (mMediaPlayer != null) {
                    	mMediaPlayer.reset();
                    	mMediaPlayer.release();
                    	mMediaPlayer = null;
                    }
                    super.hide();
                    Activity a = (Activity)getContext();
                    a.finish();

                }
            return true;
            }
        };
        
        // Now, start the theme to get things rolling
        this.playSound(R.raw.wof_theme);
    }
    
    public void handler(View v) {

    	// Get and play the associated the sound
    	Integer soundID = mSoundsHashtable.get(v.getId());
    	if (soundID != null) playSound(soundID);
    	
    	// If end of round, after the ding, play the applause and start up the theme music
    	if (v.getId() == R.id.button_puzzle_solved)
    	{
    		try {
    			Thread.sleep(2000);
    		}
    		catch (InterruptedException IE) {} // Interrupted, proceed
    		playSound (R.raw.wof_applause);
    		
    		try {
    			Thread.sleep(3400);
    		}
    		catch (InterruptedException IE) {} // Interrupted, proceed
    		playSound (R.raw.wof_theme);
    	}
    }
    
    public void playSound (Integer soundID) {
    	// Release any previous media players
    	if (mMediaPlayer != null) {  		
    		mMediaPlayer.stop();
    		mMediaPlayer.release();
    	}
    	
    	// Load and play the sound
    	mMediaPlayer = MediaPlayer.create(this, soundID);
    	if (mMediaPlayer != null)
    	{
    		mMediaPlayer.setOnPreparedListener(this);
    		mMediaPlayer.start();
    	}
    }
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }
    
    //--OnPreparedListener methods----------------------------------------------------
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
    	mMediaController.setMediaPlayer(this);
    	mMediaController.setAnchorView(findViewById(R.id.media_controller));
    	
    	mHandler.post(new Runnable() {
    		public void run() {
    			mMediaController.setEnabled(true);
    			// TPM This did work, but the anonymous inline class above was cooler...
    			//mMediaController.show(0);
    			mMediaController.show();
    		}
    	});
    }
    
    //--MediaPlayerControl methods----------------------------------------------------
    @Override
    public void start() {
    	mMediaPlayer.start();
    }
    
    @Override
    public void pause() {
        if(mMediaPlayer.isPlaying()) mMediaPlayer.pause();
    }
    
    @Override
    public int getDuration() {
    	return mMediaPlayer.getDuration();
    }
    
    @Override
    public int getCurrentPosition() {
    	return mMediaPlayer.getCurrentPosition();
    }
    
    @Override
    public void seekTo(int i) {
    	mMediaPlayer.seekTo(i);
    }
    
    @Override
    public boolean isPlaying() {
    	return mMediaPlayer.isPlaying();
    }
    
    @Override
    public int getBufferPercentage() {
//    	int percentage = (mMediaPlayer.getCurrentPosition() * 100)/mMediaPlayer.getDuration();
    	return ((mMediaPlayer.getCurrentPosition() * 100)/mMediaPlayer.getDuration());
    }
    
    @Override
    public boolean canPause() {
    	return true;
    }
    
    @Override
    public boolean canSeekBackward() {
    	return true;
    }
    
    @Override
    public boolean canSeekForward() {
    	return true;
    }
    
    @Override
    public int getAudioSessionId() {
    	return mMediaPlayer.getAudioSessionId();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mMediaController.show();
        
        return false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioSystem;

import dataStructures.BellEvent;
import dataStructures.SoundFile;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author Quinten Holmes
 */
public class AudioPlayer {
    
    private MediaPlayer mediaPlayer;
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    private AudioPlayer player;
    private BellEvent event;
    private boolean playingTrack = false;
    private final Timer timer;
    private TimerTask lastTask;
    
    public AudioPlayer getPlayer(){
        if(player == null)
            player = new AudioPlayer();
        
        return player;
    }
    
    private AudioPlayer(){
       playingTrack = false;
       timer = new Timer();
    }
    
    public void setBellEvent(BellEvent b){
        //End current task if applicable 
        if(lastTask != null && event != null)
            lastTask.cancel();
        
        event = b;
        
        if(b == null)
            return;
        
        scheduleEvent();
            
    }
    
    public BellEvent getBellEvent(){
        return event;
    }
    
    private void play(SoundFile file, int duration, Object ob){
        if(mediaPlayer !=null)
            mediaPlayer.stop();
        try{
            Media hit = file.getMedia();
            mediaPlayer = new MediaPlayer(hit);
            playingTrack = true;
            mediaPlayer.setAutoPlay(false);
            
            if(duration > 0)
                mediaPlayer.setStopTime(Duration.seconds(duration));
            
            final Object signal = ob;
            mediaPlayer.setOnStopped(new Thread(){
                @Override
                public void run(){
                    playingTrack = false;
                    synchronized(signal){signal.notify();}
                }
            });
            
            mediaPlayer.play();
        }catch(Exception e){
            e.printStackTrace();
        }
    } 

    public boolean isPlayingTrack() {
        return playingTrack;
    }
    
    public void stop() {
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }
    
    private TimerTask scheduleEvent(){
        TimerTask th = new TimerTask(){
            @Override
            public void run() {
                try {
                    synchronized(this){
                        if(event.isCancelled())
                            return;
                        
                        if(event.isPlayPreBell()){
                            play(event.getPreBell(), -1, this);
                            event.setRunning(true);
                            wait();
                            event.setRunning(false);
                        }
                        
                        if(event.isCancelled())
                            return;
                        
                        if(event.isPlaySong()){
                            play(event.getSong(), event.getSongDuration(), this);
                            event.setRunning(true);
                            wait();
                            event.setRunning(false);
                        }
                        
                        if(event.isCancelled())
                            return;
                        
                        if(event.isPlayPostBell()){
                            play(event.getPostBell(), -1, this);
                            event.setRunning(true);
                            wait();
                            event.setRunning(false);
                        }
                    }
                } catch (InterruptedException ex) {
                    //Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                } finally {
                    event = null;
                    lastTask = null;
                }
            }
        };
           
        lastTask = th;
        return lastTask;
    }
    
}

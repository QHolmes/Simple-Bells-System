/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioSystem;

import dataStructures.EventSegment;
import dataStructures.SegmentType;
import dataStructures.schedules.ScheduledEvent;
import dataStructures.SoundFile;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
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
    private static AudioPlayer player;
    private ScheduledEvent event;
    private boolean playingTrack = false;
    private final Timer timer;
    private TimerTask lastTask;
    
    public static AudioPlayer getPlayer(){
        if(player == null)
            player = new AudioPlayer();
        
        return player;
    }
    
    private AudioPlayer(){
       playingTrack = false;
       timer = new Timer();
    }
    
    public void setBellEvent(ScheduledEvent b){
        //End current task if applicable 
        if(lastTask != null && event != null)
            lastTask.cancel();
        
        event = b;
        
        if(b == null)
            return;
        
        scheduleEvent();
        timer.schedule(lastTask, b.getStartTime());
            
    }
    
    public ScheduledEvent getBellEvent(){
        return event;
    }
    
    private void play(SoundFile file, double duration){
        if(mediaPlayer !=null)
            mediaPlayer.stop();
        try{
            Media hit = file.getMedia();
            mediaPlayer = new MediaPlayer(hit);
            playingTrack = true;
            mediaPlayer.setAutoPlay(false);
            
            if(duration > 0)
                mediaPlayer.setStopTime(Duration.seconds(duration));
            
            mediaPlayer.setOnStopped(new Thread(){
                @Override
                public void run(){
                    playingTrack = false;
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
        event = null;
        if(mediaPlayer != null)
            mediaPlayer.stop();
        playingTrack = false;
    }
    
    private TimerTask scheduleEvent(){
        TimerTask th = new TimerTask(){
            @Override
            public void run() {
                long stamp = 0;
                try {
                    stamp = event.readLockSegments();
                    synchronized(this){
                        if(event.isCancelled())
                            return;
                        
                        event.setRunning(true);
                        ArrayList<EventSegment> segments = event.getSegments();
                        for(EventSegment s: segments){
                            s.setRunning(true);
                            if(s.getType() != SegmentType.SILENCE){
                                System.out.printf("Playing %s for %.0f seconds.%n", s.getFile().getFileName(), s.getDuration());
                                play(s.getFile(), s.getDuration());
                                Thread.sleep((long) Math.ceil(s.getDuration()) * 1000);
                                stop();
                            }else{
                                System.out.printf("Sleeping for %.0f seconds. %n", s.getDuration());
                                if(s.getDuration() > 0)
                                    Thread.sleep((long) Math.ceil(s.getDuration()) * 1000);
                            }
                            s.setRunning(false);
                        }
                        
                        event.setRunning(false);
                    }
                } catch (InterruptedException ex) {
                    //Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                } catch (NullPointerException e) {}
                finally {
                    if(stamp != 0)
                        event.unlockReadLockSegments(stamp);
                    
                    event = null;
                    lastTask = null;
                }
            }
        };
           
        lastTask = th;
        return lastTask;
    }
    
    /**
     * Used to play a single SoundFile now. Will interrupt any current event.
     * @param f SoundFile to be played
     * @param duration duration of SoundFile to play
     */
    public void quickPlay(SoundFile f, double duration){
        stop();
        play(f, duration);
    }
    
}

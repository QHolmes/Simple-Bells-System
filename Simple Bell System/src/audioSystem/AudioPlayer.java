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
import java.util.Calendar;
import java.util.Date;
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
    
    public void fullStop() {
        event = null;
        if(mediaPlayer != null)
            mediaPlayer.stop();
        playingTrack = false;
    }
    
    private void stop(){
        if(mediaPlayer != null)
            mediaPlayer.stop();
        playingTrack = false;
    }
    
    private TimerTask scheduleEvent(){
        TimerTask th = new TimerTask(){
            @Override
            public void run() {
                long stamp = 0;
                ScheduledEvent playinegEvent = event;
                
                try {
                    stamp = event.readLockSegments();
                    SoundFile file;
                    synchronized(this){
                        if(event.isCancelled())
                            return;
                        
                        event.setRunning(true);
                        ArrayList<EventSegment> segments = event.getSegments();
                        
                        Date start = event.getStartTime();
                        Date end = event.getStopTime();
                        Date now  = new Date();
                        long diff = (now.getTime() - start.getTime()) / 1000;
                        
                        double duration = 0;
                        int index = -1;
                        
                        while(duration <= 0.0 && index < segments.size()){
                            index++;
                            duration = segments.get(index).getDuration() - diff;
                            diff -= segments.get(index).getDuration();
                        }
                        
                        if(duration <= 0.0 || index >= segments.size()){
                            return;
                        }
                        EventSegment s;
                        for(int i = index; i < segments.size() && event != null; i++){
                            s = segments.get(i);
                            s.setRunning(true);
                            file = s.getFile();
                            if(s.getType() == SegmentType.SOUND || s.getType() == SegmentType.BELL && file != null){
                                System.out.printf("Playing %s for %.0f seconds.%n", file.getFileName(), duration);
                                play(file, duration);
                                Thread.sleep((long) Math.ceil(duration) * 1000);
                                stop();
                            }else if (s.getType() == SegmentType.PLAYLIST && file != null && s.getPlayList() != null){
                                System.out.printf("Playing %s for %.0f seconds. From playlist %s.%n", 
                                        file.getFileName(), duration, s.getPlayList().toString());
                                play(file, duration);
                                Thread.sleep((long) Math.ceil(duration) * 1000);
                                stop();
                            }else{
                                if(s.getType() != SegmentType.SILENCE && s.getFile() != null)
                                    System.out.println("Sound file is null, sleeping instead");
                                System.out.printf("Sleeping for %.0f seconds. %n", duration);
                                if(duration > 0)
                                    Thread.sleep((long) Math.ceil(duration) * 1000);
                            }
                            s.setRunning(false);
                            
                            if(i + 1 < segments.size())
                                duration = segments.get(i + 1).getDuration();
                        }
                        
                        playinegEvent.setRunning(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if(stamp != 0 && playinegEvent != null)
                        playinegEvent.unlockReadLockSegments(stamp);
                    
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
        fullStop();
        play(f, duration);
    }
    
    public double getFileDuration(String filePath){
        return 0.0;
    }
    
}

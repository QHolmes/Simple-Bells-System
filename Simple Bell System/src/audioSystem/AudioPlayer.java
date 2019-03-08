/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioSystem;

import dataStructures.EventSegment;
import dataStructures.RegionDataCore;
import dataStructures.SegmentType;
import dataStructures.schedules.EventScheduled;
import dataStructures.SoundFile;
import exceptions.IncorrectVersionException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private EventScheduled event;
    private boolean playingTrack = false;
    private final Timer timer;
    private TimerTask lastTask;
    private RegionDataCore data;
    
    public static AudioPlayer getPlayer(RegionDataCore data){
        if(player == null)
            player = new AudioPlayer(data);
        
        return player;
    }
    
    private AudioPlayer(RegionDataCore data){
        this.data = data;
       playingTrack = false;
       timer = new Timer();
    }
    
    public void setBellEvent(EventScheduled b) throws InterruptedException{
        //End current task if applicable 
        if(lastTask != null && event != null)
            lastTask.cancel();
        
        event = b;
        
        if(b == null)
            return;
        
        scheduleEvent();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = b.getStartTime();
        long time = now.until(start, ChronoUnit.MILLIS);
        if(time < 0)
            time = 0;
        timer.schedule(lastTask, time);
            
    }
    
    public EventScheduled getBellEvent(){
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
                
                boolean b = false;
                if(event == null)
                    return;
                
                EventScheduled playingEvent = event;
                
                try {
                    SoundFile file;
                    synchronized(this){
                        if(event.isCancelled())
                            return;
                        
                        while(!b)
                            try{
                                event.setRunning(event.getVersion(), true);
                                b = true;
                            }catch(IncorrectVersionException ex){}
                        
                        ArrayList<Long> segments = event.getSegments();
                        EventSegment s;
                        LocalDateTime start = event.getStartTime();
                        LocalDateTime end = event.getStopTime(data);
                        LocalDateTime now  = LocalDateTime.now();
                        long diff = (start.until(now, ChronoUnit.MILLIS)) / 1000;
                        
                        double duration = 0.0;
                        int index = -1;
                        double length;
                        do{
                            index++;
                            s = data.getSegment(segments.get(index));
                            length = s.getDuration(data);
                            duration = length- diff;
                            diff -= length;
                        }while(diff > 0.0001 && index + 1< segments.size());
                        
                        if(diff > 0.0001 || index >= segments.size()){
                            return;
                        }
                        
                        if(index < 0)
                            index = 0;
                        
                        for(int i = index; i < segments.size() && event != null && !event.isCancelled(); i++){
                            s = data.getSegment(segments.get(i));
                            
                            b = false;
                            while(!b)
                                try{
                                    s.setRunning(s.getVersion(), true);
                                    b = true;
                                }catch(InterruptedException | IncorrectVersionException ex){}
                            
                            file = s.getFile(data);
                            if(s.getType() == SegmentType.SOUND || s.getType() == SegmentType.BELL && file != null){
                                System.out.printf("[%d] Playing %s for %.0f seconds.%n", i, file.getFileName(), duration);
                                play(file, duration);
                                Thread.sleep((long) Math.ceil(duration) * 1000);
                                stop();
                            }else if (s.getType() == SegmentType.PLAYLIST && file != null && s.getPlayListID() != 0){
                                System.out.printf("[%d] Playing %s for %.0f seconds. From playlist %s.%n", 
                                        i, file.getFileName(), duration, data.getPlayList(s.getPlayListID()).toString());
                                play(file, duration);
                                Thread.sleep((long) Math.ceil(duration) * 1000);
                                stop();
                            }else{
                                if(s.getType() != SegmentType.SILENCE && s.getFile(data) != null)
                                    System.out.printf("[%d] Sound file is null, sleeping instead.%n", i, duration);
                                System.out.printf("[%d] Sleeping for %.0f seconds. %n", i, duration);
                                if(duration > 0)
                                    Thread.sleep((long) Math.ceil(duration) * 1000);
                            }
                            
                            b = false;
                            while(!b)
                                try{
                                    s.setRunning(s.getVersion(), false);
                                    b = true;
                                }catch(InterruptedException | IncorrectVersionException ex){}
                            
                            if(i + 1 < segments.size())
                                duration = data.getSegment(segments.get(i + 1)).getDuration(data);
                        }
                        
                        b = false;
                        while(!b)
                            try{
                                playingEvent.setRunning(playingEvent.getVersion(), false);
                                b = true;
                            }catch(IncorrectVersionException ex){}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
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

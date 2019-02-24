/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.Types.EventType;
import dataStructures.templates.EventTemplate;
import helperClasses.Helper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public class EventScheduled extends EventType{
    
    private LocalDateTime startTime;

    /**
     * A bell event is a sequence of files to be played.When the start time is reached
     * it will play or skip the pre bell, then it will play or skip the song, lastly it will 
     * play or skip the post bell.No files are required... however that seems silly
     * @param startTime This is when the event should start. Cannot be null or in the past.
     */
    public EventScheduled(LocalDateTime startTime){
        super();
        if(startTime == null)
            throw new NullPointerException("Start time was not specified");
                    
        this.startTime = startTime;
        segments = new ArrayList();
        
        segmentLock = new StampedLock();
        varLock = new StampedLock();
    }

    public LocalDateTime getStartTime() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime;
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }
    
    public LocalDateTime getStopTime() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime.minusSeconds(- ((long) Math.ceil(getDuration())));
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }
    
    public boolean isCancelled() throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(varLock);
            
            return cancelled;
        }finally{
            if(stamp != 0)
                varLock.unlockWrite(stamp);
        }
    }
    
    public void cancelEvent(boolean b) throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(varLock);
            cancelled = b;
        }finally{
            varLock.unlockWrite(stamp);
        }
    }

    public boolean isRunning() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(varLock);
            
            return running;
        }finally{
            if(stamp != 0)
                varLock.unlockWrite(stamp);
        }
    }

    public void setRunning(boolean running) throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(varLock);
            this.running = running;
        }finally{
            varLock.unlockWrite(stamp);
        }
    }
    
    @Override
    public boolean equals(Object o){
        if( o instanceof EventScheduled)
            return this.startTime.compareTo(((EventScheduled) o).startTime) == 0;
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.startTime);
        return hash;
    }
    
    public int compareTo(EventType o){
        if(!(o instanceof EventScheduled))
            return 0;
        
        try {
            return startTime.compareTo(((EventScheduled)o).getStartTime());
        } catch (InterruptedException ex) {
            return 0;
        }
    }
    
    @Override
    public boolean checkOverlap(EventType e) throws InterruptedException{
        LocalDateTime endTime = getStopTime();
        
        //Ends before start
        if(((EventScheduled) e).getStopTime().compareTo(startTime) <= 0)
            return false;
        
        //Starts after end
        if(((EventScheduled) e).getStartTime().compareTo(endTime) >= 0)
            return false;
        
        return true;         
    }
    
    public boolean setStartTime(LocalDateTime date) throws InterruptedException{
        long stamp = 0;
        
        try{
            if(running)
                return false;
            
            stamp = Helper.getWriteLock(timeLock);
            
            startTime = date;
            return true;
        }finally{
            if(stamp != 0)
                timeLock.unlockWrite(stamp);
        }
    }
    
    public EventTemplate getTemplate() throws InterruptedException{
        EventTemplate ev = new EventTemplate(startTime.toLocalTime());
        
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(segmentLock);
            for(int i = 0; i < segments.size(); i++)
                ev.addSegment(segments.get(i), i);

            return ev;
        }finally{
            if(stamp != 0)
                segmentLock.unlockRead(stamp);
        }
    }
    
}

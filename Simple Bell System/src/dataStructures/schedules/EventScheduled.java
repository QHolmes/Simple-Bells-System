/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.RegionDataCore;
import dataStructures.Types.EventType;
import dataStructures.templates.EventTemplate;
import exceptions.IncorrectVersionException;
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
    private long parentID;

    /**
     * A bell event is a sequence of files to be played.When the start time is reached
     * it will play or skip the pre bell, then it will play or skip the song, lastly it will 
     * play or skip the post bell.No files are required... however that seems silly
     * @param startTime This is when the event should start. Cannot be null or in the past.
     */
    public EventScheduled(long ID, LocalDateTime startTime){
        super(ID);
        if(startTime == null)
            throw new NullPointerException("Start time was not specified");
                    
        this.startTime = startTime;
        segments = new ArrayList();
        
        lock = new StampedLock();
    }

    public LocalDateTime getStartTime() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return startTime;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public LocalDateTime getStopTime(RegionDataCore data) throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return startTime.minusSeconds(- ((long) Math.ceil(getDuration(data))));
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public boolean isCancelled() throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            
            return cancelled;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public void cancelEvent(int version, boolean b) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            cancelled = b;
            this.version++;
        }finally{
            lock.unlockWrite(stamp);
        }
    }

    public boolean isRunning() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            
            return running;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setRunning(int version, boolean running) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.running = running;
            this.version++;
        }finally{
            lock.unlockWrite(stamp);
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
    
    @Override
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
    public boolean checkOverlap(EventType e, RegionDataCore data) throws InterruptedException{
        LocalDateTime endTime = getStopTime(data);
        
        //Ends before start
        if(((EventScheduled) e).getStopTime(data).compareTo(startTime) <= 0)
            return false;
        
        //Starts after end
        if(((EventScheduled) e).getStartTime().compareTo(endTime) >= 0)
            return false;
        
        return true;         
    }
    
    public boolean setStartTime(int version, LocalDateTime date) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        
        try{
            if(running)
                return false;
            
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            startTime = date;
            this.version++;
            return true;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public EventTemplate getTemplate(long newEventID, RegionDataCore data) throws InterruptedException{
        EventTemplate ev;
        long stamp = 0;
        
        while(true)
            try {
                stamp = Helper.getReadLock(lock);
                ev = new EventTemplate(newEventID, startTime.toLocalTime());
                data.addEventTemplate(ev);
                for(int i = 0; i < segments.size(); i++)
                    ev.addSegment(ev.getVersion(), segments.get(i), i);

                return ev;
            } catch (IncorrectVersionException ex) {}finally{
                if(stamp !=0)
                    lock.unlockRead(stamp);
            }
    }
    
    
    
}

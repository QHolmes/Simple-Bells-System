/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.Types.EventType;
import dataStructures.schedules.EventScheduled;
import exceptions.TimeOutOfBounds;
import helperClasses.Helper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public class EventTemplate extends EventType{
    
    private LocalTime startTime;
    private String eventName;

    public EventTemplate(LocalTime startTime) {
        super();
        this.startTime = startTime;
        segments = new ArrayList();
        varLock = new StampedLock();
    }

    public int getHour() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime.getHour();
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }

    public void setHour(int hour) throws TimeOutOfBounds, InterruptedException {
        if(hour > 23 || hour < 0)
            throw new TimeOutOfBounds("Hour needs to be between 0-23");
        
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(timeLock);
            startTime = startTime.minusHours(-hour);
        }finally{
            if(stamp != 0)
                timeLock.unlockWrite(stamp);
        }
        
    }
    
    public int getMinute() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime.getMinute();
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }

    public void setMinute(int minute) throws TimeOutOfBounds, InterruptedException {
        if(minute > 59 || minute < 0)
            throw new TimeOutOfBounds("Minutes needs to be between 0-59");
        
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(timeLock);
            startTime = startTime.minusMinutes(-minute);
        }finally{
            if(stamp != 0)
                timeLock.unlockWrite(stamp);
        }
    }
    
    public int getSecond() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime.getSecond();
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }

    public void setSecond(int second) throws TimeOutOfBounds, InterruptedException {        
         if(second > 59 || second < 0)
            throw new TimeOutOfBounds("Seconds needs to be between 0-59");
        
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(timeLock);
            startTime = startTime.minusSeconds(-second);
        }finally{
            if(stamp != 0)
                timeLock.unlockWrite(stamp);
        }
    }
    
    public LocalTime getStartTime() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime;
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }
    
    public LocalTime getStopTime() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime.minusSeconds(- ((long) Math.ceil(getDuration())));
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }

    public String getEventName() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(varLock);
            return eventName;
        }finally{
            if(stamp != 0)
                varLock.unlockRead(stamp);
        }
    }

    public void setEventName(String eventName) throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(varLock);
            this.eventName = eventName;
        }finally{
            if(stamp != 0)
                varLock.unlockWrite(stamp);
        }
    }
    
    /**
     * Creates a ScheduledEvent for today's date at the given time.
     * @param day Day the event should happen
     * @return 
     */
    public EventScheduled getScheduledEvent(LocalDate day) throws InterruptedException{
        EventScheduled event =  new EventScheduled(startTime.atDate(day));
        
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(segmentLock);
            for(int i = 0; i < segments.size(); i++)
                event.addSegment(segments.get(i), i);

            return event;
        }finally{
            if(stamp != 0)
                segmentLock.unlockRead(stamp);
        }
    }
    
    public int compareTo(EventType e){
        if(!(e instanceof EventTemplate))
            return 0;
        
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(timeLock);
            return startTime.compareTo(((EventTemplate)e).getStartTime());
        } catch (InterruptedException ex) {
            return 0;
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }
    
    @Override
    public boolean checkOverlap(EventType e) throws InterruptedException{
        if(!(e instanceof EventTemplate))
            return false;
        
        LocalTime endTime = getStopTime();
        
        //Ends before start
        if(((EventTemplate) e).getStopTime().compareTo(startTime) <= 0)
            return false;
        
        //Starts after end
        if(((EventTemplate) e).getStartTime().compareTo(endTime) >= 0)
            return false;
        
        return true;           
    }
    
    @Override
    public String toString(){
        return eventName;
    }
    
}

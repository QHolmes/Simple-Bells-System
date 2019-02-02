/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.EventSegment;
import dataStructures.schedules.ScheduledEvent;
import exceptions.StartDateInPast;
import exceptions.TimeOutOfBounds;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class EventTemplate implements Serializable{
    private static final long serialVersionUID = 1;
    
    private int hour;
    private int minute;
    private int second;
    private String eventName;
    private final ArrayList<EventSegment> segments;

    public EventTemplate(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        segments = new ArrayList();
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) throws TimeOutOfBounds {
        if(hour > 23 || hour < 0)
            throw new TimeOutOfBounds("Hour needs to be between 0-23");
        
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) throws TimeOutOfBounds {
        if(minute > 59 || minute < 0)
            throw new TimeOutOfBounds("Minutes needs to be between 0-59");
        
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) throws TimeOutOfBounds {        
         if(second > 59 || second < 0)
            throw new TimeOutOfBounds("Seconds needs to be between 0-59");
        
        this.second = second;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    /**
     * Returns a shallow copy of all the segments.
     * @return list of all segments for this event
     */
    public ArrayList<EventSegment> getSegments(){
        ArrayList<EventSegment> segs = new ArrayList();
        synchronized(segments){segs.addAll(segments);}
        return segs;
    }
    
    /**
     * Tries to remove the given segment from the event. Will return false is the
     * segment could not be removed. 
     * @param seg
     * @return 
     */
    public boolean removeSegment(EventSegment seg){
        synchronized(segments){return segments.remove(seg);}
    }
    
    /**
     * Tries to add the given segment to the list at the given spot.
     * @param seg
     * @param order
     */
    public void addSegment(EventSegment seg, int order){
        synchronized(segments){segments.add(order, seg);}
    }
    
    /**
     * Returns the total length of time, in seconds, the event will take to 
     * complete.
     * @return length of time in seconds
     */
    public double getEventDuration(){
        double length = 0;
        
        length = segments.stream().map((s) -> s.getDuration())
                .reduce(length, (accumulator, _item) -> accumulator + _item);
        
        return length;
    }
    
    /**
     * Creates a ScheduledEvent for today's date at the given time.
     * @param day Day the event should happen
     * @return 
     * @throws exceptions.StartDateInPast 
     */
    public ScheduledEvent createScheduledEvent(Date day) throws StartDateInPast{
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        
        ScheduledEvent event =  new ScheduledEvent(calendar.getTime());
        
        for(int i = 0; i < segments.size(); i++)
            event.addSegment(segments.get(i), i);
        
        return event;
    }
    
    public int compareTo(EventTemplate e){
        return ((hour * 3600) + (minute * 60) + second) - ((e.getHour() * 3600) + (e.getMinute() * 60) +e.getSecond());
    }
    
    public boolean checkOverlap(EventTemplate e){
        double start = (hour * 3600) + (minute * 60) + second;
        double end = start + getEventDuration();
        double time = (e.getHour() * 3600) + (e.getMinute() * 60) +e.getSecond();
        
        return time >= start && time <= end;            
    }
    
}

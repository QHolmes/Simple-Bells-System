/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import dataStructures.templates.EventTemplate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class ScheduledDay implements Serializable{
    private static final long serialVersionUID = 1;
    
    private ArrayList<ScheduledEvent> events = new ArrayList<>();
    private final Date day;
    private String name;
    
    public ScheduledDay(Date day){
        this.day = day;
    }
    
    public void sortEvents(){
        events.sort(new eventComparator());
    }
    
    public ArrayList<ScheduledEvent> getEvents(){
        ArrayList<ScheduledEvent> ev = new ArrayList();
        ev.addAll(events);
        return ev;
    }
    
    public boolean addEvent(ScheduledEvent ev){
        boolean overlap = false;
        
        for(ScheduledEvent e: events){
            overlap = e.checkOverlap(ev);
            if(overlap == true)
                break;
        }
        
        if(!overlap){
            events.add(ev);
            sortEvents();
        }
        
        
        return overlap;
    }
    
    public void removeEvent(ScheduledEvent ev){
        events.remove(ev);
    }
    
    class eventComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if(!(o1 instanceof EventTemplate))
                return 0;
            
            return ((EventTemplate) o1).compareTo((EventTemplate) o2);
        }        
    }

    public Date getDay() {
        return new Date(day.getTime());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the next event that should be played. If there is no more events to 
     * play it will return a null value.
     * @return 
     */
    public ScheduledEvent getNextEvent(){
        ScheduledEvent event = null;
        Date now = new Date();
        
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).getStartTime().after(now)){
                event = events.get(i);
                break;
            }
        }
        
        return event;
    }
    
    
}

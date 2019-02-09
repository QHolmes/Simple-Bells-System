/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.schedules.ScheduledDay;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class DayTemplate implements Serializable{
    private static final long serialVersionUID = 1;
    
    private ArrayList<EventTemplate> events = new ArrayList<>();
    private String name;
    
    public DayTemplate(){
        
    }
    
    public DayTemplate(String name){
        this.name = name;
    }
    
    public void sortEvents(){
        events.sort(new eventComparator());
    }
    
    public ArrayList<EventTemplate> getEvents(){
        ArrayList<EventTemplate> ev = new ArrayList();
        ev.addAll(events);
        return ev;
    }
    
    public boolean addEvent(EventTemplate ev){
        boolean overlap = false;
        
        for(EventTemplate e: events){
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
    
    public void removeEvent(EventTemplate ev){
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns a scheduled day for the given date
     * @param day The day the scheduled event would be used.
     * @return 
     */
    public ScheduledDay createScheduledDay(Date day){
        ScheduledDay scDay = new ScheduledDay(day);
        
        events.forEach( e -> {
                scDay.addEvent(e.createScheduledEvent(day));
        });
        
        scDay.setName(name);
        
        return scDay;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    public void removeFile(SoundFile file){
        events.forEach(s -> s.removeFile(file));
    }
    
    public void removePlayList(PlayList list){
        events.forEach(s -> s.removePlayList(list));
    }
    
    
}

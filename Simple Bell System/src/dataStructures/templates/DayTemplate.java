/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import audioSystem.ScheduledEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Quinten Holmes
 */
public class DayTemplate implements Serializable{
    private static final long serialVersionUID = 1;
    
    private ArrayList<EventTemplate> events = new ArrayList<>();
    private String name;
    
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
            if(overlap = true)
                break;
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
}

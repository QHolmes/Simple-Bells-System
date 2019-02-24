/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.Types.DayType;
import dataStructures.Types.EventType;
import dataStructures.schedules.DayScheduled;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class DayTemplate extends DayType{
    
    
    public DayTemplate(){
        super();
        events = new ArrayList();
        name = "";
    }
    
    public DayTemplate(String name){
        this();
        this.name = name;
    }
    
    /**
     * Returns a scheduled day for the given date
     * @param day The day the scheduled event would be used.
     * @return 
     */
    public DayScheduled getScheduledDay(LocalDate day) throws InterruptedException{
        DayScheduled scDay = new DayScheduled(day);
        
        for(EventType e: events){
            scDay.addEvent(((EventTemplate) e).getScheduledEvent(day));
        }
        
        scDay.setName(name);
        
        return scDay;
    }
    

    @Override
    public boolean addEvent(EventType ev) throws InterruptedException{
        if(!(ev instanceof EventTemplate))
            return false;
        
        boolean valid = true;
        
        for(EventType e: events){
            valid = !e.checkOverlap(ev);
            if(valid == false)
                break;
        }
        
        if(valid){
            EventTemplate temp = (EventTemplate) ev;
            events.add(temp);
            sortEvents();
        }
        
        return valid;
    }
    
}

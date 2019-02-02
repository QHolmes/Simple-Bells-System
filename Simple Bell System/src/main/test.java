/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import dataStructures.BellRegion;
import dataStructures.EventSegment;
import dataStructures.SegmentType;
import dataStructures.schedules.ScheduledDay;
import dataStructures.schedules.ScheduledWeek;
import dataStructures.SoundFile;
import dataStructures.schedules.ScheduledEvent;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import dataStructures.templates.WeekTemplate;
import exceptions.StartDateInPast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public class test {
    
    private SoundFile song;
    private final String FILEPATH = "C:\\Users\\Belva\\OneDrive\\Music\\MC Hammer\\Greatest Hits\\01 U Can't Touch This.m4a";
    private int ADDEDSEC = 5;
    
    public test(){
        
        //testTemplateBuild();
        testBellRegion();
    }
    
    private void testBellRegion(){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 2);

            song = new SoundFile(FILEPATH, "Test Song");
            EventSegment seg1 = new EventSegment(SegmentType.SOUND, song, 10);
            EventSegment seg2 = new EventSegment(SegmentType.SILENCE, null, 3);
            ScheduledEvent event = new ScheduledEvent(calendar.getTime());
            event.addSegment(seg1, 0);
            event.addSegment(seg2, 1);
            event.addSegment(seg1.clone(), 2);
            
            ScheduledDay day = new ScheduledDay(calendar.getTime());
            day.addEvent(event);
            
            ScheduledWeek week = new ScheduledWeek(calendar.getTime());
            week.setDay(day, calendar.get(Calendar.DAY_OF_WEEK) - 1);
            
            BellRegion bell = new BellRegion();
            bell.addScheduledWeek(week);
            bell.initialize();            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void testTemplateBuild(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        
        ArrayList<EventTemplate> events = new ArrayList();
        ArrayList<DayTemplate> days = new ArrayList();
        
        int hour = 0;
        int minute = 0;
        int second = 0;
        
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        System.out.println(format1.format(calendar.getTime()));
        Date startTime = calendar.getTime();
        song = new SoundFile(FILEPATH, "Test Song");
        
        for(int i = 0; i < 10; i++){
            EventSegment seg = new EventSegment(SegmentType.SILENCE, null, 3);
            EventTemplate eventTemp = new EventTemplate(hour, minute++, second);
            eventTemp.addSegment(seg, 0);
            events.add(eventTemp);
        }
        
        for(int i = 0; i < 7; i++){
            DayTemplate day = new DayTemplate("Day-" + i);
            for(EventTemplate e: events)
                day.addEvent(e);
            days.add(day);
        }
        
            WeekTemplate week = new WeekTemplate("Week-" + 0);
            
            for(int j = 0; j < 7; j++)
                week.setDay(days.get(j), j);
            
        
        ArrayList<ScheduledWeek> scWeeks = new ArrayList();
        
        for(int i = 0; i < 10; i++){
            ScheduledWeek scWeek = week.createScheduledWeek(i + 1, 2020);
            scWeek.setWeekName("Week-" + i);
            
            ScheduledDay scDays[] = scWeek.getDays();
            for(int j = 0; j < scDays.length; j++)
                scDays[j].setName("Week-" + i + " Day-"+j);
            
                
            scWeeks.add(scWeek);
        }
        
        scWeeks.forEach( w-> {
            System.out.printf("%s%n", w.getWeekName());
            ScheduledDay scDays[] = w.getDays();
            for (ScheduledDay scDay : scDays) {
                System.out.printf("   %s%n", scDay.getName());
                scDay.getEvents().forEach(e -> System.out.printf("        %s%n", format1.format(e.getStartTime())));
            }
            System.out.println();
        });
    }
}

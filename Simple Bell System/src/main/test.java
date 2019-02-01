/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import audioSystem.AudioPlayer;
import dataStructures.ScheduledDay;
import dataStructures.ScheduledEvent;
import dataStructures.ScheduledWeek;
import dataStructures.SoundFile;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import dataStructures.templates.WeekTemplate;
import exceptions.StartDateInPast;
import exceptions.TimeOutOfBounds;
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
        
        testTemplateBuild();
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
            events.add(new EventTemplate(hour, minute++, second, false, false, true, null, null, song, -1.0));
        }
        
        for(int i = 0; i < 7; i++){
            DayTemplate day = new DayTemplate("Day-" + i);
            events.forEach((e) -> day.addEvent(e));
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

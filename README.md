# Simple-Bells-System

This is a rapid prototype created to test the backend of my senior class project. The system is made for a school environment where one might need to play a tone over the speakers, at scheduled times of the day, to indicate the start or end of a period. This system expands on known systems as it allows the choice of any sound/ music file.


## Sounds

### Bell Sounds

You can add sound files as bell tones. This gives two main advantages. One, the bell tones are separate from music files to make it easier to locate the tone you want. Second, it allows the choice of a default bell sound. This is used as the first bell sound loaded on new events and when the “Ring Bell” button is pressed.

Bell sounds can be added by going to **Sound -> Bell Sounds**. In that menu you may add, remove, select a default bell sound, and rename bell sounds. When adding new sounds, you will select the folder that the bell sounds are in. All sound files in that folder are imported.


### Music 

Music files can be added from **Sound -> Music Files**. You can add, remove, or rename music files on that menu. When adding new sounds, you will select the folder that the music files are in. All sound files in that folder are imported. 


### Playlists

Playlists allow you to have a random song selected when played. To create a playlist, go to **Sound -> Play Lists**. In that menu you can create any number of playlists, rename, and add any music files that have been added to the system. When randomly selecting a song from a playlist, the last 3/4 * n of the music files played will not be up for selection, (where n is the number of music files in the playlist). This is to keep the random feel, where true randomness could repeat songs too often.


## Events

### Creating a New Event

New events can be created on the main screen. On the right-hand side of the screen you can click “New Event” to start creating an event. On the top of the Event pane you can set the time for the event to go off, please note the time is in 24hr format. 

In the event pane you can edit, add, or remove event segments. Event segments make up what happens during the event itself. An event segment can be:

+ **Silence**:   No sound is played for the period of time
+ **Music**:     The selected music file will play for the given time, if you enter “0” the full play time will be filled in.
+ **Play List**: A randomly selected song will play from the contents of the playlist, for the given amount of time.
+ **Bell**:      The selected bell sound will play for the given time, if you enter “0” the full play time will be filled in.

__Please note, if a time is entered that is longer then the play time, there will be silence until the next event segment or the end of the event__

To add a new event segment, press the “+” button at the end of the segment list. To remove a segment, press the “-“ to the left of that segment’s row. 

When the event is to your liking, and at the correct time, press “Add Event” to add it to the schedule. Please note, once an event is added, you can not change the event time.

### Editing an Event

To edit an event you can select the event under the list of events for the day. You will be able to add, edit, or remove any event segments. All changes will be automatically saved. Please note, you will not be able to change the time of an event later.

### Removing an Event

To remove an event, select the event you wish to remove from the list of events for the day. Once selected and visible in the event pane, click “Delete Event”.

### Saving an Event

If a certain ordering of event segments is used often you can save an event to load later. To save an event, first create the event as you would like it, the time the event is scheduled does not matter. Once all the event segments you wish are added, press “Save Event”. This will open a pop-up with a text box. Rename the event if you desire then click “Save”.

### Load a Saved Event

To load an event you have already saved, press “Load Event”. Once pressed, a menu will open, with a drop-down menu, allowing you to select the saved event you wish to load. Once the event you want to load is selected click “Select” and the event will open in the event pane. Now that the event is in the pane, you can set the time and click “Add Event” to add the event to the day.
### Editing Saved Events

You can not directly edit saved events. The easiest approach is to load the desired event, make changes and re-save the event.


## Days


A day can hold any number of events for a certain date.


### Switching Days

To change your view of which day you are looking at you can go to **Day -> Next Day** or **Day -> Prev** to go to the next or previous day respectably. You can also use the keyboard short-cut that is listed next to each menu option.

### Saving a Day

Much like how you can save event that are used often, you can save days. To save a day go to **Day -> Save Day**. Once pressed, a text box will appear allowing you to rename the day, if desired, and then click “Save”.

### Loading a Day

To load a day, go to **Day -> Load Day**. When clicked, a menu will open with a drop down of all available, saved days. Select the day you wish to load and press “Select”. Unlike events, once selected a Day will automatically be active once loaded.


## Weeks


Weeks are what you would expect. Spanning Sunday to Saturday, a week holds 7 days. They are the largest unit in the system. You can only view 3 weeks into the future.

### Switching Weeks

To navigate between weeks, you can go to **Week -> Next Week* or **Week -> Prev Week** to go to the next or previous week respectably. You can also use the keyboard short-cut that is listed next to each menu option.

### Saving a Week

Much like how you can save days or events that are used often, you can save weeks. To save a week go to **Week -> Save Week**. Once pressed, a text box will appear allowing you to rename the week, if desired, and then click “Save”.

### Loading a Day

To load a week, go to **Week -> Load Week**. When clicked, a menu will open with a drop down of all available, saved weeks. Select the week you wish to load and press “Select”. Unlike events, once selected a week will automatically be active once loaded.

### Setting a Default Week

A default week will automatically load when a new week is generated. It is also the template used to reset weeks (more here after). To select a default week, navigate to **Week -> Load Week**, select the desired saved week in the combo-box, as you would when loading. Once selected, you will want to press “Set Default”. Once selected a pop-up will confirm your choice, you may press “Close” to close it, and the “X” button to close the Load Week menu.

### Resetting a Week

In the Week menu there is an option to reset the week (**Week -> Reset Week**). When pressed the current week will be reset back to the default week. If no default week has been selected, it will load an empty week.

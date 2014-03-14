package me.magnet.magneto.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.magnet.magneto.ChatRoom;
import me.magnet.magneto.annotations.Param;
import me.magnet.magneto.annotations.RespondTo;
import org.jivesoftware.smack.Chat;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class CleoRemindMe implements MagnetoPlugin {
    ArrayList<String> thelist = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    enum TimeUnits{
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
        YEARS
    }

    public CleoRemindMe() throws IOException {
    }

    /**
     * Remind in a specified ammount of time.
     */
    @RespondTo(regex = "\\b(remind me to {item} in {time}).*")
    public void setNewReminder(final ChatRoom room, @Param("item") String item, @Param("time") String time) throws ParseException {
        DateTime now = parseTime(time);
        log.info("Reminding you at:" +now.toString());

        if(now == null){
            room.sendMessage("Sorry, I don't understand the time units you used.. You said: "+time);
        }else{
            //Now create the time and schedule it
            Timer timer = new Timer();
            final String message = "You asked me to remind you to: " + item;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    room.sendMessage(message);
                }
            }, now.toDate());
            room.sendMessage("Ok, I'll remind you in "+ time);
        }
    }

    /**
     * Expects a string with two elements seperate by a space
     * @param time
     * @return
     */
    private DateTime parseTime(String time) {
        String[] timeParts = time.split(" ");
        int amount = Integer.valueOf(timeParts[0]);
        TimeUnits units=null;

        try {
            units = TimeUnits.valueOf(timeParts[1].toUpperCase());
        } catch (IllegalArgumentException e) {
           log.info("Illegal time unit" + timeParts[1]);
        }
        if(units == null){
            return null;
        }

        DateTime now = new DateTime();
        switch (units){
            case SECONDS:
                now = now.plusSeconds(amount);
                break;
            case MINUTES:
                now = now.plusMinutes(amount);
                break;
            case HOURS:
                now = now.plusHours(amount);
                break;
            case DAYS:
                now = now.plusDays(amount);
                break;
            case WEEKS:
                now = now.plusWeeks(amount);
                break;
            case MONTHS:
                now = now.plusMonths(amount);
                break;
            case YEARS:
                now = now.plusYears(amount);
                break;
        }
        return now;
    }


    @Override
    public String getName() {
        return "Cleo RemindeMe";
    }
}

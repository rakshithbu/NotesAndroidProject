package com.example.mynotes.comparator;

import com.example.mynotes.model.Notes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class NotesDateSorting implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        Notes n1 = (Notes)o1;
        Notes n2 = (Notes)o2;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1,d2;
        try {
             d1 = dateFormat.parse(n1.created_on);
             d2 = dateFormat.parse(n2.created_on);
             if(d1.after(d2))
             {
                 return 1;
             }
             if(d1.before(d2))
             {
                 return -1;
             }
             if(d1.equals(d2))
             {
                 return 0;
             }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

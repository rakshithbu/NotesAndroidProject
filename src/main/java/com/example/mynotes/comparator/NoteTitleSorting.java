package com.example.mynotes.comparator;

import com.example.mynotes.model.Notes;

import java.util.Comparator;

public class NoteTitleSorting implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Notes n1 = (Notes)o1;
        Notes n2 = (Notes)o2;
        return n1.title.compareTo(n2.title);
    }
}

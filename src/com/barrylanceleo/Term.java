package com.barrylanceleo;

import java.util.LinkedList;

public class Term {
    String termString;
    int postingCount;
    LinkedList<Posting> postingList;

    void printTerm() {
        System.out.println("Term: " + termString);
        System.out.println("Number of postings: " + postingCount);
        System.out.println("Posting List : ");
        int j = 1;
        for (Posting p : postingList) {
            System.out.println("\t" + j + ". DocId: " + p.docId);
            System.out.println("\tFrequency: " + p.frequency);
            j++;
        }
    }
}

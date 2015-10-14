package com.barrylanceleo;

import java.util.ArrayList;

public class Term {
    String termString;
    int postingCount;
    ArrayList<Posting> postingList;

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

    String getDocIDString() {
        String output = new String();
        for (int i = 0; i < postingList.size() - 1; i++) {
            output = output + postingList.get(i).docId + " " + postingList.get(i).frequency + ", ";
        }
        output = output + postingList.get(postingList.size() - 1).docId + " "
                + postingList.get(postingList.size() - 1).frequency;

        return output;
    }
}

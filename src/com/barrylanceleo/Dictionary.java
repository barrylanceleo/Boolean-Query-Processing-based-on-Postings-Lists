package com.barrylanceleo;

import java.util.LinkedList;

public class Dictionary {
    int termsCount;
    LinkedList<Term> terms;

    void printDictionary() {
        System.out.println("Number of terms is the Dictionary: " + termsCount);
        int i = 1;
        for (Term term : terms) {
            System.out.println(i + ". Term: " + term.termString);
            System.out.println("Count: " + term.count);
            System.out.println("Posting List : ");
            int j = 1;
            for (Posting p : term.postingList) {
                System.out.println("\t" + j + ". DocId: " + p.docId);
                System.out.println("\tNumber of Occurrences: " + p.numOfOccurrences);
                j++;
            }
            i++;
        }
    }
}

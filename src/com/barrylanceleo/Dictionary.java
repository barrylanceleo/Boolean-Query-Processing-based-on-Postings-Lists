package com.barrylanceleo;

import java.util.LinkedList;

public class Dictionary {
    Dictionary.SortBy sortedBy;
    int termsCount;
    LinkedList<Term> terms;

    public enum SortBy {
        docId(1),
        frequency(2),
        unsorted(-1);
        private int value;

        SortBy(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

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
                System.out.println("\tFrequency: " + p.frequency);
                j++;
            }
            i++;
        }
    }
}

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
        switch (sortedBy) {
            case docId:
                System.out.println("The postings are sorted by DocId.");
                break;
            case frequency:
                System.out.println("The postings are sorted by frequency.");
                break;
            case unsorted:
                System.out.println("The postings are unsorted.");
        }
        int i = 1;
        for (Term term : terms) {
            System.out.println(i + ". Term: " + term.termString);
            System.out.println("Count: " + term.postingCount);
            System.out.println("Posting List : ");
            int j = 1;
            for (Posting p : term.postingList) {
                System.out.println("\t" + j + ". DocId: " + p.docId);
                System.out.println("\tFrequency: " + p.frequency);
                j++;
            }
            i++;

            //for testing, i is the number of terms
//            if(i == 47)
//                break;

        }
    }

    Dictionary getTopK(int k) {
        //Construct a mini dictionary containing the topKterms
        Dictionary topKTerms = new Dictionary();
        topKTerms.sortedBy = SortBy.unsorted;
        //topKTerms.termsCount = 0;
        topKTerms.terms = new LinkedList<>();
        if (topKTerms.terms.size() == 0)
            topKTerms.terms.add(terms.get(0));
        for (int i = 1; i < terms.size(); i++) {
            //ignore the element if topkterms is filled
            // and the posting count is smaller than all terms in topKterms
            if (topKTerms.terms.size() >= k && topKTerms.terms.get(k - 1).postingCount >= terms.get(i).postingCount) {
                System.out.println("Ignoring terms " + i);
                break;
            }
            for (int j = 0; j < topKTerms.terms.size(); j++) {
                if (terms.get(i).postingCount > topKTerms.terms.get(j).postingCount) {
                    topKTerms.terms.add(j, terms.get(i));
                    if (topKTerms.terms.size() > k) {
                        topKTerms.terms.remove(k - 1);
                    }
                    break;
                }
            }
        }
        topKTerms.termsCount = topKTerms.terms.size();
        return topKTerms;
    }
    
}

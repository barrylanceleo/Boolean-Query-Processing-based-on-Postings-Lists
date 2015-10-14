package com.barrylanceleo;

import java.util.ArrayList;

public class Dictionary {
    Dictionary.SortBy sortedBy;
    int termsCount;
    ArrayList<Term> terms;

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
            System.out.println("Number of postings: " + term.postingCount);
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

    void printTerms() {
        for (int i = 0; i < terms.size() - 1; i++) {
            System.out.print(i + 1 + ". " + terms.get(i).termString + ", ");
        }
        System.out.println(terms.size() + ". " + terms.get(terms.size() - 1).termString);
    }

    String getTermsString() {
        StringBuilder termsString = new StringBuilder();
        for (int i = 0; i < terms.size() - 1; i++) {
            termsString.append(terms.get(i).termString);
            termsString.append(" " + terms.get(i).postingCount);
            termsString.append(" ,");
        }
        termsString.append(terms.get(terms.size() - 1).termString);
        termsString.append(" " + terms.get(terms.size() - 1).postingCount);
        return termsString.toString();
    }

    Dictionary getTopK(int k) {
        //Construct a mini dictionary containing the topKterms
        Dictionary topKTerms = new Dictionary();
        topKTerms.sortedBy = SortBy.unsorted;

        //topKTerms.termsCount = 0;
        topKTerms.terms = new ArrayList<>();

        long timeBefore = System.currentTimeMillis();
        for (int i = 0; i < terms.size(); i++) {
            //ignore the element if topkterms is filled
            // and the posting count is smaller than all terms in topKterms
            if (topKTerms.terms.size() == k && topKTerms.terms.get(k - 1).postingCount >= terms.get(i).postingCount) {
                //System.out.println("Ignoring term " + i);
                continue;
            }
            int j;
            for (j = 0; j < topKTerms.terms.size(); j++) {
                if (terms.get(i).postingCount > topKTerms.terms.get(j).postingCount) {
                    topKTerms.terms.add(j, terms.get(i));
                    if (topKTerms.terms.size() > k) {
                        topKTerms.terms.remove(k);
                    }
                    break;
                }
            }
            //if the term was not inserted and topKTerms has space insert it at the end
            if (j == topKTerms.terms.size() && j < k)
                topKTerms.terms.add(j, terms.get(i));
        }
        topKTerms.termsCount = topKTerms.terms.size();
        long timeAfter = System.currentTimeMillis();
        System.out.println("Time Taken: " + (timeAfter - timeBefore));
        return topKTerms;
    }


    Term getPostingList(String inputString) {
        for (Term term : terms) {
            if (term.termString.equals(inputString))
                return term;
        }
        return null;
    }
}

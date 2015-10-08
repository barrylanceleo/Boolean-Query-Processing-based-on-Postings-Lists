package com.barrylanceleo;


public class Main {

    public static void main(String[] args) {

        IndexBuilder indexBuilder = new IndexBuilder();
        //Dictionary dictionarySortedByDocId = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.docId);
        Dictionary dictionarySortedByFreq = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.frequency);
        //dictionarySortedByFreq.printDictionary();

        //get a mini dictionary of the top k terms
        //Dictionary topKTerms = dictionarySortedByDocId.getTopK(10);
        //topKTerms.printDictionary();

        //getPosting for a given term
        Term requestedTerm = dictionarySortedByFreq.getPostingList("-year");
        if (requestedTerm != null) {
            requestedTerm.printTerm();
        } else {
            System.out.print("term not found");
        }

    }
}

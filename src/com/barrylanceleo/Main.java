package com.barrylanceleo;


public class Main {

    public static void main(String[] args) {

        IndexBuilder indexBuilder = new IndexBuilder();
        Dictionary dictionarySortedByDocId = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.docId);
        Dictionary dictionarySortedByFreq = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.frequency);
        dictionarySortedByFreq.printDictionary();
    }
}

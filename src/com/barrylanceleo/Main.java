package com.barrylanceleo;

public class Main {

    public static void main(String[] args) {

        IndexBuilder indexBuilder = new IndexBuilder();
        Dictionary dictionary = indexBuilder.BuildIndex("term.idx");
        dictionary.printDictionary();
    }
}

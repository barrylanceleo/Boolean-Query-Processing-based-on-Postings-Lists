package com.barrylanceleo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Main {

    static Dictionary dictionarySortedByDocId;
    static Dictionary dictionarySortedByFreq;
    static LogWriter logWriter = new LogWriter();

    static int getTopKTerms(String commands[]) {
        if (commands.length <= 1) {
            System.out.println("Unsupported command. Enter \"help\" for help.");
            return -1;
        }
        //get the value for K
        int k;
        try {
            k = Integer.parseInt(commands[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("Unsupported command. Enter \"help\" for help.");
            return -1;
        }

        //get a mini dictionary of the top k terms
        Dictionary topKTerms = dictionarySortedByDocId.getTopK(k);
        topKTerms.printTerms();

        //Build the log output
        String output = "FUNCTION: ";
        output += commands[0] + " " + commands[1] + "\n";
        output += "Result: ";
        output += topKTerms.getTermsString();
        output += "\n";
        //write output to logfile
        logWriter.writeLog(output);

        System.out.println(output);
        return 0;
    }

    static int getPostings(String commands[]) {
        if (commands.length <= 1) {
            System.out.println("Unsupported command. Enter \"help\" for help.");
            return -1;
        }

        //iterate through the given terms starting with 1 as the first command will be getPostings
        for (int i = 1; i < commands.length; i++) {
            //getPosting for the current term
            Term termWithPostingsSortedByDocId = dictionarySortedByDocId.getPostingList(commands[i]);
            Term termWithPostingsSortedByFreq = dictionarySortedByFreq.getPostingList(commands[i]);

            //build the output
            String output = "FUNCTION: " + commands[0] + " " + commands[i] + "\n";
            if (termWithPostingsSortedByDocId == null) {
                output += "term not found\n";
            } else {
                output += "Ordered by doc IDs:" + termWithPostingsSortedByDocId.getDocIDString() + "\n";
                output += "Ordered by TF:" + termWithPostingsSortedByFreq.getDocIDString() + "\n";
            }

            //write output to logfile
            logWriter.writeLog(output);

            System.out.print(output);

        }
        return 0;
    }

    static int termAtATimeQueryAnd(String commands[]) {
        if (commands.length <= 1) {
            System.out.println("Unsupported command. Enter \"help\" for help.");
            return -1;
        }

        //note the time
        long timeBefore = System.currentTimeMillis();

        //Build Output
        String output = "FUNCTION: ";
        for (int i = 0; i < commands.length - 1; i++) {
            output += commands[i] + " ";
        }
        output += commands[commands.length - 1] + "\n";

        //get the postingsList for the terms
        int numberOfTerms = commands.length - 1;
        Term searchTerms[] = new Term[numberOfTerms];
        for (int i = 0; i < numberOfTerms; i++) {
            searchTerms[i] = dictionarySortedByFreq.getPostingList(commands[i + 1]);
            if (searchTerms[i] == null) {
                output += "terms not found\n";
                logWriter.writeLog(output);
                System.out.println(output);
                return 0;
            }
        }

        ArrayList<Posting> foundDocuments = new ArrayList<>();

        //add all the documents of first term to the foundDocuments
        for (Posting p : searchTerms[0].postingList) {
            foundDocuments.add(p);
        }

        int comparisonsCount = 0;
        //compare with the next term and remove the documents which are not present in both
        //iterate over all search terms
        for (int i = 1; i < numberOfTerms; i++) {
            //iterate over the found documents
            for (int j = 0; j < foundDocuments.size(); j++) {
                //iterate over the documents of next term
                int k;
                for (k = 0; k < searchTerms[i].postingList.size(); k++) {
                    comparisonsCount++;
                    if (foundDocuments.get(j).docId == searchTerms[i].postingList.get(k).docId) {
                        comparisonsCount++;
                        break;
                    }
                }
                //if docId not found remove it from foundDocuments
                if (k == searchTerms[i].postingList.size()) {
                    foundDocuments.remove(j);
                    j--; //since a term is removed, the later elements move to the left
                    // so we need to minus one to compensate for that
                }
            }
        }

        //sort the found docs in descending order
        Collections.sort(foundDocuments, new Comparator<Posting>() {
            @Override
            public int compare(Posting o1, Posting o2) {
                return Integer.compare(o2.docId, o1.docId);
            }
        });

        //note the time
        long timeAfter = System.currentTimeMillis();

        //add the documents count to the output
        output += foundDocuments.size() + " documents are found\n";

        //add the comparisons count to the output
        output += comparisonsCount + " comparisons are made\n";

        //add the time taken to the output
        output += ((timeAfter - timeBefore) / 1000) + "." + ((timeAfter - timeBefore) % 1000) + "seconds are used\n";


//        //add the foundDocuments to the output
//        output += "Result: ";
//        if (foundDocuments.size() == 0) {
//            output += "no matching doc ids\n";
//            logWriter.writeLog(output);
//            System.out.print(output);
//            return 0;
//        }
//        for (int i = 0; i < foundDocuments.size() - 1; i++) {
//            output += foundDocuments.get(i).docId + " " + foundDocuments.get(i).frequency + ", ";
//        }
//        output = output + foundDocuments.get(foundDocuments.size() - 1).docId + " "
//                + foundDocuments.get(foundDocuments.size() - 1).frequency + "\n";

        //optimization part

        //sort the terms in ascending order by postingCount
        Arrays.sort(searchTerms, new Comparator<Term>() {
            @Override
            public int compare(Term o1, Term o2) {
                return Integer.compare(o1.postingCount, o2.postingCount);
            }
        });

        //after sorting, the same algorithm applies
        foundDocuments = new ArrayList<>();

        //add all the documents of first term to the foundDocuments
        for (Posting p : searchTerms[0].postingList) {
            foundDocuments.add(p);
        }

        comparisonsCount = 0;
        //compare with the next term and remove the documents which are not present in both
        //iterate over all search terms
        for (int i = 1; i < numberOfTerms; i++) {
            //iterate over the found documents
            for (int j = 0; j < foundDocuments.size(); j++) {
                //iterate over the documents of next term
                int k;
                for (k = 0; k < searchTerms[i].postingList.size(); k++) {
                    comparisonsCount++;
                    if (foundDocuments.get(j).docId == searchTerms[i].postingList.get(k).docId) {
                        comparisonsCount++;
                        break;
                    }
                }
                //if docId not found remove it from foundDocuments
                if (k == searchTerms[i].postingList.size()) {
                    foundDocuments.remove(j);
                    j--; //since a term is removed, the later elements move to the left
                    // so we need to minus one to compensate for that
                }
            }
        }

        //add the comparisons count of optimization to the output
        output += comparisonsCount + " comparisons are made with optimization (optional bonus part)\n";

        //end of optimization part

        //sort the found docs in descending order
        Collections.sort(foundDocuments, new Comparator<Posting>() {
            @Override
            public int compare(Posting o1, Posting o2) {
                return Integer.compare(o2.docId, o1.docId);
            }
        });


        //add the foundDocuments to the output
        output += "Result: ";
        if (foundDocuments.size() == 0) {
            output += "no matching doc ids\n";
            logWriter.writeLog(output);
            System.out.print(output);
            return 0;
        }
        for (int i = 0; i < foundDocuments.size() - 1; i++) {
            output += foundDocuments.get(i).docId + ", ";
        }
        output = output + foundDocuments.get(foundDocuments.size() - 1).docId + "\n";


        //write output to logfile
        logWriter.writeLog(output);

        System.out.println(output);

        return 0;
    }

    static int termAtATimeQueryOr(String commands[]) {
        if (commands.length <= 1) {
            System.out.println("Unsupported command. Enter \"help\" for help.");
            return -1;
        }

        //note the time
        long timeBefore = System.currentTimeMillis();

        //Build Output
        String output = "FUNCTION: ";
        for (int i = 0; i < commands.length - 1; i++) {
            output += commands[i] + " ";
        }
        output += commands[commands.length - 1] + "\n";

        //get the postingsList for the terms
        int numberOfTerms = commands.length - 1;
        ArrayList<Term> searchTermsFoundInDictionary = new ArrayList<>();

        for (int i = 0; i < numberOfTerms; i++) {
            Term foundTerm = dictionarySortedByFreq.getPostingList(commands[i + 1]);
            if (foundTerm != null) {
                searchTermsFoundInDictionary.add(foundTerm);
            }
        }
        if (searchTermsFoundInDictionary.size() == 0) {
            output += "terms not found\n";
            logWriter.writeLog(output);
            System.out.println(output);
            return 0;
        }

        int numberOfTermsFound = searchTermsFoundInDictionary.size();

        ArrayList<Posting> foundDocuments = new ArrayList<>();

        int comparisonsCount = 0;
        //compare with the next term and add the documents which are not already present
        //iterate over all search terms
        for (int i = 0; i < numberOfTermsFound; i++) {
            //iterate over postings in each term
            ArrayList<Posting> newDocumentsForCurrentTerm = new ArrayList<>();
            for (int j = 0; j < searchTermsFoundInDictionary.get(i).postingList.size(); j++) {
                int k;
                //iterate over the found documents
                for (k = 0; k < foundDocuments.size(); k++) {
                    comparisonsCount++;
                    if (searchTermsFoundInDictionary.get(i).postingList.get(j).docId == foundDocuments.get(k).docId) {
                        break;
                    }
                }

                // if the doc id is not present in foundList add it to the newDocumentsForCurrentTerm list
                if (k == foundDocuments.size()) {
                    newDocumentsForCurrentTerm.add(searchTermsFoundInDictionary.get(i).postingList.get(j));
                    //foundDocuments.add(searchTermsFoundInDictionary[i].postingList.get(j));
                }
            }
            //add all terms in newDocumentsForCurrentTerm list to the found documents list
            for (Posting p : newDocumentsForCurrentTerm) {
                foundDocuments.add(p);
            }
        }

        //sort the found docs in descending order
        Collections.sort(foundDocuments, new Comparator<Posting>() {
            @Override
            public int compare(Posting o1, Posting o2) {
                return Integer.compare(o2.docId, o1.docId);
            }
        });

        //note the time
        long timeAfter = System.currentTimeMillis();

        //add the documents count to the output
        output += foundDocuments.size() + " documents are found\n";

        //add the comparisons count to the output
        output += comparisonsCount + " comparisons are made\n";

        //add the time taken to the output
        output += ((timeAfter - timeBefore) / 1000) + "." + ((timeAfter - timeBefore) % 1000) + "seconds are used\n";

//        //add the foundDocuments to the output
//        output += "Result: ";
//        if (foundDocuments.size() == 0) {
//            output += "no matching doc ids\n";
//            logWriter.writeLog(output);
//            System.out.print(output);
//            return 0;
//        }
//        for (int i = 0; i < foundDocuments.size() - 1; i++) {
//            output += foundDocuments.get(i).docId + " " + foundDocuments.get(i).frequency + ", ";
//        }
//        output = output + foundDocuments.get(foundDocuments.size() - 1).docId + " "
//                + foundDocuments.get(foundDocuments.size() - 1).frequency + "\n";


        //optimization part

        //sort the terms in descending order by postingCount
        Collections.sort(searchTermsFoundInDictionary, new Comparator<Term>() {
            @Override
            public int compare(Term o1, Term o2) {
                return Integer.compare(o2.postingCount, o1.postingCount);
            }
        });

        //after sorting, the same algorithm applies
        foundDocuments = new ArrayList<>();

        comparisonsCount = 0;
        //compare with the next term and add the documents which are not already present
        //iterate over all search terms
        for (int i = 0; i < numberOfTermsFound; i++) {
            //iterate over postings in each term
            ArrayList<Posting> newDocumentsForCurrentTerm = new ArrayList<>();
            for (int j = 0; j < searchTermsFoundInDictionary.get(i).postingList.size(); j++) {
                int k;
                //iterate over the found documents
                for (k = 0; k < foundDocuments.size(); k++) {
                    comparisonsCount++;
                    if (searchTermsFoundInDictionary.get(i).postingList.get(j).docId == foundDocuments.get(k).docId) {
                        break;
                    }
                }

                // if the doc id is not present in foundList add it to the newDocumentsForCurrentTerm list
                if (k == foundDocuments.size()) {
                    newDocumentsForCurrentTerm.add(searchTermsFoundInDictionary.get(i).postingList.get(j));
                    //foundDocuments.add(searchTermsFoundInDictionary[i].postingList.get(j));
                }
            }
            //add all terms in newDocumentsForCurrentTerm list to the found documents list
            for (Posting p : newDocumentsForCurrentTerm) {
                foundDocuments.add(p);
            }
        }

        //add the comparisons count of optimization to the output
        output += comparisonsCount + " comparisons are made with optimization (optional bonus part)\n";

        //end of optimization part

        //sort the found docs in descending order
        Collections.sort(foundDocuments, new Comparator<Posting>() {
            @Override
            public int compare(Posting o1, Posting o2) {
                return Integer.compare(o2.docId, o1.docId);
            }
        });

        //add the foundDocuments to the output
        output += "Result: ";
        if (foundDocuments.size() == 0) {
            output += "no matching doc ids\n";
            logWriter.writeLog(output);
            System.out.print(output);
            return 0;
        }
        for (int i = 0; i < foundDocuments.size() - 1; i++) {
            output += foundDocuments.get(i).docId + " " + foundDocuments.get(i).frequency + ", ";
        }
        output = output + foundDocuments.get(foundDocuments.size() - 1).docId + " "
                + foundDocuments.get(foundDocuments.size() - 1).frequency + "\n";


        //write output to logfile
        logWriter.writeLog(output);

        System.out.println(output);

        return 0;
    }

    static int docAtATimeQueryAnd(String commands[]) {
        if (commands.length <= 1) {
            System.out.println("Unsupported command. Enter \"help\" for help.");
            return -1;
        }

        //note the time
        long timeBefore = System.currentTimeMillis();

        //Build Output
        String output = "FUNCTION: ";
        for (int i = 0; i < commands.length - 1; i++) {
            output += commands[i] + " ";
        }
        output += commands[commands.length - 1] + "\n";

        //get the postingsList for the terms
        int numberOfTerms = commands.length - 1;
        Term searchTerms[] = new Term[numberOfTerms];
        for (int i = 0; i < numberOfTerms; i++) {
            searchTerms[i] = dictionarySortedByDocId.getPostingList(commands[i + 1]);
            if (searchTerms[i] == null) {
                output += "terms not found\n";
                logWriter.writeLog(output);
                System.out.println(output);
                return 0;
            }
        }

        ArrayList<Integer> foundDocuments = new ArrayList<>();

        int comparisonsCount = 0;
        //initialize pointers to point to the beginning of all terms
        int docPointers[] = new int[numberOfTerms];
        for (int i = 0; i < numberOfTerms; i++) {

            docPointers[i] = 0;
        }

        //iterate through all the docs in first term
        findDocs:
        for (int i = 0; i < searchTerms[0].postingList.size(); i++) {
            int searchedDocId = searchTerms[0].postingList.get(i).docId;
            //iterate through all the searchTerms
            int j;
            for (j = 1; j < numberOfTerms; j++) {
                comparisonsCount++;
                //while searchedDocId is lesser than the docIds of the current term so go past them
                while (searchedDocId > searchTerms[j].postingList.get(docPointers[j]).docId) {
                    comparisonsCount++;

                    docPointers[j]++;

                    if (docPointers[j] == searchTerms[j].postingList.size()) {
                        //reached the end of a term, so there can't be an AND match anymore
                        break findDocs;
                    }
                }
                //if current Term contains the DocId go to the next term
                if (searchedDocId == searchTerms[j].postingList.get(docPointers[j]).docId) {

                    docPointers[j]++;
                    continue;
                }//if docId is not present in the current term, go to the next docId
                else {
                    break;
                }
            }
            //if docId was present in all terms add it to the found list
            if (j == numberOfTerms) {
                foundDocuments.add(searchedDocId);
            }

            //break if any of the terms have reached the end of their posting list
            for (int k = 1; k < numberOfTerms; k++) {
                if (docPointers[k] >= searchTerms[k].postingList.size())
                    break findDocs;
            }
        }

        //note the time
        long timeAfter = System.currentTimeMillis();

        //add the documents count to the output
        output += foundDocuments.size() + " documents are found\n";

        //add the comparisons count to the output
        output += comparisonsCount + " comparisons are made\n";

        //add the time taken to the output
        output += ((timeAfter - timeBefore) / 1000) + "." + ((timeAfter - timeBefore) % 1000) + "seconds are used\n";

        //need to perform the optimization here

        //add the foundDocuments to the output
        output += "Result: ";
        if (foundDocuments.size() == 0) {
            output += "no matching doc ids\n";
            logWriter.writeLog(output);
            System.out.print(output);
            return 0;
        }
        for (int i = 0; i < foundDocuments.size() - 1; i++) {
            output += foundDocuments.get(i) + ", ";
        }
        output = output + foundDocuments.get(foundDocuments.size() - 1) + "\n";

        //write output to logfile
        logWriter.writeLog(output);

        System.out.println(output);

        return 0;
    }

    static int docAtATimeQueryOr(String commands[]) {
        if (commands.length <= 1) {
            System.out.println("Unsupported command. Enter \"help\" for help.");
            return -1;
        }

        //note the time
        long timeBefore = System.currentTimeMillis();

        //Build Output
        String output = "FUNCTION: ";
        for (int i = 0; i < commands.length - 1; i++) {
            output += commands[i] + " ";
        }
        output += commands[commands.length - 1] + "\n";

        //get the postingsList for the terms
        int numberOfTerms = commands.length - 1;
        ArrayList<Term> searchTermsFoundInDictionary = new ArrayList<>();

        for (int i = 0; i < numberOfTerms; i++) {
            Term foundTerm = dictionarySortedByDocId.getPostingList(commands[i + 1]);
            if (foundTerm != null) {
                searchTermsFoundInDictionary.add(foundTerm);
            }
        }
        if (searchTermsFoundInDictionary.size() == 0) {
            output += "terms not found\n";
            logWriter.writeLog(output);
            System.out.println(output);
            return 0;
        }

        int numberOfTermsFound = searchTermsFoundInDictionary.size();

        ArrayList<Integer> foundDocuments = new ArrayList<>();

        int comparisonsCount = 0;
        //initialize pointers to point to the beginning of all found terms
        ArrayList<Integer> docPointers = new ArrayList<>();
        for (int i = 0; i < numberOfTermsFound; i++) {
            docPointers.add(0);
        }

        //
        while (searchTermsFoundInDictionary.size() != 0) {
            int smallestDocId = searchTermsFoundInDictionary.get(0).postingList.get(docPointers.get(0)).docId;
            ArrayList<Integer> smallestIndex = new ArrayList<>();
            smallestIndex.add(0);
            //iterate through all the terms

            //find the smallest docIds among all the pointers
            for (int i = 1; i < searchTermsFoundInDictionary.size(); i++) {
                comparisonsCount++;
                if (searchTermsFoundInDictionary.get(i).postingList.get(docPointers.get(i)).docId
                        < smallestDocId) {
                    //if a smaller term is found destroy the old smallestIndex list and add this to a new one
                    smallestDocId = searchTermsFoundInDictionary.get(i).postingList.get(docPointers.get(i)).docId;
                    smallestIndex = new ArrayList<>();
                    smallestIndex.add(i);
                } else {
                    // if an equal element is found add it to the smallestIndex list
                    //comparisonsCount++;
                    if (searchTermsFoundInDictionary.get(i).postingList.get(docPointers.get(i)).docId
                            == smallestDocId) {
                        //smallestDocId = searchTermsFoundInDictionary.get(i).postingList.get(docPointers.get(i)).docId;
                        smallestIndex.add(i);
                    }
                }
            }
            //add the smallest docId to the found docs list
            foundDocuments.add(smallestDocId);

            //increment the pointers having the smallest docs and remove the terms if their docIds are exhausted
            //iterating in the reverse order so that the removal of items do not mess up the order.
            for (int j = smallestIndex.size() - 1; j >= 0; j--) {
                docPointers.set(smallestIndex.get(j), docPointers.get(smallestIndex.get(j)) + 1);
                if (docPointers.get(smallestIndex.get(j)) ==
                        searchTermsFoundInDictionary.get(smallestIndex.get(j)).postingList.size()) {
                    searchTermsFoundInDictionary.remove(smallestIndex.get(j).intValue());
                    docPointers.remove(smallestIndex.get(j).intValue());
                }
            }
        }


        //note the time
        long timeAfter = System.currentTimeMillis();

        //add the documents count to the output
        output += foundDocuments.size() + " documents are found\n";

        //add the comparisons count to the output
        output += comparisonsCount + " comparisons are made\n";

        //add the time taken to the output
        output += ((timeAfter - timeBefore) / 1000) + "." + ((timeAfter - timeBefore) % 1000) + "seconds are used\n";

        //need to perform the optimization here

        //add the foundDocuments to the output
        output += "Result: ";
        if (foundDocuments.size() == 0) {
            output += "no matching doc ids\n";
            logWriter.writeLog(output);
            System.out.print(output);
            return 0;
        }
        for (int i = 0; i < foundDocuments.size() - 1; i++) {
            output += foundDocuments.get(i) + ", ";
        }
        output = output + foundDocuments.get(foundDocuments.size() - 1) + "\n";

        //write output to logfile
        logWriter.writeLog(output);

        System.out.println(output);

        return 0;
    }


    public static void main(String[] args) {

        //Build the dictionaries
        System.out.print("Building the dictionaries...");
        IndexBuilder indexBuilder = new IndexBuilder();
        long timeBefore = System.currentTimeMillis();
        dictionarySortedByDocId = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.docId);
        dictionarySortedByFreq = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.frequency);
        long timeAfter = System.currentTimeMillis();

        //dictionarySortedByFreq.printDictionary();
        System.out.println("\nDone building the dictionaries. in " + (timeAfter - timeBefore) / 1000
                + "." + (timeAfter - timeBefore) % 1000 + "seconds.");

        //read and handle the input
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String inputCommand;
            System.out.println("Enter your command.");
            while (true) {
                System.out.print("$");
                inputCommand = br.readLine();
                String commands[] = inputCommand.split(" ");
                //System.out.println("Command length: " + commands.length);
                if (commands.length < 1)
                    continue;
                switch (commands[0]) {
                    case "help":
                        System.out.println("getTopK K: This returns the key dictionary terms that have " +
                                "the K largest postings lists.");
                        break;

                    case "getTopK":
                        getTopKTerms(commands);
                        break;

                    case "getPostings":
                        getPostings(commands);
                        break;

                    case "termAtATimeQueryAnd":
                        termAtATimeQueryAnd(commands);
                        break;

                    case "termAtATimeQueryOr":
                        termAtATimeQueryOr(commands);
                        break;

                    case "docAtATimeQueryAnd":
                        docAtATimeQueryAnd(commands);
                        break;

                    case "docAtATimeQueryOr":
                        docAtATimeQueryOr(commands);
                        break;

                    case "exit":
                        System.exit(0);

                    default:
                        System.out.println("Unsupported command. Enter \"help\" for help.");

                }
//                for(String command : commands)
//                {
//                    System.out.println("--"+command+"--");
//                }
            }

        } catch (IOException io) {
            System.out.println("Bye!");
        }


    }
}

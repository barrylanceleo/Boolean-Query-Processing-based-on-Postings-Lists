package com.barrylanceleo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

        //compare with the next term and removed the documents which are not present in both
        //iterate over all search terms
        for (int i = 1; i < numberOfTerms; i++) {
            //iterate over the found documents
            for (int j = 0; j < foundDocuments.size(); j++) {
                //iterate over the documents of next term
                int k;
                for (k = 0; k < searchTerms[i].postingList.size(); k++) {
                    if (foundDocuments.get(j).docId == searchTerms[i].postingList.get(k).docId) {
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

    public static void main(String[] args) {

        //Build the dictionaries
        System.out.print("Building the dictionaries...");
        IndexBuilder indexBuilder = new IndexBuilder();
        long timeBefore = System.currentTimeMillis();
        dictionarySortedByDocId = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.docId);
        dictionarySortedByFreq = indexBuilder.BuildIndex("term.idx", Dictionary.SortBy.frequency);
        long timeAfter = System.currentTimeMillis();

        //dictionarySortedByFreq.printDictionary();
        System.out.println("\nDone building the dictionaries. in " + +(timeAfter - timeBefore));

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


                    default:
                        System.out.println("Unsupported command. Enter \"help\" for help.");

                }
//                for(String command : commands)
//                {
//                    System.out.println("--"+command+"--");
//                }
            }

        } catch (IOException io) {
            io.printStackTrace();
        }


    }
}

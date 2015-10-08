package com.barrylanceleo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class IndexBuilder {

    Dictionary BuildIndex(String fileName, Dictionary.SortBy sortBy) {

        // This will reference one line at a time
        String line;
        int linenumber = 0;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            //Initialize the Dictionary Object
            Dictionary dictionary = new Dictionary();
            dictionary.termsCount = 0;
            dictionary.terms = new LinkedList<>();
            //the dictionary will be sorted by sortBy value
            dictionary.sortedBy = sortBy;

            //Read the Posting File and process it
            while ((line = bufferedReader.readLine()) != null) {
                linenumber++;

                //adding new term to dictionary
                dictionary.termsCount++;

                String tokensInTerm[] = line.split("\\\\");
                if (tokensInTerm.length != 3) {
                    System.out.println("Error: Term " + tokensInTerm[0] + " in the file contains more than 3 parts separated by '\\\\'");
                    return null;
                }

                //Construct the term
                Term term = new Term();
                term.termString = tokensInTerm[0];

                //remove the 'c' from the count and update term.count
                StringBuilder count = new StringBuilder(tokensInTerm[1]);
                if (count.charAt(0) == 'c') {
                    count.deleteCharAt(0);
                } else {
                    System.out.println("Error: Term \"" + tokensInTerm[0] + "\" in the file has count in an invalid format.");
                    return null;
                }
                term.count = Integer.valueOf(count.toString());

                //Update term.postingList

                term.postingList = new LinkedList<>();

                StringBuilder postingList = new StringBuilder(tokensInTerm[2]);
                if (postingList.charAt(0) == 'm' && postingList.charAt(1) == '['
                        && postingList.charAt(postingList.length() - 1) == ']') {
                    postingList.deleteCharAt(0);
                    postingList.deleteCharAt(0);
                    postingList.deleteCharAt(postingList.length() - 1);
                } else {
                    System.out.println("Error: Term \"" + tokensInTerm[0] + "\" in the file has postingList in an invalid format.");
                    return null;
                }

                String postings[] = postingList.toString().split(", ");

                if (postings.length == term.count) {
                    for (String postingString : postings) {
                        String dataInPosting[] = postingString.split("/");
                        if (dataInPosting.length == 2) {
                            Posting posting = new Posting();
                            posting.docId = Integer.valueOf(dataInPosting[0]);
                            posting.frequency = Integer.valueOf(dataInPosting[1]);

                            //insert the posting in order sorted by 'sortBy' value
                            int i;
                            if (sortBy == Dictionary.SortBy.docId) {
                                for (i = 0; i < term.postingList.size(); i++) {
                                    if (term.postingList.get(i).docId > posting.docId) {
                                        term.postingList.add(i, posting);
                                        break;
                                    }
                                }
                                if (i == term.postingList.size()) {
                                    term.postingList.add(i, posting);
                                }
                            } else if (sortBy == Dictionary.SortBy.frequency) {
                                for (i = 0; i < term.postingList.size(); i++) {
                                    if (term.postingList.get(i).frequency > posting.frequency) {
                                        term.postingList.add(i, posting);
                                        break;
                                    }
                                }
                                if (i == term.postingList.size()) {
                                    term.postingList.add(i, posting);
                                }
                            } else if (sortBy == Dictionary.SortBy.unsorted) {
                                term.postingList.add(posting);
                            }
                        } else {
                            System.out.println("Error: Term \"" + tokensInTerm[0] + "\" in the file has a posting in an " +
                                    "invalid format.\nPosting: " + postingString);
                            return null;
                        }
                    }
                } else {
                    System.out.println("Error: Term \"" + tokensInTerm[0] + "\" in the file has a mismatch in the number" +
                            " of postings and the count provided.\nCount provided: " + term.count + "\nNumber of postings: "
                            + postings.length);
                    return null;
                }

                //add the term to the dictionary
                dictionary.terms.add(term);

//                //Print term for testing
//                System.out.println("Term: "+term.termString);
//                System.out.println("Count: "+term.count);
//                System.out.println("Posting List : ");
//                for(Posting p : term.postingList)
//                {
//                    System.out.println("\tDocId: "+p.docId);
//                    System.out.println("\tNumber of Occurrences: "+p.frequency);
//                }
//                System.out.println("PostingList: "+tokensInTerm[2]);

                dictionary.termsCount = linenumber;

                // to break the code after reading one line for testing
//                if(linenumber == 47)
//                    break;
            }


            // Always close files.
            bufferedReader.close();

            return dictionary;
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Exception while reading line: " + linenumber + " from file.");
        }
        return null;
    }
}



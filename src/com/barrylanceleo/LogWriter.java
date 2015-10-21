package com.barrylanceleo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by BarryLance on 10/13/2015.
 */
public class LogWriter {
    String logFileLocation;

    LogWriter() {
        logFileLocation = "output.log";
        //logFileLocation = "D:\\Dropbox\\Projects\\Boolean Query Processing\\files\\output.log";

        //delete and create a new file if the log file is already existing
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(logFileLocation, false));
            pw.close();
        } catch (IOException ioe) {
            System.out.println("Error initializing log file.\n");
            ioe.printStackTrace();
        }

    }

    LogWriter(String fileLocation) {
        logFileLocation = fileLocation;

        //delete and create a new file if the log file is already existing
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(logFileLocation, false));
            pw.close();
        } catch (IOException ioe) {
            System.out.println("Error initializing log file.\n");
            ioe.printStackTrace();
        }
    }

    int writeLog(String logText) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(logFileLocation, true));
            pw.print(logText);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

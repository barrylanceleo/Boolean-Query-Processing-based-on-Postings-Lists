package com.barrylanceleo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by BarryLance on 10/13/2015.
 */
public class LogWriter {
    String logFileName;
    String logFilePath;

    LogWriter() {
        logFileName = "log.txt";
        logFilePath = "D:\\Dropbox\\Projects\\Boolean Query Processing\\files\\";

        //delete and create a new file if the log file is already existing
        String filename = logFilePath + logFileName;
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename, false));
            pw.close();
        } catch (IOException ioe) {
            System.out.println("Error initializing log file.\n");
            ioe.printStackTrace();
        }

    }

    LogWriter(String fileName, String path) {
        logFileName = fileName;
        logFilePath = path;

        //delete and create a new file if the log file is already existing
        String filename = logFilePath + logFileName;
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename, false));
            pw.close();
        } catch (IOException ioe) {
            System.out.println("Error initializing log file.\n");
            ioe.printStackTrace();
        }
    }

    int writeLog(String logText) {
        String filename = logFilePath + logFileName;
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename, true));
            pw.print(logText);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

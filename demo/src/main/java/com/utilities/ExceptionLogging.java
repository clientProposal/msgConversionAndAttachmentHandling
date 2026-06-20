package com.utilities;

public class ExceptionLogging {

    private static boolean err = false;

    public static void fatalErrorStopProgram(Exception e) {
        System.out.println("FATAL: " + e);
        System.exit(1);
    }

    public static void failureMarkAndReportLater(Exception e) {
        System.out.println("ERROR: " + e);
        err = true;
    }

    public static boolean hasErrors() {
        return err;
    }

    public static void reset() {
        err = false;
    }
}
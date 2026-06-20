package com.demo;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.*;

import com.utilities.ExceptionLogging;
import com.utilities.PathUtility;
import com.pdftron.sdf.*;

public class EmailToPDFConversion {
    public static void convert(PathUtility paths, String inputFilename, String outputFilename) {
        try (PDFDoc pdfdoc = new PDFDoc()) {
            Convert.officeToPdf(pdfdoc, paths.inputPath + inputFilename, null);
            pdfdoc.save(paths.outputPath + outputFilename, SDFDoc.SaveMode.INCREMENTAL, null);
            System.out.println("Done: " + outputFilename);
        } catch (PDFNetException e) {
            ExceptionLogging.failureMarkAndReportLater(e);
            System.out.println("Not done: " + outputFilename);
            System.out.println(e);
        }
    }
}
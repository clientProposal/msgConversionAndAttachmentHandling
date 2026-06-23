package com.demo;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.HTML2PDF;
import com.pdftron.pdf.PDFNet;

import com.utilities.PathUtility;
import com.utilities.TokenLoader;
import com.utilities.ExceptionLogging;

public class App {
    static PathUtility paths = new PathUtility();

    public static void main(String[] args) {
        PDFNet.initialize(TokenLoader.token);
        PDFNet.setResourcesPath(paths.resourcePath);
        try {
            HTML2PDF.setModulePath(paths.libPath);
            PDFNet.addResourceSearchPath(paths.libPath);
            if (!HTML2PDF.isModuleAvailable()) {
                ExceptionLogging.fatalErrorStopProgram(new Exception("The HTML2PDF module is not available."));
            }
        } catch (PDFNetException e) {
            ExceptionLogging.fatalErrorStopProgram(e);
        } catch (Exception e) {
            ExceptionLogging.fatalErrorStopProgram(e);
        }

        String inputMsg = "msg_with_attachment.eml";
        String outputPdf = "msg_with_attachment.pdf";
        EmailToPDFConversion.convert(paths, inputMsg, outputPdf);

        ExtractAttachments.extract(paths, outputPdf);

        ConvertAttachmentAndReattach.process(paths, outputPdf);

        PDFNet.terminate();
    }
}
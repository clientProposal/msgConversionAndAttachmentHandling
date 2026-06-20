package com.demo;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.sdf.NameTree;
import com.pdftron.sdf.NameTreeIterator;
import com.pdftron.sdf.Obj;

import com.utilities.ExceptionLogging;
import com.utilities.PathUtility;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExtractAttachments {

    public static void extract(PathUtility paths, String pdfFilename) {
        try (PDFDoc doc = new PDFDoc(paths.outputPath + pdfFilename)) {
            doc.initSecurityHandler();

            NameTree embeddedFiles = NameTree.find(doc.getSDFDoc(), "EmbeddedFiles");
            if (!embeddedFiles.isValid()) {
                System.out.println("No embedded files found in: " + pdfFilename);
                return;
            }

            NameTreeIterator iter = embeddedFiles.getIterator();
            int count = 0;

            while (iter.hasNext()) {
                String entryName = iter.key().getAsPDFText();
                Obj fileSpec = iter.value();

                String filename = getFilename(fileSpec, entryName);

                Obj ef = fileSpec.findObj("EF");
                if (ef == null) {
                    System.out.println("Skipping (no EF dict): " + filename);
                    iter.next();
                    continue;
                }

                Obj fileStream = ef.findObj("F");
                if (fileStream == null) {
                    System.out.println("Skipping (no stream): " + filename);
                    iter.next();
                    continue;
                }

                com.pdftron.filters.Filter filter = fileStream.getDecodedStream();
                com.pdftron.filters.FilterReader reader = new com.pdftron.filters.FilterReader(filter);

                byte[] buf = new byte[4096];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                long bytesRead;
                while ((bytesRead = reader.read(buf)) > 0) {
                    baos.write(buf, 0, (int) bytesRead);
                }
                byte[] data = baos.toByteArray();

                String safeName = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
                String outputFile = paths.outputPath + safeName;

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(data);
                }

                System.out.println("Extracted: " + safeName + " (" + data.length + " bytes)");
                count++;
                iter.next();
            }

            System.out.println("Total attachments extracted: " + count);

        } catch (PDFNetException e) {
            ExceptionLogging.failureMarkAndReportLater(e);
        } catch (IOException e) {
            ExceptionLogging.failureMarkAndReportLater(e);
        }
    }

    private static String getFilename(Obj fileSpec, String fallback) throws PDFNetException {
        Obj uf = fileSpec.findObj("UF");
        if (uf != null && uf.isString()) {
            return uf.getAsPDFText();
        }

        Obj f = fileSpec.findObj("F");
        if (f != null && f.isString()) {
            return f.getAsPDFText();
        }

        return fallback;
    }
}
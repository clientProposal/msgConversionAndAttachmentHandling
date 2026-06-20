package com.demo;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.sdf.NameTree;
import com.pdftron.sdf.NameTreeIterator;
import com.pdftron.sdf.Obj;
import com.pdftron.sdf.SDFDoc;

import com.utilities.ExceptionLogging;
import com.utilities.PathUtility;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ConvertAttachmentAndReattach {

    public static void process(PathUtility paths, String pdfFilename) {
        try (PDFDoc doc = new PDFDoc(paths.outputPath + pdfFilename)) {
            doc.initSecurityHandler();

            NameTree embeddedFiles = NameTree.find(doc.getSDFDoc(), "EmbeddedFiles");
            if (!embeddedFiles.isValid()) {
                System.out.println("No embedded files to convert in: " + pdfFilename);
                return;
            }

            java.util.List<String[]> entries = new java.util.ArrayList<>();
            NameTreeIterator iter = embeddedFiles.getIterator();
            while (iter.hasNext()) {
                String key = iter.key().getAsPDFText();
                String filename = getFilename(iter.value(), key);
                entries.add(new String[]{key, filename});
                iter.next();
            }

            for (String[] entry : entries) {
                embeddedFiles.erase(entry[0].getBytes(StandardCharsets.UTF_8));
            }

            int converted = 0;
            for (String[] entry : entries) {
                String key = entry[0];
                String filename = entry[1];
                String safeName = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
                File extractedFile = new File(paths.outputPath + safeName);

                if (!extractedFile.exists()) {
                    System.out.println("Extracted file not found, skipping: " + safeName);
                    continue;
                }

                String lowerName = safeName.toLowerCase();
                String pdfAttachmentName = getBaseName(safeName) + ".pdf";
                String convertedPath = paths.outputPath + pdfAttachmentName;

                if (lowerName.endsWith(".pdf")) {
                    convertedPath = extractedFile.getAbsolutePath();
                    pdfAttachmentName = safeName;
                } else {
                    try (PDFDoc convertedDoc = new PDFDoc()) {
                        Convert.toPdf(convertedDoc, extractedFile.getAbsolutePath());
                        convertedDoc.save(convertedPath, SDFDoc.SaveMode.LINEARIZED, null);
                        System.out.println("Converted: " + safeName + " -> " + pdfAttachmentName);
                    } catch (PDFNetException e) {
                        System.out.println("Failed to convert: " + safeName + " (" + e.getMessage() + ")");
                        continue;
                    }
                }

                reattach(doc, embeddedFiles, pdfAttachmentName, convertedPath);
                converted++;
            }

            doc.save(paths.outputPath + pdfFilename, SDFDoc.SaveMode.LINEARIZED, null);
            System.out.println("Reattached " + converted + " converted attachments to: " + pdfFilename);

        } catch (PDFNetException e) {
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

    private static void reattach(PDFDoc doc, NameTree embeddedFiles, String name, String filePath)
            throws PDFNetException {
        com.pdftron.pdf.FileSpec fs = com.pdftron.pdf.FileSpec.create(doc, filePath, true);
        embeddedFiles.put(name.getBytes(StandardCharsets.UTF_8), fs.getSDFObj());
    }

    private static String getBaseName(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(0, lastDot);
        }
        return filename;
    }
}
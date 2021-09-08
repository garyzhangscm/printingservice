/**
 * Copyright 2019
 *
 * @author gzhang
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.garyzhangscm.printingservice;

import org.apache.logging.log4j.util.Strings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrintingService {

    @Value("${fileupload.temp-file.directory:/upload/tmp/}")
    String destinationFolder;

    public void printingPDFFile(File file, int copies) throws IOException, PrinterException {
        System.out.println(LocalDateTime.now() + ": Will print from default printer");
        printingPDFFile(file, PrinterJob.getPrinterJob(), copies);
    }

    public void printingPDFFile(File file, PrinterJob job, int copies) throws IOException, PrinterException {

        PDDocument document  = PDDocument.load(file);
        System.out.println(LocalDateTime.now() + ": file name printed");

        System.out.println(LocalDateTime.now() + "： got default printer");
        job.setPageable(new PDFPageable(document));
        System.out.println(LocalDateTime.now() + "： print " + copies + " copies");
        job.setCopies(copies);
        job.print();

        // close the document after printing
        document.close();
    }

    public void printingPDFFile(File file, String printerName, int copies) throws IOException, PrinterException {

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : printServices) {
            System.out.println(LocalDateTime.now()+
                    ": compare existing printer " + printer.getName() +
                    " with required printer " + printerName +
                    ": match? " + printer.getName().equalsIgnoreCase(printerName));
            if(printer.getName().equalsIgnoreCase(printerName)) {

                System.out.println(LocalDateTime.now()+ ": Found the printer!!! Will print from " + printerName);
                printingPDFFile(file, printer, copies);
                return;

            }
        }


        System.out.println(LocalDateTime.now() + ": Cannot find any printer with name " + printerName);

    }

    public List<String> getPrinters() {

        List<String> printers = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {

            System.out.println(LocalDateTime.now()+ ": Printer in system " + printer.getName());
            printers.add(printer.getName());
        }
        return printers;

    }
    public void printingPDFFile(File file, PrintService printer, int copies)
            throws IOException, PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(printer);
        PDDocument document  = PDDocument.load(file);

        job.setPageable(new PDFPageable(document));
        System.out.println(LocalDateTime.now() + "： print " + copies + " copies");
        job.setCopies(copies);
        job.print();
        document.close();
    }


    public void testPrintPDF(String printer) throws IOException, PrinterException {
        String filename = destinationFolder + "test.pdf";
        System.out.println(LocalDateTime.now() + ": Start to test printing file " + filename);
        File file = new File(filename);
        System.out.println(LocalDateTime.now()+ ": File loaded");
        if (Strings.isBlank(printer)) {
            System.out.println(LocalDateTime.now() + ": Will print from the default printer");
            printingPDFFile(file, 1);
        }
        else {
            System.out.println(LocalDateTime.now() + ": Will print from the printer: " + printer );
            printingPDFFile(file, printer, 1);
        }

    }

    public void printZebraLabel(String zplCommand, int copies) throws PrintException {
        System.out.println(LocalDateTime.now() + ": Will print from default printer");
        printZebraLabel(zplCommand, PrintServiceLookup.lookupDefaultPrintService(), copies);
    }



    public void printZebraLabel(String zplCommand, String printerName, int copies) throws IOException, PrinterException, PrintException {

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : printServices) {
            System.out.println(LocalDateTime.now()+
                    ": compare existing printer " + printer.getName() +
                    " with required printer " + printerName +
                    ": match? " + printer.getName().equalsIgnoreCase(printerName));
            if(printer.getName().equalsIgnoreCase(printerName)) {

                System.out.println(LocalDateTime.now()+ ": Found the printer!!! Will print from " + printerName);
                printZebraLabel(zplCommand, printer, copies);
                return;

            }
        }


        System.out.println(LocalDateTime.now() + ": Cannot find any printer with name " + printerName);

    }

    public void printZebraLabel(String zplCommand, PrintService printService, int copies)
            throws PrintException {

        DocPrintJob job = printService.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(zplCommand.getBytes(), flavor, null);
        job.print(doc, null);
    }

}

/**
 * Copyright 2018
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PrintingController {
    private static final Logger logger
            = LoggerFactory.getLogger(PrintingController.class);


    @Autowired
    FileService fileService;
    @Autowired
    PrintingService printingService;



    @RequestMapping(value="/", method = RequestMethod.GET)
    @RestResource
    public String welcome() {
        System.out.println("welcome to printing service");
        return "welcome";
    }


    @RequestMapping(value="/printing/test", method = RequestMethod.GET)
    @RestResource
    public String testPrinting(@RequestParam(name = "printer", required = false, defaultValue = "") String printer)
            throws PrintException, IOException, URISyntaxException, PrinterException {
        System.out.println(LocalDateTime.now() + "Start to test print");
        printingService.testPrintPDF(printer);
        return "print success";
    }


    @RequestMapping(value="/printers", method = RequestMethod.GET)
    @RestResource
    public List<String> getPrinters()
            throws PrintException, IOException, URISyntaxException, PrinterException {
        return printingService.getPrinters();

    }


    @RequestMapping(value="/printing/pdf", method = RequestMethod.POST)
    @RestResource
    public String printingPDF(@RequestParam("file") MultipartFile file,
                              @RequestParam(name = "printer", required = false, defaultValue = "") String printer,
                              @RequestParam(name = "copies", required = false, defaultValue = "1") int copies) throws IOException, PrinterException {
        System.out.println(LocalDateTime.now() + "Start to print uploaded pdf file from printer " + printer + ", copies: " + copies);

        File localFile = fileService.saveFile(file);
        if (Strings.isBlank(printer)) {
            printingService.printingPDFFile(localFile, copies);

        }
        else  {
            printingService.printingPDFFile(localFile, printer, copies);
        }


        return "print success";
    }


    @RequestMapping(value="/printing/label", method = RequestMethod.POST)
    @RestResource
    public String printLabel(@RequestParam("file") MultipartFile file,
                              @RequestParam(name = "printer", required = false, defaultValue = "") String printer,
                              @RequestParam(name = "copies", required = false, defaultValue = "1") int copies) throws IOException, PrinterException, PrintException {
        System.out.println(LocalDateTime.now() + "Start to print uploaded pdf file from printer " + printer + ", copies: " + copies);

        String labelContent = new String(file.getBytes(), StandardCharsets.UTF_8);

        System.out.println("Start to print label with content");
        System.out.println(labelContent);

        if (Strings.isBlank(printer)) {
            printingService.printZebraLabel(labelContent, copies);

        }
        else  {
            printingService.printZebraLabel(labelContent, printer, copies);
        }


        return "print success";
    }


}

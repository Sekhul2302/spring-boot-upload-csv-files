package com.bezkoder.spring.files.csv.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.bezkoder.spring.files.csv.model.DataKyc;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;

import com.bezkoder.spring.files.csv.model.Tutorial;

public class CSVHelper {
  public static String TYPE = "text/csv";
  static String[] HEADERs = { "Id", "Title", "Description", "Published" };

  public static boolean hasCSVFormat(MultipartFile file) {

    if (!TYPE.equals(file.getContentType())) {
      return false;
    }

    return true;
  }

  public static List<Tutorial> csvToTutorials(InputStream is) {
    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        CSVParser csvParser = new CSVParser(fileReader,
            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

      List<Tutorial> tutorials = new ArrayList<Tutorial>();

      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {
        Tutorial tutorial = new Tutorial(
              Long.parseLong(csvRecord.get("Id")),
              csvRecord.get("Title"),
              csvRecord.get("Description"),
              Boolean.parseBoolean(csvRecord.get("Published"))
            );

        tutorials.add(tutorial);
      }

      return tutorials;
    } catch (IOException e) {
      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream tutorialsToCSV(List<Tutorial> tutorials) {
    final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
      for (Tutorial tutorial : tutorials) {
        List<String> data = Arrays.asList(
              String.valueOf(tutorial.getId()),
              tutorial.getTitle(),
              tutorial.getDescription(),
              String.valueOf(tutorial.isPublished())
            );

        csvPrinter.printRecord(data);
      }

      csvPrinter.flush();
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
    }
  }

  public static List<DataKyc> mappingCsv(InputStream is) {
    CSVFormat fmt = CSVFormat.EXCEL.withDelimiter('|');
    try (

            BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader,
                    fmt.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

      List<DataKyc> tutorials = new ArrayList<DataKyc>();

      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {
        String sDate1=csvRecord.get("TGL_TRX");
        String dateInString = "19590709";
        LocalDate date = LocalDate.parse(sDate1, DateTimeFormatter.BASIC_ISO_DATE);
        Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(date));
        DataKyc dataKyc = new DataKyc(
                date1,
                csvRecord.get("NO_KONTRAK"),
                csvRecord.get("NO_KTP"),
                csvRecord.get("CIF"),
                Integer.parseInt(csvRecord.get("CLIENT_ID")),
                Integer.parseInt(csvRecord.get("KODE_PRODUK")),
                csvRecord.get("FILE_PATH_KTP"),
                csvRecord.get("FILE_PATH_SELFIE")
        );

        tutorials.add(dataKyc);
      }

      return tutorials;
    } catch (IOException | ParseException e) {
      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
    }
  }

}

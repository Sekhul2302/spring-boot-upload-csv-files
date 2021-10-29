package com.bezkoder.spring.files.csv.controller;

import java.util.List;

import com.bezkoder.spring.files.csv.model.DataKyc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bezkoder.spring.files.csv.service.CSVService;
import com.bezkoder.spring.files.csv.helper.CSVHelper;
import com.bezkoder.spring.files.csv.message.ResponseMessage;
import com.bezkoder.spring.files.csv.model.Tutorial;

import javax.validation.Valid;

@CrossOrigin("http://localhost:8081")
@Controller
@RequestMapping("/api/csv")
public class CSVController {

  @Autowired
  CSVService fileService;

  @PostMapping("/upload")
  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
    String message = "";

    if (CSVHelper.hasCSVFormat(file)) {
      try {
        fileService.save(file);

        message = "Uploaded the file successfully: " + file.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
      } catch (Exception e) {
        message = "Could not upload the file: " + file.getOriginalFilename() + "!";
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
      }
    }

    message = "Please upload a csv file!";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
  }

  @GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials() {
    try {
      List<Tutorial> tutorials = fileService.getAllTutorials();

      if (tutorials.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/download")
  public ResponseEntity<Resource> getFile() {
    String filename = "tutorials.csv";
    InputStreamResource file = new InputStreamResource(fileService.load());

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType("application/csv"))
        .body(file);
  }

  @PostMapping("/scheduller")
  public ResponseEntity<ResponseMessage> schedulerInsert() throws Exception {
    String message = "Success insert data csv to db";

    fileService.schedulerInsertKyc();

    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
  }

  @RequestMapping(value = "/getDataKyc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseMessage> getDataKyc(@RequestBody @Valid DataKyc request)throws Exception{
    String message = "success";

    List<DataKyc> kycList = fileService.getListbyKtp(request.getNoKtp());

    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message, kycList));
  }

}

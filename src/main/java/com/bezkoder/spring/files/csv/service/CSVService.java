package com.bezkoder.spring.files.csv.service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bezkoder.spring.files.csv.model.DataKyc;
import com.bezkoder.spring.files.csv.repository.DataKycRepository;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bezkoder.spring.files.csv.helper.CSVHelper;
import com.bezkoder.spring.files.csv.model.Tutorial;
import com.bezkoder.spring.files.csv.repository.TutorialRepository;

@Service
public class CSVService {
  private final static Logger logger = LoggerFactory.getLogger(CSVService.class);

  @Value("${config.minio.host}")
  private String endPoint;

  @Value("${config.minio.accessKey}")
  private String accessKey;

  @Value("${config.minio.secretKey}")
  private String secretKey;

  @Value("${config.minio.bucketName}")
  private String bucketName;

  @Autowired
  TutorialRepository repository;

  @Autowired
  DataKycRepository repositoryDataKyc;

  public void save(MultipartFile file) {
    try {
      List<Tutorial> tutorials = CSVHelper.csvToTutorials(file.getInputStream());
      repository.saveAll(tutorials);
    } catch (IOException e) {
      throw new RuntimeException("fail to store csv data: " + e.getMessage());
    }
  }

  public ByteArrayInputStream load() {
    List<Tutorial> tutorials = repository.findAll();

    ByteArrayInputStream in = CSVHelper.tutorialsToCSV(tutorials);
    return in;
  }

  public List<Tutorial> getAllTutorials() {
    return repository.findAll();
  }

  public List<DataKyc> getListbyKtp(String noKtp) throws Exception {
    logger.info("Start : Services getListbyKtp : " + noKtp);

    List<DataKyc> test = new ArrayList<>();

    test = repositoryDataKyc.findDataByNoKtp(noKtp);

    if(noKtp.equals("")){
      test =  repositoryDataKyc.findAll();
    }

    List<DataKyc> dataKycs = new ArrayList<>();
    int index = 0;
    for (Object a : test){

      String pathMinioSelfie = test.get(index).getTglTransaksi()+"/SELFIE/"+test.get(index).getPathSelvie();
      String pathMinioKtp = test.get(index).getTglTransaksi()+"/KTP/"+test.get(index).getPathKtp();
      String urlFotoSelfie = this.getUrlPhoto(pathMinioSelfie);
      String urlFotoKtp = this.getUrlPhoto(pathMinioKtp);

      DataKyc dataKyc1 = new DataKyc();
      dataKyc1.setTglTransaksi(test.get(index).getTglTransaksi());
      dataKyc1.setNoKontrak(test.get(index).getNoKontrak());
      dataKyc1.setNoKtp(test.get(index).getNoKtp());
      dataKyc1.setCif(test.get(index).getCif());
      dataKyc1.setClientId(test.get(index).getClientId());
      dataKyc1.setKodeProduk(test.get(index).getKodeProduk());
      dataKyc1.setPathKtp(test.get(index).getPathKtp());
      dataKyc1.setPathSelvie(urlFotoSelfie);
      dataKyc1.setPathKtp(urlFotoKtp);
      dataKycs.add(dataKyc1);
      index++;
    }

    return dataKycs;
  }

  public String getUrlPhoto(String pathObject) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, InvalidExpiresRangeException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    MinioClient minioClient = MinioClient.builder().endpoint(endPoint)
            .credentials(accessKey, secretKey).build();

    String url =
            null;
    try {
      url = minioClient.getPresignedObjectUrl(
              GetPresignedObjectUrlArgs.builder()
                      .method(Method.GET)
                      .bucket(bucketName)
                      .object(pathObject)
                      .expiry(2, TimeUnit.HOURS)
                      .build());
    } catch (io.minio.errors.ServerException e) {
      e.printStackTrace();
    }

    return url;
  }

  @Scheduled(cron = "${cron.ussd.posting}")
  public void schedulerInsertKyc() throws InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IOException{
    try {
      logger.info("Scheduler insert data KYC called");

      FileInputStream fis = null;

      MinioClient minioClient = MinioClient.builder().endpoint(endPoint)
              .credentials(accessKey, secretKey).build();

      Date date = Calendar.getInstance().getTime();
      DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
//      String strDate = dateFormat.format(date);
      String strDate = "20211025";
      String getPath = strDate+"/CSV/NASABAHTE_"+strDate+".csv";


      InputStream stream1 = (minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(getPath).build()));

      List<DataKyc> tutorials = CSVHelper.mappingCsv(stream1);

      repositoryDataKyc.saveAll(tutorials);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

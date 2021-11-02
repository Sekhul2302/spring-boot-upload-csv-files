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
import java.time.LocalDate;
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
import io.minio.errors.*;
import io.minio.http.Method;
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

  final int BAREKSA = 5122;

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

  public List<DataKyc> filterDataKyc(DataKyc request)  throws Exception{

    int ClientId = request.getClientId();
    String noKtp = request.getNoKtp();
    String noKontrak = request.getNoKontrak();
    String cif = request.getCif();
    boolean foto = request.isFoto();

    if (!foto){
      logger.info("Find Data kyc range tanggal "+request.getTanggalAwal()+" tanggalAkhir"+request.getTanggalAkhir());
      return repositoryDataKyc.findBytglTransaksiBetween(request.getTanggalAwal(), request.getTanggalAkhir());
    }

    if (ClientId != 0 && noKontrak.equals("") && noKtp.equals("") && cif.equals("")){
      logger.info("Find By channel "+ClientId);
      return repositoryDataKyc.findByClientId(ClientId);
    }

    if (ClientId != 0 && !noKontrak.equals("") && noKtp.equals("") && cif.equals("")){
      logger.info("Find By channel "+ClientId);
      return repositoryDataKyc.findByClientIdAndNoKontrak(ClientId, noKontrak);
    }

    if (ClientId != 0 && !noKtp.equals("")){
      logger.info("Find By channel "+ClientId+ " no ktp " +noKtp);
      return repositoryDataKyc.findByClientIdAndNoKtp(ClientId, noKtp);
    }

    if (ClientId != 0 && !cif.equals("") && noKontrak.equals("") && noKtp.equals("")){
      logger.info("Scheduler insert data KYC called");
      return repositoryDataKyc.findByClientIdAndCif(ClientId, cif);
    }

    if (ClientId !=0 && !noKontrak.equals("") && !noKtp.equals("") && cif.equals("")){
      logger.info("Find By channel "+ClientId+ " no Kontrak " +noKontrak+ " no ktp " +noKtp);
      return repositoryDataKyc.findByClientIdAndNoKontrakAndNoKtp(ClientId, noKontrak, noKtp);
    }

    if (ClientId !=0 && !noKontrak.equals("") && !cif.equals("") && noKtp.equals("")){
      logger.info("Find By channel "+ClientId+ " no Kontrak " +noKontrak+ " cif " +cif);
      return repositoryDataKyc.findByClientIdAndNoKontrakAndNoKtp(ClientId, noKontrak, cif);
    }

    if (ClientId !=0 && !noKtp.equals("") && !cif.equals("") && noKontrak.equals("")){
      logger.info("Find By channel "+ClientId+ " no ktp " +noKtp+ " cif " +cif);
      return repositoryDataKyc.findByClientIdAndNoKtpAndCif(ClientId, noKtp, cif);
    }

    logger.info("Scheduler insert data KYC called");
    return repositoryDataKyc.findByClientId(ClientId);
  }

  public List<DataKyc> getDataKyc(DataKyc request) throws Exception {

    List<DataKyc> result = new ArrayList<>();

    result = this.filterDataKyc(request);

    String urlFotoSelfie="";
    String urlFotoKtp = "";
    boolean foto = request.isFoto();

    List<DataKyc> dataKycs = new ArrayList<>();
    int index = 0;
    for (Object a : result){

      Date tglTransaksi = result.get(index).getTglTransaksi();

      String pathMinioSelfie = tglTransaksi+"/SELFIE/"+result.get(index).getPathSelfie();
      String pathMinioKtp = tglTransaksi+"/KTP/"+result.get(index).getPathKtp();

      if(foto){
        urlFotoSelfie = this.getUrlPhoto(pathMinioSelfie);
        urlFotoKtp = this.getUrlPhoto(pathMinioKtp);
      }

      DataKyc dataKyc1 = new DataKyc();
      dataKyc1.setTglTransaksi(result.get(index).getTglTransaksi());
      dataKyc1.setNoKontrak(result.get(index).getNoKontrak());
      dataKyc1.setNoKtp(result.get(index).getNoKtp());
      dataKyc1.setCif(result.get(index).getCif());
      dataKyc1.setClientId(result.get(index).getClientId());
      dataKyc1.setKodeProduk(result.get(index).getKodeProduk());
      if(foto) {
        dataKyc1.setPathSelfie(urlFotoSelfie);
        dataKyc1.setPathKtp(urlFotoKtp);
      }
      dataKyc1.setFoto(request.isFoto());
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


      DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
//      String strDate = dateFormat.format(date);
      String strDate = "20211031";
      String getPath = strDate+"/CSV/NASABAHTE_"+strDate+".csv";


      InputStream stream1 = (minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(getPath).build()));

      List<DataKyc> tutorials = CSVHelper.mappingCsv(stream1);

      repositoryDataKyc.saveAll(tutorials);
      logger.info("Insert csv success");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

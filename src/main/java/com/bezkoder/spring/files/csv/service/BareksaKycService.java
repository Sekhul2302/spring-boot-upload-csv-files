package com.bezkoder.spring.files.csv.service;

import com.bezkoder.spring.files.csv.dto.RequestKyc;
import com.bezkoder.spring.files.csv.dto.RequestKycChannel;
import com.bezkoder.spring.files.csv.dto.ResponseDataKyc;
import com.bezkoder.spring.files.csv.dto.ResponseNavKyc;
import com.bezkoder.spring.files.csv.message.Response;
import com.bezkoder.spring.files.csv.model.DataKyc;
import com.bezkoder.spring.files.csv.repository.DataKycRepository2;
import com.google.gson.Gson;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BareksaKycService {
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
    DataKycRepository2 dataKycRepository2;

    @Autowired
    private Gson gson;

    public DataKyc filterSearchObject(RequestKyc request){

        int ClientId = request.getChannel();
        String noKtp = request.getNoKtp();
        String noKontrak = request.getNoKontrak();
        String cif = request.getCif();

        if (ClientId != 0 && !noKontrak.equals("") && noKtp.equals("") && cif.equals("")){
            logger.info("Find By channel "+ClientId);
            return dataKycRepository2.findByClientIdAndNoKontrak(ClientId, noKontrak);
        }

        if (ClientId != 0 && !noKtp.equals("")){
            logger.info("Find By channel "+ClientId+ " no ktp " +noKtp);
            return dataKycRepository2.findByClientIdAndNoKtp(ClientId, noKtp);
        }

        if (ClientId != 0 && !cif.equals("") && noKontrak.equals("") && noKtp.equals("")){
            logger.info("Scheduler insert data KYC called");
            return dataKycRepository2.findByClientIdAndCif(ClientId, cif);
        }

        if (ClientId !=0 && !noKontrak.equals("") && !noKtp.equals("") && cif.equals("")){
            logger.info("Find By channel "+ClientId+ " no Kontrak " +noKontrak+ " no ktp " +noKtp);
            return dataKycRepository2.findByClientIdAndNoKontrakAndNoKtp(ClientId, noKontrak, noKtp);
        }

        if (ClientId !=0 && !noKontrak.equals("") && !cif.equals("") && noKtp.equals("")){
            logger.info("Find By channel "+ClientId+ " no Kontrak " +noKontrak+ " cif " +cif);
            return dataKycRepository2.findByClientIdAndNoKontrakAndNoKtp(ClientId, noKontrak, cif);
        }

        if (ClientId !=0 && !noKtp.equals("") && !cif.equals("") && noKontrak.equals("")){
            logger.info("Find By channel "+ClientId+ " no ktp " +noKtp+ " cif " +cif);
            return dataKycRepository2.findByClientIdAndNoKtpAndCif(ClientId, noKtp, cif);
        }

        return dataKycRepository2.findByClientIdAndNoKontrakAndNoKtpAndCif(ClientId, noKontrak, noKtp, cif);
    }

    public List<DataKyc> getDataKycByClient(int clientId){
        logger.info("Find by client id +");
        return dataKycRepository2.findByClientId(clientId);
    }

    public Object responseKyc(RequestKyc request) throws InvalidBucketNameException, InsufficientDataException, ErrorResponseException, InvalidExpiresRangeException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ResponseDataKyc responseDataKyc = new ResponseDataKyc();

        if (request.getChannel() !=0 && request.getNoKtp().equals("") && request.getNoKontrak().equals("") && request.getCif().equals("")){
            List<DataKyc> resp = this.getDataKycByClient(request.getChannel());

            List<ResponseDataKyc>listResponse = new ArrayList<>();

            String urlFotoSelfie="";
            String urlFotoKtp = "";

            int index = 0;
            for (Object a : resp){

                Date tglTransaksi = resp.get(index).getTglTransaksi();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                String strDate = formatter.format(tglTransaksi);

                String pathMinioSelfie = strDate+"/SELFIE/"+resp.get(index).getPathSelfie();
                String pathMinioKtp = strDate+"/KTP/"+resp.get(index).getPathKtp();

                urlFotoSelfie = this.getUrlPhoto(pathMinioSelfie);
                urlFotoKtp = this.getUrlPhoto(pathMinioKtp);

                responseDataKyc.setTglTransaksi(resp.get(index).getTglTransaksi());
                responseDataKyc.setNoKontrak(resp.get(index).getNoKontrak());
                responseDataKyc.setNoKtp(resp.get(index).getNoKtp());
                responseDataKyc.setCif(resp.get(index).getCif());
                responseDataKyc.setClientId(resp.get(index).getClientId());
                responseDataKyc.setKodeProduk(resp.get(index).getKodeProduk());
                responseDataKyc.setPathPhoto(urlFotoKtp);
                responseDataKyc.setPathSelfie(urlFotoSelfie);
                listResponse.add(responseDataKyc);
                index++;
            }
            return listResponse;
        }

        DataKyc data =  this.filterSearchObject(request);
        responseDataKyc.setTglTransaksi(data.getTglTransaksi());
        responseDataKyc.setNoKontrak(data.getNoKontrak());
        responseDataKyc.setNoKtp(data.getNoKtp());
        responseDataKyc.setCif(data.getCif());
        responseDataKyc.setClientId(data.getClientId());
        responseDataKyc.setKodeProduk(data.getKodeProduk());

        return responseDataKyc;
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

    public Object getKycBydate(RequestKycChannel requestKycChannel){
        Object data = new Object();
        List<DataKyc> dataKyc =  dataKycRepository2.findBytglTransaksiBetween(requestKycChannel.getTanggalAwal(), requestKycChannel.getTanggalAkhir());

        List<ResponseNavKyc> responseNavKycs = new ArrayList<>();

        int index = 0;
        for(Object a: dataKyc){
            ResponseNavKyc responseNavKyc = new ResponseNavKyc();
            responseNavKyc.setTglTransaksi(dataKyc.get(index).getTglTransaksi());
            responseNavKyc.setNoKontrak(dataKyc.get(index).getNoKontrak());
            responseNavKyc.setNoKtp(dataKyc.get(index).getNoKtp());
            responseNavKyc.setCif(dataKyc.get(index).getCif());
            responseNavKyc.setClientId(dataKyc.get(index).getClientId());
            responseNavKyc.setKodeProduk(dataKyc.get(index).getKodeProduk());
            responseNavKycs.add(responseNavKyc);
        }

        return responseNavKycs;
    }

}

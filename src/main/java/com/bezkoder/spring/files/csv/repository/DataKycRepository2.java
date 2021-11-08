package com.bezkoder.spring.files.csv.repository;

import com.bezkoder.spring.files.csv.model.DataKyc;
import com.bezkoder.spring.files.csv.dto.ResponseDataKyc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface DataKycRepository2 extends JpaRepository<DataKyc, Long> {

    DataKyc findByClientIdAndNoKtp(int clientId, String noKtp);

    DataKyc findByClientIdAndNoKontrak(int clientId, String noKontrak);

    DataKyc findByClientIdAndCif(int clientId, String cif);

    DataKyc findByClientIdAndNoKontrakAndNoKtp(int clientId, String noKontrak, String noKtp);

    DataKyc findByClientIdAndNoKtpAndCif(int clientId, String noKtp, String cif);

    DataKyc findByClientIdAndNoKontrakAndNoKtpAndCif(int clientId, String noKontrak, String noKtp, String cif);

    List<DataKyc> findByClientId(int clientId);
    List<DataKyc> findBytglTransaksiBetween(Date tanggalAwal, Date tanggalAkhir);
}

package com.bezkoder.spring.files.csv.repository;

import com.bezkoder.spring.files.csv.model.DataKyc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataKycRepository extends JpaRepository<DataKyc, Long> {
    List<DataKyc> findDataByNoKtp(String noKtp);

    List<DataKyc> findAll();

    List<DataKyc> findByClientId(int channel);

    List<DataKyc> findByClientIdAndNoKontrak(int channel, String noKontrak);

    List<DataKyc> findByClientIdAndNoKtp(int channel, String noKtp);

    List<DataKyc> findByClientIdAndCif(int channel, String cif);

    List<DataKyc> findByClientIdAndNoKontrakAndNoKtp(int channel, String noKontrak, String noKtp);

    List<DataKyc> findByClientIdAndNoKtpAndCif(int channel, String noKtp, String cif);

    List<DataKyc> findBytglTransaksiBetween(String tanggalAwal, String tanggalAkhir);
}

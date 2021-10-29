package com.bezkoder.spring.files.csv.repository;

import com.bezkoder.spring.files.csv.model.DataKyc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataKycRepository extends JpaRepository<DataKyc, Long> {
    List<DataKyc> findDataByNoKtp(String noKtp);

    List<DataKyc> findAll();
}

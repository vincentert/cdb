package com.takima.cdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.takima.cdb.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{
}

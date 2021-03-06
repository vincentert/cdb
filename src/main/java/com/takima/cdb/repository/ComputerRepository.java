package com.takima.cdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.takima.cdb.entity.Computer;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long>{
}

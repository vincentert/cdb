package com.takima.cdb.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.takima.cdb.dto.ComputerDTO;
import com.takima.cdb.entity.Company;
import com.takima.cdb.entity.Computer;
import com.takima.cdb.repository.CompanyRepository;
import com.takima.cdb.repository.ComputerRepository;

@RestController
public class ComputerRestController {

	@Autowired
	private ComputerRepository computerRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@GetMapping("/computers")
	public List<Computer> retrieveAllComputers() {
		return computerRepository.findAll();
	}

	@GetMapping("/computer/{id}")
	public Optional<Computer> retrieveComputer(@PathVariable("id") long id) {
		return computerRepository.findById(id);
	}

	@PostMapping(path = "/computer", consumes = "application/json")
	public ResponseEntity<String> addComputer(@RequestBody ComputerDTO computer) {
		
		String computerName = computer.getName();
		Boolean saveComputerDTOProvided = true;
		String erreurMsg = "";
		String newLine = System.getProperty("line.separator");
		
		if(computerName==null || computerName.isBlank()) {
			saveComputerDTOProvided = false;
			erreurMsg = erreurMsg.concat("The field 'name' is mandatory to create add a computer !"+newLine);
		}
		
		Computer computerToSave = new Computer();
		computerToSave.setName(computerName);
		
		Timestamp introduced = computer.getIntroduced();
		Timestamp discontinued = computer.getDiscontinued();
		if(introduced!=null && discontinued!=null && discontinued.after(introduced)) {
			computerToSave.setIntroduced(introduced);
			computerToSave.setDiscontinued(discontinued);
		}else if(introduced!=null || discontinued!=null) {
			saveComputerDTOProvided = false;
			erreurMsg = erreurMsg.concat("The field 'discontinued' has to be greater than the field 'introduced' !"+newLine);
		}
		if(computer.getCompany_id() != 0) {
			Optional<Company> company = companyRepository.findById(computer.getCompany_id());
			if(company.isPresent()) {
				computerToSave.setCompany(company.get());
			}else {
				saveComputerDTOProvided = false;
				erreurMsg = erreurMsg.concat("The field 'id_company' doesn't refer to any company present in the data base !"+newLine);
			}
		}

		if(Boolean.TRUE.equals(saveComputerDTOProvided)) {
			computerRepository.save(computerToSave);
			return new ResponseEntity<>("The computer '"+computerName+"' has been added to the data base! ",HttpStatus.CREATED); 
		}
		return new ResponseEntity<>(erreurMsg,HttpStatus.BAD_REQUEST);
	}

	@DeleteMapping(path = "/computer/{id}")
	public ResponseEntity<String> deleteComputer(@PathVariable long id) {

		if(id>0 && computerRepository.existsById(id)) {
			computerRepository.deleteById(id);
			return new ResponseEntity<>("The computer number "+id+" has been deleted from the data base! ", HttpStatus.OK);  
		}

		return new ResponseEntity<>("The computer number "+id+" doesn't exist", HttpStatus.NOT_FOUND);
	}

	@PutMapping(path = "/computer/{id}", consumes = "application/json")
	public ResponseEntity<String> updateComputer(@PathVariable long id, @RequestBody ComputerDTO computer) {
		Optional<Computer> optionalComputerToUpdate = computerRepository.findById(id);
		if(!optionalComputerToUpdate.isPresent()) {
			return new ResponseEntity<>("None computer correspond to the id '"+id+"'",HttpStatus.NOT_FOUND); 
		}

		Computer computerToUpdate = optionalComputerToUpdate.get();
		Timestamp introduced = computer.getIntroduced();
		Timestamp discontinued = computer.getDiscontinued();
		String name = computer.getName();

		if(introduced!=null && discontinued!=null && discontinued.after(introduced)) {

			computerToUpdate.setIntroduced(introduced);
			computerToUpdate.setDiscontinued(discontinued);

		}else if(introduced!=null || discontinued!=null) {
			return new ResponseEntity<>("The field 'discontinued' has to be greater than the field 'introduced' !",HttpStatus.BAD_REQUEST);
		}

		if(computer.getCompany_id() != 0) {
			Optional<Company> company = companyRepository.findById(computer.getCompany_id());
			if(company.isPresent()) {
				computerToUpdate.setCompany(company.get());
			}else {
				return new ResponseEntity<>("The field 'id_company' doesn't refer to any company present in the data base !", HttpStatus.BAD_REQUEST);
			}
		}
		if(name!=null && !name.isBlank()){
			computerToUpdate.setName(name);
		}

		computerRepository.save(computerToUpdate);
		return new ResponseEntity<>("The computer number '"+id+"' has been updtaded in the data base! ", HttpStatus.OK);
	}
	
	
	@GetMapping(path = "/computers",params = { "page", "size" })
	public List<Computer> findPaginated(@RequestParam("page") int page, 
	  @RequestParam("size") int size) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Computer> resultPage = computerRepository.findAll(pageable);
	    if (page > resultPage.getTotalPages()) {
	    	return new ArrayList<>(); 
	    }	 
	    return resultPage.getContent();
	}
	
}

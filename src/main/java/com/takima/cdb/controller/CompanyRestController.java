package com.takima.cdb.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.takima.cdb.dto.CompanyDTO;
import com.takima.cdb.entity.Company;
import com.takima.cdb.repository.CompanyRepository;

@RestController
public class CompanyRestController {

	@Autowired
	private CompanyRepository companyRepository;

	@GetMapping("/companies")
	public List<Company> retrieveAllCompanies() {
		return companyRepository.findAll();
	}
	
	@GetMapping("/company/{id}")
	public Optional<Company> retrieveCompany(@PathVariable("id") long id) {
		return companyRepository.findById(id);
	}

	@PostMapping(path = "/company", consumes = "application/json")
	public ResponseEntity<String> addCompany(@RequestBody CompanyDTO company) {
		String companyName = company.getName();
		
		if(companyName==null || companyName.isBlank()) {
			return new ResponseEntity<>("The field 'name' is mandatory to create add a company !", HttpStatus.BAD_REQUEST);
		}
		
		Company companyToSave = new Company();
		companyToSave.setName(companyName);
		companyRepository.save(companyToSave);
		
		return new ResponseEntity<>("The company '"+companyName+"' has been added to the data base! ", HttpStatus.CREATED); 
	}
	
	@DeleteMapping(path = "/company/{id}")
	public ResponseEntity<String> deleteCompany(@PathVariable long id) {

		if(id>0 && companyRepository.existsById(id)) {
			companyRepository.deleteById(id);
			return new ResponseEntity<>("The company number "+id+" has been deleted from the data base! ", HttpStatus.OK);  
		}

	    return new ResponseEntity<>("The company number "+id+" doesn't exist", HttpStatus.NOT_FOUND);
	}
	
	@PutMapping(path = "/company/{id}", consumes = "application/json")
	public ResponseEntity<String> updateCompany(@PathVariable long id, @RequestBody CompanyDTO company) {
		
		String companyName = company.getName();
		Optional<Company> optionalCompanyToUpdate = companyRepository.findById(id);
		if(!optionalCompanyToUpdate.isPresent()) {
			return new ResponseEntity<>("The company number "+id+" doesn't exist", HttpStatus.NOT_FOUND);
		}
		
		Company companyToUpdate = optionalCompanyToUpdate.get();
		if(companyName!=null && !companyName.isBlank()) {
			companyToUpdate.setName(companyName);
			companyRepository.save(companyToUpdate);
			return new ResponseEntity<>("The company '"+companyName+"' has been updated to the data base! ", HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Nothing to update", HttpStatus.NO_CONTENT); 
	}
}

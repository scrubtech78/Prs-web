package com.prs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.prs.db.VendorRepo;
import com.prs.model.Vendor;

@CrossOrigin
@RestController
@RequestMapping("/api/vendors")
public class VendorController {
	@Autowired
	private VendorRepo vendorRepo;

	@GetMapping("/")
	public List<Vendor> getAllVendors() {
		return vendorRepo.findAll();
	}

	@GetMapping("{id}")
	public Vendor getVendorById(@PathVariable int id) {
		Optional<Vendor> v = vendorRepo.findById(id);
		if (v.isPresent()) {
			return v.get();
		} else {
			System.err.println("Get Vendor error: id [" + id + "] does not exist.");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor Not Found: id [" + id + "]");
		}

	}

	@PostMapping("")
	public Vendor addVendor(@RequestBody Vendor vendor) {
		return vendorRepo.save(vendor);
	}

	@PutMapping("{id}")
	public Vendor updateVendor(@PathVariable int id, @RequestBody Vendor vendor) {
		Vendor v = null;
		if (id != vendor.getId()) {
			System.err.println("Vendor id does not match path id.");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor Not Found");
			// TODO Return error to front end.
		} else if (!vendorRepo.existsById(id)) {
			System.err.println("Vendor does not exist for id: " + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor Not Found");
			// TODO Return error to front end.
		} else {
			try {
				v = vendorRepo.save(vendor);
			} catch (Exception e) {
				System.err.println(e);
				throw e;
			}
		}
		return v;
	}

	@DeleteMapping("/{id}")
	public Boolean deleteVendor(@PathVariable int id) {
		boolean success = false;
		if (vendorRepo.existsById(id)) {
			vendorRepo.deleteById(id);
			success = true;
		} else {
			System.err.println("Delete Error: No Vendor exists for id:" + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor Not Found");
		}

		return success;
	}
}

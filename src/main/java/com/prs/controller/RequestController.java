package com.prs.controller;

import java.time.LocalDate;
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

import com.prs.db.RequestRepo;
import com.prs.model.Request;

@CrossOrigin
@RestController
@RequestMapping("/api/requests")
public class RequestController {
	@Autowired
	private RequestRepo requestRepo;

	@GetMapping("/")
	public List<Request> getAllRequests() {
		return requestRepo.findAll();

	}

	@GetMapping("{id}")
	public Request getRequestById(@PathVariable int id) {
		Optional<Request> r = requestRepo.findById(id);
		if (r.isPresent()) {
			return r.get();
		} else {
			System.err.println("Get Request error: id [" + id + "] does not exist.");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request Not Found: id [" + id + "]");
		}

	}

	@PostMapping("")
	public Request addRequest(@RequestBody Request request) {
		request.setStatus("NEW");
		request.setSubmittedDate(LocalDate.now());
		request.setTotal(0);
		requestRepo.save(request);
		return request;

	} 

	@PutMapping("/{id}")
	public Request updateRequest(@PathVariable int id, @RequestBody Request request) {
		Request r = null;
		if (id != request.getId()) {
			System.err.println("Request id does not match path id.");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request Not Found");
			// TODO Return error to front end.
		} else if (!requestRepo.existsById(id)) {
			System.err.println("Request does not exist for id: " + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request Not Found");
			// TODO Return error to front end.
		} else {
			try {
				r = requestRepo.save(request);
			} catch (Exception e) {
				System.err.println(e);
				throw e;
			}
		}
		return r;
	}

	@DeleteMapping("/{id}")
	public Boolean deleteRequest(@PathVariable int id) {
		boolean success = false;
		if (requestRepo.existsById(id)) {
			requestRepo.deleteById(id);
			success = true;
		} else {
			System.err.println("Delete Error: No Request exists for id:" + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request Not Found");
		}
		return success;
	}

	@PostMapping("/approve/{id}")
	public Request approveRequest(@PathVariable int id) {
		// Sets the status of the request for the id provided to "APPROVED"
		// find request for id
		Optional<Request> r = requestRepo.findById(id);
		Request request = r.get();

		// set request status to APPROVED
		request.setStatus("APPROVED");

		// save request
		requestRepo.save(request);
		return request;
	}

	@PostMapping("/reject/{id}")
	public Request rejectRequest(@PathVariable int id) {
		Optional<Request> r = requestRepo.findById(id);
		Request request = r.get();

		request.setStatus("REJECTED");

		requestRepo.save(request);
		return request;
	}

	@PostMapping("/review/{id}")
	public Request reviewRequest(@PathVariable int id) {
		Optional<Request> r = requestRepo.findById(id);
		Request request = r.get();
		double totalRequest = request.getTotal();
		if (totalRequest <= 50.00) {
			request.setStatus("APPROVED");

		} else {
			request.setStatus("REVIEW");

		}
		requestRepo.save(request);
		return request;

	}

	@GetMapping("/reviews/{userId}")
	public List<Request> getAllRequestsInReview(@PathVariable int userId) {
		List<Request> requests = requestRepo.findByUserIdNotAndStatus(userId, "REVIEW");
		return requests;
	}

}

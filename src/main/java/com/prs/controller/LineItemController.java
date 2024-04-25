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

import com.prs.db.LineItemRepo;
import com.prs.db.RequestRepo;
import com.prs.model.LineItem;
import com.prs.model.Request;

@CrossOrigin
@RestController
@RequestMapping("/api/lineitems")
public class LineItemController {
	@Autowired
	private LineItemRepo lineitemRepo;
	@Autowired
	private RequestRepo requestRepo;

	@GetMapping("/")
	public List<LineItem> getAllLineItems() {
		return lineitemRepo.findAll();
	}

	@GetMapping("{id}")
	public LineItem getLineItemById(@PathVariable int id) {
		Optional<LineItem> l = lineitemRepo.findById(id);
		if (l.isPresent()) {
			return l.get();
		} else {
			System.err.println("Get Lineitem error: id [" + id + "] does not exist.");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LineItem Not Found: id [" + id + "]");
		}

	}

	@PostMapping("")
	public LineItem addLineItem(@RequestBody LineItem lineitem) {
		lineitemRepo.save(lineitem);
		recalTotal(lineitem.getRequest());
		return lineitem;

	}

	@PutMapping("/{id}")
	public LineItem updateLineItem(@PathVariable int id, @RequestBody LineItem lineitem) {
		LineItem l = null;
		if (id != lineitem.getId()) {
			System.err.println("Lineitem id does not match path id.");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lineitem Not Found");
			// TODO Return error to front end.
		} else if (!lineitemRepo.existsById(id)) {
			System.err.println("Lineitem does not exist for id: " + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lineitem Not Found");
			// TODO Return error to front end.
		} else {
			try {
				l = lineitemRepo.save(lineitem);
			} catch (Exception e) {
				System.err.println(e);
				throw e;
			}
		}
		recalTotal(l.getRequest());
		return l;
	}

	@DeleteMapping("/{id}")
	public Boolean deleteLineItem(@PathVariable int id) {
		Request request = null;
		boolean success = false;
		if (lineitemRepo.existsById(id)) {
			LineItem lineitem= lineitemRepo.findById(id).get();
			request = lineitem.getRequest();
			lineitemRepo.deleteById(id);
			success = true;
		} else {
			System.err.println("Delete Error: No lineitem  exists for id:" + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lineitem Not Found");
		}
		recalTotal(request);
		return success;

	}
	@GetMapping("/by-request/{requestId}")
	public List<LineItem> getLineitemsByRequestId(@PathVariable int requestId){
			List<LineItem> lineitem= lineitemRepo.findByRequestId(requestId);
			return lineitem;
	}
	

	private void recalTotal(Request request) {
		List<LineItem> lineItems = lineitemRepo.findByRequest(request);

		// declare a new sum variable (0)

		double sum = 0;

		// loop through the line Items

		for (LineItem li : lineItems) {
			// for each line item, calculate Lithe lineTotal
			// (product.price * quantity)

			double lineTotal = li.getProduct().getPrice() * li.getQuantity();
			// add the lineTotal to the sum
			sum += lineTotal;

		}
		// set
		request.setTotal(sum);
		requestRepo.save(request);
	}

	// save request
}

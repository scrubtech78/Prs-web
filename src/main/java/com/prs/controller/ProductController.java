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

import com.prs.db.ProductRepo;
import com.prs.model.Product;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductRepo productRepo;

	@GetMapping("/")
	public List<Product> getAllProducts() {
		return productRepo.findAll();
	}

	@GetMapping("{id}")
	public Product getProductById(@PathVariable int id) {
		Optional<Product> p = productRepo.findById(id);
		if (p.isPresent()) {
			return p.get();
		} else {
			System.err.println("Get Product error: id [" + id + "] does not exist.");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found: id [" + id + "]");
		}

	}

	@PostMapping("")
	public Product addProduct(@RequestBody Product product) {
		return productRepo.save(product);

	}

	@PutMapping("{id}")
	public Product updateProduct(@PathVariable int id, @RequestBody Product product) {
		Product p = null;
		if (id != product.getId()) {
			System.err.println("Product id does not match path id.");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product Not Found");
			// TODO Return error to front end.
		} else if (!productRepo.existsById(id)) {
			System.err.println("Product does not exist for id: " + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found");
			// TODO Return error to front end.
		} else {
			try {
				p = productRepo.save(product);
			} catch (Exception e) {
				System.err.println(e);
				throw e;
			}
		}
		return p;
	}

	@DeleteMapping("/{id}")
	public Boolean deleteProduct(@PathVariable int id) {
		boolean success = false;
		if (productRepo.existsById(id)) {
			productRepo.deleteById(id);
			success = true;
		} else {
			System.err.println("Delete Error: No Product exists for id:" + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found");
		}
		return success;

	}
}

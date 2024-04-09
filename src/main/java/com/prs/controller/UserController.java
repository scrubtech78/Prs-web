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

import com.prs.db.UserRepo;
import com.prs.model.User;
import com.prs.model.UserLogin;

@CrossOrigin
@RestController
@RequestMapping("/api/users")

public class UserController {
	@Autowired
	private UserRepo userRepo;

	@GetMapping("/")
	public List<User> getAllusers() {
		return userRepo.findAll();
	}

	@GetMapping("{id}")
	public User getUserById(@PathVariable int id) {
		Optional<User> u = userRepo.findById(id);
		if (u.isPresent()) {
			return u.get();
		} else {
			System.err.println("Get User error: id [" + id + "] does not exist.");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found: id [" + id + "]");
		}

	}

	@PostMapping("")
	public User addUser(@RequestBody User user) {
		return userRepo.save(user);
	}

	@PutMapping("{id}")
	public User updateUser(@PathVariable int id, @RequestBody User user) {
		User u = null;
		if (id != user.getId()) {
			System.err.println("User id does not match path id.");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Not Found");
			// TODO Return error to front end.
		} else if (!userRepo.existsById(id)) {
			System.err.println("User does not exist for id: " + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
			// TODO Return error to front end.
		} else {
			try {
				u = userRepo.save(user);
			} catch (Exception e) {
				System.err.println(e);
				throw e;
			}
		}
		return u;
	}

	@DeleteMapping("/{id}")
	public Boolean deleteUser(@PathVariable int id) {
		boolean success = false;
		if (userRepo.existsById(id)) {
			userRepo.deleteById(id);
			success = true;
		} else {
			System.err.println("Delete Error: No User exists for id:" + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
		}

		return success;

	}

	@PostMapping("/login")
	public User login(@RequestBody UserLogin ul) {
		User user = userRepo.findByUsernameAndPassword(ul.getUsername(), ul.getPassword());
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username or password not found");
		}
		return user;

	}
}

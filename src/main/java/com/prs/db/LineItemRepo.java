package com.prs.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prs.model.LineItem;
import com.prs.model.Request;

public interface LineItemRepo extends JpaRepository<LineItem, Integer> {
	List<LineItem> findByRequest(Request request);

}

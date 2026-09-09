package com.stmartinviersen.tuetenverkauf.order.repository;

import com.stmartinviersen.tuetenverkauf.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}

package com.pearchInventory.ioa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class InventoryOrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryOrdersApplication.class, args);
	}

}

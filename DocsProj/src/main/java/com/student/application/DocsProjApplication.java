package com.student.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point
 * @author Jason Fan
 *
 */
@SpringBootApplication //  Search all annotations, including packages of same parent (so also the builder/controller/etc. packages)
public class DocsProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocsProjApplication.class, args);
	}

}

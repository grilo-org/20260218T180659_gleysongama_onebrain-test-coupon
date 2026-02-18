package br.com.gama.cupon;

import org.springframework.boot.SpringApplication;

public class TestCuponApplication {

	public static void main(String[] args) {
		SpringApplication.from(CuponApplication::main).run(args);
	}

}

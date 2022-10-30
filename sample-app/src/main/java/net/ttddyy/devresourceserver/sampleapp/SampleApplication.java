/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ttddyy.devresourceserver.sampleapp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

/**
 * @author Tadaya Tsuyukubo
 */
@SpringBootApplication
public class SampleApplication {

	private static final Logger logger = LoggerFactory.getLogger(SampleApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Bean
	CommandLineRunner cli() {
		return (args) -> {
			// see "schema.sql" and "data.sql"
			logger.info("Running query");
			List<String> names = this.jdbcTemplate.query("SELECT name FROM emp", new SingleColumnRowMapper());

			System.out.println("vvvvvvvvvvvvvvvv");
			System.out.println("name = " + names);
			System.out.println("^^^^^^^^^^^^^^^^");
		};
	}

}

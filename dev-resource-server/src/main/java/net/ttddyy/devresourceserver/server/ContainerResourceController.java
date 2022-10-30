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

package net.ttddyy.devresourceserver.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tadaya Tsuyukubo
 */
@RestController
public class ContainerResourceController implements DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(ContainerResourceController.class);

	// TODO: should create a service class to manage containers
	private static final Map<String, GenericContainer<?>> containerMap = new HashMap<>();

	@PostMapping("/containers")
	public Map<String, String> create(@RequestParam String imageName) {
		// TODO: hardcoded for now. Get more params and manage container lifecycle
		// TODO: in proper impl, need concurrency control
		PostgreSQLContainer<?> container = new PostgreSQLContainer<>(imageName);
		containerMap.put(imageName, container);
		container.start();

		return Map.of(
				"url", container.getJdbcUrl(),
				"username", container.getUsername(),
				"password", container.getPassword());
	}

	@GetMapping("/containers")
	public List<String> list() {
		return containerMap.keySet().stream().toList();
	}

	@GetMapping("/containers/{imageName}")
	public Map<String, String> info(@PathVariable String imageName) {
		GenericContainer<?> container = containerMap.get(imageName);

		if (container == null) {
			return Collections.emptyMap();
		}
		// TODO: force casting for now
		PostgreSQLContainer<?> pgContainer = (PostgreSQLContainer<?>) container;
		return Map.of(
				"url", pgContainer.getJdbcUrl(),
				"username", pgContainer.getUsername(),
				"password", pgContainer.getPassword());
	}

	@DeleteMapping("/containers/{imageName}")
	public void remove(@PathVariable String imageName) {
		GenericContainer<?> container = containerMap.remove(imageName);
		if (container != null) {
			container.stop();
		}
	}

	@Override
	public void destroy() throws Exception {
		for (GenericContainer<?> container : containerMap.values()) {
			try {
				logger.info("Shutting down a container: " + container);
				container.stop();
			}
			catch (Exception ex) {
				logger.warn("Failed to shutdown a container: " + container);
			}
		}
	}
}

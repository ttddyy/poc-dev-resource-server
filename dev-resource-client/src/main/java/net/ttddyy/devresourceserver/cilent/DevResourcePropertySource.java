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

package net.ttddyy.devresourceserver.cilent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Tadaya Tsuyukubo
 */
public class DevResourcePropertySource extends EnumerablePropertySource<String> {

	private static final String PROPERTY_DATASOURCE_URL = "spring.datasource.url";

	private static final String PROPERTY_DATASOURCE_USERNAME = "spring.datasource.username";

	private static final String PROPERTY_DATASOURCE_PASSWORD = "spring.datasource.password";

	private final Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

	private DevResourceClient devResourceClient;

	public DevResourcePropertySource(String name) {
		super(name);
		init();
	}

	private void init() {
		// TODO: baseurl is currently hardcoded
		WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
		HttpClientAdapter clientAdapter = WebClientAdapter.forClient(webClient);
		this.devResourceClient = HttpServiceProxyFactory.builder(clientAdapter).build().createClient(DevResourceClient.class);
	}

	@Override
	public String[] getPropertyNames() {
		return new String[] { PROPERTY_DATASOURCE_URL, PROPERTY_DATASOURCE_USERNAME, PROPERTY_DATASOURCE_PASSWORD };
	}

	@Override
	public Object getProperty(String name) {
		if (!containsProperty(name)) {
			return null;
		}
		// TODO: hardcoded for now
		String imageName = "postgres:14";
		Map<String, String> properties = getOrCreateContainer(imageName);
		switch (name) {
			case PROPERTY_DATASOURCE_URL -> {
				return properties.get("url");
			}
			case PROPERTY_DATASOURCE_USERNAME -> {
				return properties.get("username");
			}
			case PROPERTY_DATASOURCE_PASSWORD -> {
				return properties.get("password");
			}
		}
		throw new IllegalArgumentException("Not supported property: " + name);
	}

	private Map<String, String> getOrCreateContainer(String imageName) {
		Map<String, String> map = cache.computeIfAbsent(imageName, key -> {
			// TODO: create a model class instead of map
			Map<String, String> props = this.devResourceClient.get(imageName);
			if (props.isEmpty()) {
				logger.info("Requesting a new image - " + imageName);
				props = this.devResourceClient.create(imageName);
			}
			return props;
		});
		return map;
	}
}

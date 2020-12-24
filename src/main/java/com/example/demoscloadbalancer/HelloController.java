package com.example.demoscloadbalancer;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class HelloController {

	private final RestTemplate restTemplate;

	private final WebClient webClient;

	public HelloController(@LoadBalanced RestTemplate restTemplate, WebClient.Builder webClientBuilder, LoadBalancedExchangeFilterFunction loadBalancerExchangeFilterFunction) {
		this.restTemplate = restTemplate;
		this.webClient = webClientBuilder
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
				.filter(loadBalancerExchangeFilterFunction)
				.build();
	}

	@GetMapping(path = "/1")
	public ResponseEntity<String> hello1() {
		return this.restTemplate.getForEntity("http://demo/get", String.class);
	}

	@GetMapping(path = "/2")
	public Mono<ResponseEntity<String>> hello2() {
		return this.webClient
				.get()
				.uri("http://demo/get")
				.retrieve().toEntity(String.class);
	}
}

package com.example.demoscloadbalancer.dummy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration(proxyBeanMethods = false)
public class DummyServiceConfig {

	@Bean
	public HttpServer service1() {
		return this.createHttpServer(1, 12333, HttpStatus.OK);
	}

	@Bean
	public HttpServer service2() {
		return this.createHttpServer(2, 12334, HttpStatus.OK);
	}

	@Bean
	public HttpServer service3() {
		return this.createHttpServer(3, 12335, HttpStatus.OK);
	}

	public HttpServer createHttpServer(int id, int port, HttpStatus status) {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", exchange -> {
				String response = status.getReasonPhrase();
				exchange.getResponseHeaders().add("X-Service-Id", String.valueOf(id));
				exchange.sendResponseHeaders(status.value(), response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			});
			server.start();
			return server;
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

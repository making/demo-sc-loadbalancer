package com.example.demoscloadbalancer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class LoadBalancerConfig {

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder, OkHttpClient okHttpClient) {
		return builder
				.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(okHttpClient))
				.build();
	}

	@Bean
	public OkHttpClient okHttpClient(MeterRegistry meterRegistry) {
		return new OkHttpClient.Builder()
				.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
				.eventListener(OkHttpMetricsEventListener
						.builder(meterRegistry, "okhttp.requests")
						.uriMapper(request -> request.url().toString())
						.build())
				.build();
	}

	@Bean
	public SurgicalRoutingRequestTransformer surgicalRoutingRequestTransformer() {
		return new SurgicalRoutingRequestTransformer();
	}
}

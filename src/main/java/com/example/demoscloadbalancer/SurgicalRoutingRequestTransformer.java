package com.example.demoscloadbalancer;

import java.util.Map;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

/**
 * Adds a surgical routing header to the request if CF App GUID and CF Instance
 * Index are present in metadata.
 *
 * @see <a href=
 *      'https://docs.cloudfoundry.org/devguide/deploy-apps/routes-domains.html#surgical-routing'>
 *      https://docs.cloudfoundry.org/devguide/deploy-apps/routes-domains.html#
 *      surgical-routing</a>
 *
 * @author William Tran
 */
public class SurgicalRoutingRequestTransformer implements LoadBalancerRequestTransformer {
	public static final String CF_APP_GUID = "cfAppGuid";

	public static final String CF_INSTANCE_INDEX = "cfInstanceIndex";

	public static final String SURGICAL_ROUTING_HEADER = "X-Cf-App-Instance";

	@Override
	public HttpRequest transformRequest(HttpRequest request, ServiceInstance instance) {
		Map<String, String> metadata = instance.getMetadata();
		if (metadata.containsKey(CF_APP_GUID) && metadata.containsKey(CF_INSTANCE_INDEX)) {
			final String headerValue = String.format("%s:%s", metadata.get(CF_APP_GUID), metadata.get(CF_INSTANCE_INDEX));
			// request.getHeaders might be immutable, so return a wrapper
			return new HttpRequestWrapper(request) {
				@Override
				public HttpHeaders getHeaders() {
					HttpHeaders headers = new HttpHeaders();
					headers.putAll(super.getHeaders());
					headers.add(SURGICAL_ROUTING_HEADER, headerValue);
					return headers;
				}
			};
		}
		return request;
	}

}
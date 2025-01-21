package telran.probes.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProxyServiceImpl implements ProxyService{

	@Value("#{${app.map.hosts.ports}}")
	Map<String, String> routingMap;
	
	@Override
	public ResponseEntity<byte[]> proxyRouting(ProxyExchange<byte[]> proxy, HttpServletRequest request,
			String httpMethod) {
		String url = getUrl(request);
		log.debug("Routed URL is {}", url);
		return switch(httpMethod) {
		case "GET" -> proxy.uri(url).get();
		case "POST" -> proxy.uri(url).post();
		case "PUT" -> proxy.uri(url).put();
		default -> throw new IllegalArgumentException("Unexpected value: " + httpMethod);
		};
	}

	private String getUrl(HttpServletRequest request) {
		String resourceName = request.getRequestURI();
		log.debug("Resource name: {}", resourceName);
		String endpoint = resourceName.split("/")[1];
		log.debug("Endpoint is {}", endpoint);
		String serviceUrl = routingMap.get(endpoint);
		return String.format("http://%s%s", serviceUrl, resourceName);
	}
	
	@PostConstruct
	void logMap() {
		log.debug("Routing map: {}", routingMap);
	}

}

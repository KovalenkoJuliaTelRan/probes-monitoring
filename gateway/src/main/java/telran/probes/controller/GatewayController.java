package telran.probes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import telran.probes.service.ProxyService;

@RestController
public class GatewayController {
	@Autowired
	ProxyService service;
	
	@GetMapping("/**")
	ResponseEntity<byte[]> getProxy(ProxyExchange<byte[]> proxy, HttpServletRequest request){
		return service.proxyRouting(proxy, request, "GET");
	}
	
	@PostMapping("/**")
	ResponseEntity<byte[]> postProxy(ProxyExchange<byte[]> proxy, HttpServletRequest request){
		return service.proxyRouting(proxy, request, "POST");
	}
	
	@PutMapping("/**")
	ResponseEntity<byte[]> putProxy(ProxyExchange<byte[]> proxy, HttpServletRequest request){
		return service.proxyRouting(proxy, request, "PUT");
	}
}

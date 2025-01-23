package telran.probes.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.probes.dto.RolesResponseDto;
import telran.probes.dto.UserRequestDto;
import telran.probes.dto.UserResponseDto;
import telran.probes.dto.UserUpdateDto;
import telran.probes.entities.UserAccount;
import telran.probes.service.IAccountingManagement;

@RestController
@RequestMapping("/account")
public class AccountingController {
	@Autowired
	IAccountingManagement service;

	@PostMapping("/register")
	public UserResponseDto registration(@RequestBody UserRequestDto account) {
		return service.registration(account);
	}

	@DeleteMapping("/user/{login}")
	public UserResponseDto removeUser(@PathVariable String login) {
		return service.removeUser(login);
	}

	@PostMapping("/login")
	public UserAccount login(Principal principal) {
		return service.getUser(principal.getName());
	}

	@PutMapping("/user")
	public UserResponseDto editUser(Principal principal, @RequestBody UserUpdateDto account) {
		return service.editUser(principal.getName(), account);
	}

	@PutMapping("/password")
	public boolean updatePassword(Principal principal, @RequestHeader("X-New-Password") String password) {
		return service.updatePassword(principal.getName(), password);
	}

	@PutMapping("/revoke/{login}")
	public boolean revokeAccount(@PathVariable String login) {
		return service.revokeAccount(login);
	}

	@PutMapping("/activate/{login}")
	public boolean activateAccount(@PathVariable String login) {
		return service.activateAccount(login);
	}

	@GetMapping("/password/{login}")
	public String getPasswordHash(@PathVariable String login) {
		return service.getPasswordHash(login);
	}

	@GetMapping("/activation_date/{login}")
	public LocalDateTime getActivationDate(@PathVariable String login) {
		return service.getActivationDate(login);
	}

	@GetMapping("/roles/{login}")
	public RolesResponseDto getRoles(@PathVariable String login) {
		return service.getRoles(login);
	}

	@PutMapping("/user/{login}/role/{role}")
	public RolesResponseDto addRole(@PathVariable String login, @PathVariable String role) {
		return service.addRole(login, role);
	}

	@DeleteMapping("/user/{login}/role/{role}")
	public RolesResponseDto removeRole(@PathVariable String login, @PathVariable String role) {
		return service.removeRole(login, role);
	}
}

package telran.probes.service;

import java.time.LocalDateTime;

import telran.probes.dto.RolesResponseDto;
import telran.probes.dto.UserRequestDto;
import telran.probes.dto.UserResponseDto;
import telran.probes.dto.UserUpdateDto;
import telran.probes.entities.UserAccount;

public interface IAccountingManagement
{
	UserResponseDto registration(UserRequestDto account);
	UserResponseDto removeUser(String login);
	UserAccount getUser(String login);
	UserResponseDto editUser(String login, UserUpdateDto account);
	boolean updatePassword(String login, String password);
	boolean revokeAccount(String login);
	boolean activateAccount(String login);
	String getPasswordHash(String login);
	LocalDateTime getActivationDate(String login);
	RolesResponseDto getRoles(String login);
	RolesResponseDto addRole(String login, String role);
	RolesResponseDto removeRole(String login, String role);
}

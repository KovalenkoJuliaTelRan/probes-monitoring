package telran.probes.service;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import telran.probes.dto.RolesResponseDto;
import telran.probes.dto.UserRequestDto;
import telran.probes.dto.UserResponseDto;
import telran.probes.dto.UserUpdateDto;
import telran.probes.dto.exceptions.*;
import telran.probes.entities.UserAccount;

@Service
public class AccountingManagementMongo implements IAccountingManagement, CommandLineRunner {
	@Autowired
	MongoTemplate template;
	@Value("${password_length:5}")
	private int passwordLength;
	@Value("${n_last_hash:3}")
	private int n_last_hash;
	@Autowired
	PasswordEncoder encoder;

	@Override
	public UserResponseDto registration(UserRequestDto account) {
		if (!isPasswordValid(account.getPassword()))
			throw new PasswordNotValidException(account.getPassword());
		UserAccount acc = new UserAccount(account.getLogin(), createHash(account.getPassword()), 
				account.getFirstName(), account.getLastName());
		try {
			template.insert(acc);
		} catch (DuplicateKeyException e) {
			throw new UserExistsException(account.getLogin());
		}
		return UserResponseDto.build(acc);
	}

	private String createHash(String password) {
		return encoder.encode(password);
	}

	private boolean isPasswordValid(String password) {
		return password.length() >= passwordLength;
	}

	private UserAccount getUserAccount(String login) {
		UserAccount user = template.findById(login, UserAccount.class);
		if (user == null)
			throw new UserNotFoundException(login);
		return user;
	}

	@Override
	public UserResponseDto removeUser(String login) {
		UserAccount account = template.findAndRemove(new Query(Criteria.where("login").is(login)), 
				UserAccount.class);
		if (account == null)
			throw new UserNotFoundException(login);
		return UserResponseDto.build(account);
	}

	@Override
	public UserAccount getUser(String login) {
		return getUserAccount(login);
	}

	@Override
	public UserResponseDto editUser(String login, UserUpdateDto data) {
		if (data.getFirstName() == null || data.getLastName() == null)
			throw new UserUpdateException();
		Query query = new Query(Criteria.where("login").is(login));
		Update update = new Update().set("firstName", data.getFirstName()).set("lastName", data.getLastName());
		UserAccount user = template.findAndModify(query, update,
				new FindAndModifyOptions().returnNew(true).upsert(false), UserAccount.class);
		if (user == null)
			throw new UserNotFoundException(login);
		return UserResponseDto.build(user);
	}

	@Override
	public boolean updatePassword(String login, String newPassword) {
		if (newPassword == null || !isPasswordValid(newPassword))
			throw new PasswordNotValidException(newPassword);
		UserAccount user = getUserAccount(login);
		if (encoder.matches(newPassword, user.getHash()))
			throw new PasswordNotValidException(newPassword);
		LinkedList<String> lastHash = user.getLastHash();
		if (isPasswordFromLast(newPassword, lastHash))
			throw new PasswordNotValidException(newPassword);
		if (lastHash.size() == n_last_hash)
			lastHash.removeFirst();
		lastHash.add(user.getHash());
		user.setHash(encoder.encode(newPassword));
		user.setActivationDate(LocalDateTime.now());
		template.save(user);
		return true;
	}

	private boolean isPasswordFromLast(String newPassword, LinkedList<String> lastHash) {
		return lastHash.stream().anyMatch(p -> encoder.matches(newPassword, p));
	}

	@Override
	public boolean revokeAccount(String login) {
		UserAccount account = getUserAccount(login);
		if (account.isRevoked())
			throw new AccountRevokeException(login);
		account.setRevoked(true);
		template.save(account);
		return true;
	}

	@Override
	public boolean activateAccount(String login) {
		UserAccount account = getUserAccount(login);
		if (!account.isRevoked())
			throw new AccountActivationException(login);
		account.setRevoked(false);
		account.setActivationDate(LocalDateTime.now());
		template.save(account);
		return true;
	}

	@Override
	public String getPasswordHash(String login) {
		UserAccount account = getUserAccount(login);
		return account.isRevoked() ? null : account.getHash();
	}

	@Override
	public LocalDateTime getActivationDate(String login) {
		UserAccount account = getUserAccount(login);
		return account.isRevoked() ? null : account.getActivationDate();
	}

	@Override
	public RolesResponseDto getRoles(String login) {
		UserAccount account = getUserAccount(login);
		return account.isRevoked() ? null : new RolesResponseDto(login, account.getRoles());
	}

	@Override
	public RolesResponseDto addRole(String login, String role) {
		UserAccount account = getUserAccount(login);
		HashSet<String> roles = account.getRoles();
		if (roles.contains(role))
			throw new RoleExistsException(role);
		roles.add(role);
		template.save(account);
		return new RolesResponseDto(login, account.getRoles());
	}

	@Override
	public RolesResponseDto removeRole(String login, String role) {
		UserAccount account = getUserAccount(login);
		HashSet<String> roles = account.getRoles();
		if (!roles.contains(role))
			throw new RoleNotExistsException(role);
		roles.remove(role);
		template.save(account);
		return new RolesResponseDto(login, account.getRoles());
	}

	@Override
	public void run(String... args) throws Exception {
		if (!template.exists(new Query(Criteria.where("login").is("admin")), UserAccount.class)) {
			UserAccount admin = new UserAccount("admin", encoder.encode("admin"), "", "");
			admin.setRoles(new HashSet<String>(List.of("ADMIN")));
			template.save(admin);
		}
	}
}

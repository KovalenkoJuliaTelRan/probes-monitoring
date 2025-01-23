package telran.probes.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document("accounts")
public class UserAccount
{
	@Id
	@Setter(value = AccessLevel.NONE)
	private String login;
	private String hash;
	private String firstName;
	private String lastName;
	private HashSet<String> roles;
	private LocalDateTime activationDate;
	private boolean revoked;
	private LinkedList<String> lastHash;
	public UserAccount(String login, String hash, String firstName, String lastName)
	{
		this.login = login;
		this.hash = hash;
		this.firstName = firstName;
		this.lastName = lastName;
		roles = new HashSet<>();
		roles.add("USER");
		activationDate = LocalDateTime.now();
		lastHash = new LinkedList<>();
	}
	public UserAccount()
	{
		roles = new HashSet<>();
		roles.add("USER");
		activationDate = LocalDateTime.now();
		lastHash = new LinkedList<>();
	}
}

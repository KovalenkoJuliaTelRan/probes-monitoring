package telran.probes.dto;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import telran.probes.entities.UserAccount;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDto {
	private String login;
	private String firstName;
	private String lastName;
	private HashSet<String> roles;

	public static UserResponseDto build(UserAccount user) {
		return new UserResponseDto(user.getLogin(), user.getFirstName(), user.getLastName(), user.getRoles());
	}
}

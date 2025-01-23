package telran.probes.dto;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RolesResponseDto {
	private String login;
	private HashSet<String> roles;
}

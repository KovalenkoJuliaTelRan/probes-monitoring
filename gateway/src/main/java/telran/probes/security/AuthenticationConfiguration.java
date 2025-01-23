package telran.probes.security;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import telran.probes.entities.UserAccount;

@Configuration
public class AuthenticationConfiguration implements UserDetailsService
{
	@Autowired
	MongoTemplate template;;
	@Value("${activationPeriod: 5}")//days
	int activationPeriod;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		UserAccount user = template.findById(username, UserAccount.class);
		if(user == null)
			throw new UsernameNotFoundException(username);
		String password = user.getHash();
		boolean passwordIsNotExpired = ChronoUnit.MINUTES.between(LocalDateTime.now(), user.getActivationDate()) > activationPeriod;
		String[] roles = user.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
		return new UserProfile(username, password, AuthorityUtils.createAuthorityList(roles), passwordIsNotExpired);	
	}
}

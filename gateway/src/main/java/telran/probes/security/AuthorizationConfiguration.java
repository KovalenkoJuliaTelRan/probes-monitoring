package telran.probes.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class AuthorizationConfiguration
{
	@Value("${app.user.notifier.role}")
	String userNotifierRole;
	@Value("${app.user.range.role}")
	String userRangeRole;
	@Value("${app.admin.notifier.role}")
	String adminNotifierRole;
	@Value("${app.admin.range.role}")
	String adminRangeRole;
	@Value("${app.sensor.range.provider.url}")
	String rangeSensorUrl;
	@Value("${app.emails.provider.url}")
	String emailsSensorUrl;
	@Value("${app.admin.range.url}")
	String adminRangeUrl;
	@Value("${app.admin.emails.url}")
	String adminEmailsUrl;
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception
	{
		http.httpBasic(Customizer.withDefaults())
		.csrf(csrf -> csrf.disable())
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
		http.addFilterBefore(new ExpiredPasswordFilter(), BasicAuthenticationFilter.class);
		http.authorizeHttpRequests(
				authorize -> authorize.requestMatchers("/account/register", "/account/register/").permitAll()
					.requestMatchers(HttpMethod.PUT, "/account/revoke/*", "/account/activate/*").hasRole("ADMIN")
					.requestMatchers("/account/user/*/role/*").hasAnyRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/account/*/{login}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
					.requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
					.requestMatchers("/account/login", "/account/password").authenticated()
					.requestMatchers(HttpMethod.PUT, "/account/user").authenticated()
					.requestMatchers(adminRangeUrl + "/**").hasRole(adminRangeRole)
					.requestMatchers(adminEmailsUrl + "/**").hasRole(adminNotifierRole)
					.requestMatchers(rangeSensorUrl + "/**").hasRole(userRangeRole)
					.requestMatchers(emailsSensorUrl + "/**").hasRole(userNotifierRole)
					.anyRequest().denyAll());
		return http.build();
	}
}

package com.RBU.Project2.Security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final Jwtservice jwtservice ;
	public JwtAuthFilter (Jwtservice jwtservice) {
		this.jwtservice = jwtservice;
	}
	@Override
	protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain) throws IOException, ServletException  {
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterchain.doFilter(request, response);
			return;
		}
		String token = authHeader.substring(7);
		if (jwtservice.isTokenValid(token)) {
			String email = jwtservice.extractEmail(token);
			Long userId = jwtservice.extractUserId(token);
			CustomUserDetail userDetails = new CustomUserDetail(userId,email);
			
			UsernamePasswordAuthenticationToken authToken
			= new UsernamePasswordAuthenticationToken(
					userDetails, null, Collections.emptyList());
			authToken.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
			filterchain.doFilter(request, response);
		}
	}

}
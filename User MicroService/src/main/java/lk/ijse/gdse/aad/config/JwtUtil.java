package lk.ijse.gdse.aad.config;


import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lk.ijse.gdse.aad.dto.UserDTO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private long accessTokenValidity = 60*60*1000;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private final JwtParser jwtParser;
    private final String secret_key = "mysecretkey";

    public JwtUtil(){
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createToken(UserDTO user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("userId",user.getUserId());
        claims.put("userName",user.getUsername());
        claims.put("userPassword",user.getPassword());
        claims.put("roles",user.getRoles());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }
    private Claims parseJwtClaims(String token) {
        System.out.println("pass");
         return jwtParser.parseClaimsJws(token).getBody();
    }


    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            System.out.println("resolve token : "+token);
            if (token != null) {
                System.out.println("pass");
                Claims claims = parseJwtClaims(token);
                System.out.println("clamis 1 : "+claims);
                return claims;
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        System.out.println("Request : "+request);
        String bearerToken = request.getHeader(TOKEN_HEADER);
        System.out.println("Bearer Token : "+bearerToken);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            System.out.println("here come : "+bearerToken.substring(TOKEN_PREFIX.length()));
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    private List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }



}
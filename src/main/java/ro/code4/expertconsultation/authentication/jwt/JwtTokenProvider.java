package ro.code4.expertconsultation.authentication.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ro.code4.expertconsultation.authentication.model.UserPrincipal;

import java.util.Date;


@Slf4j
@Component
public class JwtTokenProvider {

    //private static final log log = logFactory.getlog(JwtTokenProvider.class);


    //    @Value("${app.jwtSecret}")
    private final String jwtSecret = "HRlELXqpSB"; //temporary key

    //    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(final Authentication authentication) {

        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();


        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromJWT(final String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(final String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}

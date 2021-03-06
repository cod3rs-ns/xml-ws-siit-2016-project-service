package rs.ac.uns.sw.xml.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for token manipulation.
 */
@Component
public class TokenUtils {

    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";
    private static final String CREATED = "created";

    @Value("${xmlws.token.secret}")
    private String secret;

    @Value("${xmlws.token.expiration}")
    private Long expiration;

    /**
     * Extracts username from token.
     *
     * @param token authentication token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        String username = null;
        if (token != null && !"null".equals(token)) {
            final Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        }
        return username;
    }

    /**
     * Extracts Date of creation from token.
     *
     * @param token authentication token
     * @return Date of creation
     */
    public Date getCreatedDateFromToken(String token) {
        Date created = null;
        if (token != null && !"null".equals(token)) {
            final Claims claims = this.getClaimsFromToken(token);
            created = new Date((Long) claims.get(CREATED));
        }
        return created;
    }


    /**
     * Extracts Date of expiration from token.
     *
     * @param token authentication token
     * @return Date of expiration
     */
    public Date getExpirationDateFromToken(String token) {
        Date expirationDate = null;
        if (token != null && !"null".equals(token)) {
            final Claims claims = this.getClaimsFromToken(token);
            expirationDate = claims.getExpiration();
        }
        return expirationDate;
    }

    /**
     * Extracts Audience from token.
     *
     * @param token authentication token
     * @return Audience - String
     */
    public String getAudienceFromToken(String token) {
        String audience = null;
        if (token != null && !"null".equals(token)) {
            final Claims claims = this.getClaimsFromToken(token);
            audience = (String) claims.get("audience");
        }
        return audience;
    }

    /**
     * Extracts Claims from token.
     *
     * @param token authentication token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        if (token != null && !"null".equals(token)) {
            claims = Jwts.parser()
                    .setSigningKey(this.secret)
                    .parseClaimsJws(token)
                    .getBody();
        }
        return claims;
    }

    private Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + this.expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expirationDate = this.getExpirationDateFromToken(token);
        return expirationDate.before(this.generateCurrentDate());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return lastPasswordReset != null && created.before(lastPasswordReset);
    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = this.getAudienceFromToken(token);
        return AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience);
    }

    /**
     * Generates token based on details of the user.
     *
     * @param userDetails UserDetails
     * @return encrypted string - token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put(CREATED, this.generateCurrentDate());
        return this.generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(this.generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, this.secret)
                .compact();
    }

    /**
     * Checks if token can be refreshed.
     *
     * @param token             authentication token
     * @param lastPasswordReset Date of last password reset
     * @return true if token can be refreshed, false otherwise
     */
    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = this.getCreatedDateFromToken(token);
        return !(this.isCreatedBeforeLastPasswordReset(created, lastPasswordReset)) && (!(this.isTokenExpired(token)) || this.ignoreTokenExpiration(token));
    }

    /**
     * Creates refreshed token
     *
     * @param token current authentication token
     * @return new refreshed token
     */
    public String refreshToken(String token) {
        String refreshedToken = null;
        if (token != null && !"null".equals(token)) {
            final Claims claims = this.getClaimsFromToken(token);
            claims.put(CREATED, this.generateCurrentDate());
            refreshedToken = this.generateToken(claims);
        }
        return refreshedToken;
    }

    /**
     * Perform token validation.
     *
     * @param token       authentication token
     * @param userDetails current user details
     * @return true if token is validate, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        SecurityUser user = (SecurityUser) userDetails;
        final String username = this.getUsernameFromToken(token);
        /**
         * Currently unused.
         *  final Date created = this.getCreatedDateFromToken(token);
         *  final Date expiration = this.getExpirationDateFromToken(token);
         *  return (username.equals(user.getUsername()) && !(this.isTokenExpired(token)));
         */
        return username.equals(user.getUsername());

    }
}

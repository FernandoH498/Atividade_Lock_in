package br.sesi.achadosperdidos.util;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfUtil {

    private static final String ATTR = "csrfToken";
    private static final SecureRandom RNG = new SecureRandom();

    private CsrfUtil() {}

    public static String getOrCreate(HttpSession session) {
        String token = (String) session.getAttribute(ATTR);
        if (token == null) {
            byte[] bytes = new byte[32];
            RNG.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(ATTR, token);
        }
        return token;
    }

    public static boolean validate(HttpSession session, String submitted) {
        if (session == null || submitted == null) return false;
        String stored = (String) session.getAttribute(ATTR);
        return stored != null && stored.equals(submitted);
    }
}

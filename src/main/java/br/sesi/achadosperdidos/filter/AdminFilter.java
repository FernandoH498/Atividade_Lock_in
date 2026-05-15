package br.sesi.achadosperdidos.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  hReq    = (HttpServletRequest)  req;
        HttpServletResponse hRes    = (HttpServletResponse) res;
        HttpSession         session = hReq.getSession(false);

        boolean isAdmin = session != null &&
                          Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        if (!isAdmin) {
            hRes.sendError(HttpServletResponse.SC_FORBIDDEN,
                           "Acesso restrito ao administrador.");
            return;
        }
        chain.doFilter(req, res);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}

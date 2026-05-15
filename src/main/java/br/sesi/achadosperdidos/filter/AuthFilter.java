package br.sesi.achadosperdidos.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/itens/novo", "/itens/devolver", "/itens/excluir"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  hReq  = (HttpServletRequest)  req;
        HttpServletResponse hRes  = (HttpServletResponse) res;
        HttpSession         session = hReq.getSession(false);

        boolean logado = session != null && session.getAttribute("userId") != null;
        if (!logado) {
            String after = hReq.getRequestURI();
            String qs    = hReq.getQueryString();
            if (qs != null) after += "?" + qs;
            session = hReq.getSession(true);
            session.setAttribute("redirectAfterLogin", after);
            hRes.sendRedirect(hReq.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, res);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}

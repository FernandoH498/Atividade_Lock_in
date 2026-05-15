package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession(true)));
        req.getRequestDispatcher("/WEB-INF/views/logout_confirm.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            if (!CsrfUtil.validate(session, req.getParameter("csrf"))) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido.");
                return;
            }
            session.invalidate();
        }
        // limpa cookie de sessão
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        res.addCookie(cookie);
        res.sendRedirect(req.getContextPath() + "/login?logout=1");
    }
}

package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.model.User;
import br.sesi.achadosperdidos.service.UserService;
import br.sesi.achadosperdidos.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService svc = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            res.sendRedirect(req.getContextPath() + "/itens");
            return;
        }
        req.getSession(true);
        req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (!CsrfUtil.validate(session, req.getParameter("csrf"))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido.");
            return;
        }

        String login = req.getParameter("login");
        String senha = req.getParameter("senha");

        try {
            int[] idOut = new int[1];
            UserService.LoginResultado resultado = svc.login(login, senha, idOut);

            if (resultado == UserService.LoginResultado.OK) {
                User u = svc.buscarPorId(idOut[0]);

                // previne session fixation
                String redirect = null;
                if (session != null) {
                    redirect = (String) session.getAttribute("redirectAfterLogin");
                    session.invalidate();
                }
                session = req.getSession(true);
                session.setAttribute("userId",  u.getId());
                session.setAttribute("userNome", u.getNome());
                session.setAttribute("isAdmin",  u.isAdmin());
                session.setMaxInactiveInterval(30 * 60); // 30 min

                if (u.isAdmin()) {
                    res.sendRedirect(req.getContextPath() + "/admin");
                    return;
                }
                if (redirect != null && redirect.startsWith(req.getContextPath())) {
                    res.sendRedirect(redirect);
                } else {
                    res.sendRedirect(req.getContextPath() + "/itens");
                }
                return;
            }

            String msg;
            if (resultado == UserService.LoginResultado.CONTA_BLOQUEADA) {
                msg = "Conta bloqueada por excesso de tentativas. Contate o administrador.";
            } else if (resultado == UserService.LoginResultado.EMAIL_NAO_VERIFICADO) {
                msg = "Confirme seu e-mail antes de entrar. <a href='" +
                      req.getContextPath() + "/verify?email=" + encodeParam(login) + "'>Reenviar código</a>";
            } else {
                msg = "Login ou senha incorretos.";
            }

            req.setAttribute("erro", msg);
            req.setAttribute("loginVal", login);
            req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);

        } catch (Exception e) {
            getServletContext().log("Erro no login", e);
            req.setAttribute("erro", "Erro interno. Tente novamente.");
            req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
        }
    }

    private String encodeParam(String s) {
        try { return java.net.URLEncoder.encode(s == null ? "" : s, "UTF-8"); }
        catch (Exception e) { return ""; }
    }
}

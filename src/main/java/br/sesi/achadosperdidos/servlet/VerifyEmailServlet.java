package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.service.UserService;
import br.sesi.achadosperdidos.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/verify")
public class VerifyEmailServlet extends HttpServlet {

    private final UserService svc = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        req.getSession(true);
        req.setAttribute("csrf",  CsrfUtil.getOrCreate(req.getSession()));
        req.setAttribute("email", req.getParameter("email"));
        req.setAttribute("novo",  req.getParameter("novo"));
        req.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(req, res);
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

        String action = req.getParameter("action");
        String email  = req.getParameter("email");
        String codigo = req.getParameter("codigo");

        try {
            if ("reenviar".equals(action)) {
                svc.reenviarCodigo(email);
                req.setAttribute("sucesso", "Novo código enviado para " + email);
                req.setAttribute("email", email);
                req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
                req.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(req, res);
                return;
            }

            boolean ok = svc.verificarEmail(email, codigo);
            if (ok) {
                res.sendRedirect(req.getContextPath() + "/login?verificado=1");
            } else {
                req.setAttribute("erro", "Código inválido ou expirado.");
                req.setAttribute("email", email);
                req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
                req.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(req, res);
            }
        } catch (UserService.RegistroException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(req, res);
        } catch (Exception e) {
            getServletContext().log("Erro na verificação", e);
            req.setAttribute("erro", "Erro interno. Tente novamente.");
            req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(req, res);
        }
    }
}

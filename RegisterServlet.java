package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.service.UserService;
import br.sesi.achadosperdidos.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

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
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, res);
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

        String email = req.getParameter("email");
        String nome  = req.getParameter("nome");
        String senha = req.getParameter("senha");
        String conf  = req.getParameter("senha_confirm");

        try {
            if (senha == null || !senha.equals(conf)) {
                req.setAttribute("erro", "As senhas não coincidem.");
                req.setAttribute("emailVal", email);
                req.setAttribute("nomeVal", nome);
                req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, res);
                return;
            }

            svc.registrar(email, nome, senha);

            res.sendRedirect(req.getContextPath() + "/verify?email=" +
                    java.net.URLEncoder.encode(email == null ? "" : email.trim().toLowerCase(), "UTF-8") +
                    "&novo=1");

        } catch (UserService.RegistroException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("emailVal", email);
            req.setAttribute("nomeVal", nome);
            req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, res);
        } catch (Exception e) {
            getServletContext().log("Erro no cadastro", e);
            req.setAttribute("erro", "Erro interno. Tente novamente.");
            req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, res);
        }
    }
}

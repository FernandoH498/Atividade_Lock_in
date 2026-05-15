package br.sesi.achadosperdidos.servlet.admin;

import br.sesi.achadosperdidos.model.User;
import br.sesi.achadosperdidos.service.UserService;
import br.sesi.achadosperdidos.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private final UserService svc = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            List<User> usuarios = svc.listarTodos();
            req.setAttribute("usuarios", usuarios);
        } catch (Exception e) {
            getServletContext().log("Erro ao listar usuários", e);
            req.setAttribute("erro", "Erro ao carregar usuários.");
        }
        req.setAttribute("csrf", CsrfUtil.getOrCreate(req.getSession()));
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, res);
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
        String idStr  = req.getParameter("userId");

        try {
            int id = Integer.parseInt(idStr);
            User alvo = svc.buscarPorId(id);
            if (alvo == null) {
                res.sendRedirect(req.getContextPath() + "/admin/users?erro=not_found");
                return;
            }
            // nunca permite operar no admin
            if (alvo.isAdmin()) {
                res.sendRedirect(req.getContextPath() + "/admin/users?erro=forbidden");
                return;
            }

            String act = action == null ? "" : action;
            if ("unlock".equals(act)) {
                svc.desbloquearConta(id);
            } else if ("block".equals(act)) {
                br.sesi.achadosperdidos.dao.UserDAO uDao = new br.sesi.achadosperdidos.dao.UserDAO();
                uDao.updateStatus(id, "BLOQUEADO");
            } else if ("delete".equals(act)) {
                svc.excluirUsuario(id);
            } else if ("edit".equals(act)) {
                String nome      = req.getParameter("nome");
                String novaSenha = req.getParameter("nova_senha");
                if (nome != null && !nome.trim().isEmpty()) svc.atualizarNome(id, nome);
                if (novaSenha != null && !novaSenha.trim().isEmpty()) svc.atualizarSenha(id, novaSenha);
            }
            res.sendRedirect(req.getContextPath() + "/admin/users?ok=1");
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/admin/users?erro=invalid_id");
        } catch (Exception e) {
            getServletContext().log("Erro ao gerenciar usuário", e);
            res.sendRedirect(req.getContextPath() + "/admin/users?erro=server");
        }
    }
}

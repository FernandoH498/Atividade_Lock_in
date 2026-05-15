package br.sesi.achadosperdidos.servlet.admin;

import br.sesi.achadosperdidos.dao.ItemDAO;
import br.sesi.achadosperdidos.model.User;
import br.sesi.achadosperdidos.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {

    private final UserService userSvc = new UserService();
    private final ItemDAO     itemDAO = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            List<User> usuarios = userSvc.listarTodos();
            long totalUsuarios   = usuarios.stream().filter(u -> !u.isAdmin()).count();
            long bloqueados      = usuarios.stream().filter(User::isBloqueado).count();
            long pendentes       = usuarios.stream().filter(User::isPendente).count();
            long totalItens      = itemDAO.findAll(null, null, null).size();

            req.setAttribute("totalUsuarios", totalUsuarios);
            req.setAttribute("bloqueados",    bloqueados);
            req.setAttribute("pendentes",     pendentes);
            req.setAttribute("totalItens",    totalItens);
        } catch (Exception e) {
            getServletContext().log("Erro no dashboard admin", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, res);
    }
}

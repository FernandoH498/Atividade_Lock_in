package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/itens/devolver")
public class ItemUpdateServlet extends HttpServlet {

    private final ItemService service = new ItemService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            service.devolverItem(id);
            resp.sendRedirect(req.getContextPath() + "/itens?sucesso=Item+marcado+como+devolvido!");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/itens?erro=ID+inválido");
        } catch (SQLException e) {
            getServletContext().log("Erro ao devolver item id=" + idStr, e);
            resp.sendRedirect(req.getContextPath() + "/itens?erro=Erro+ao+atualizar+status");
        }
    }
}

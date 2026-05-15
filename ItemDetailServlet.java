package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.model.Item;
import br.sesi.achadosperdidos.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/itens/detalhe")
public class ItemDetailServlet extends HttpServlet {

    private final ItemService service = new ItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");

        if (idStr == null || idStr.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/itens?erro=ID+não+informado");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Item item = service.buscarItemPorId(id);
            req.setAttribute("item", item);
            req.getRequestDispatcher("/WEB-INF/views/detail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/itens?erro=ID+inválido");
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao buscar item id=" + idStr, e);
            req.setAttribute("erro", "Erro ao consultar o banco de dados. Tente novamente.");
            req.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(req, resp);
        }
    }
}

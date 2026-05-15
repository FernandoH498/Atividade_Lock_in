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
import java.util.List;

@WebServlet("/itens")
public class ItemListServlet extends HttpServlet {

    private final ItemService service = new ItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String status    = req.getParameter("status");
        String busca     = req.getParameter("busca");
        String categoria = req.getParameter("categoria");

        try {
            List<Item> todos = service.listarItens(null, null, null);
            long totalDevolvidos = todos.stream().filter(i -> "DEVOLVIDO".equals(i.getStatus())).count();
            req.setAttribute("totalItens",     todos.size());
            req.setAttribute("totalDevolvidos", totalDevolvidos);
            req.setAttribute("totalAchados",    todos.size() - totalDevolvidos);

            boolean temFiltro = (status != null && !status.isBlank())
                             || (busca != null && !busca.isBlank())
                             || (categoria != null && !categoria.isBlank());

            List<Item> itens = temFiltro ? service.listarItens(status, busca, categoria) : todos;
            req.setAttribute("itens", itens);

        } catch (SQLException e) {
            req.setAttribute("erro", "Erro ao consultar o banco de dados. Tente novamente.");
            getServletContext().log("Erro ao listar itens", e);
        }

        req.setAttribute("filtroStatus",    status);
        req.setAttribute("filtroBusca",     busca);
        req.setAttribute("filtroCategoria", categoria);

        req.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(req, resp);
    }
}

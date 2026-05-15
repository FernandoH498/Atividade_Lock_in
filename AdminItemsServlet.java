package br.sesi.achadosperdidos.servlet.admin;

import br.sesi.achadosperdidos.dao.ItemDAO;
import br.sesi.achadosperdidos.model.Item;
import br.sesi.achadosperdidos.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/items")
public class AdminItemsServlet extends HttpServlet {

    private final ItemDAO dao = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String busca     = req.getParameter("busca");
        String status    = req.getParameter("status");
        String categoria = req.getParameter("categoria");
        try {
            List<Item> itens = dao.findAll(status, busca, categoria);
            req.setAttribute("itens", itens);
        } catch (Exception e) {
            getServletContext().log("Erro ao listar itens (admin)", e);
            req.setAttribute("erro", "Erro ao carregar itens.");
        }
        req.setAttribute("csrf",       CsrfUtil.getOrCreate(req.getSession()));
        req.setAttribute("buscaVal",   busca);
        req.setAttribute("statusVal",  status);
        req.setAttribute("catVal",     categoria);
        req.getRequestDispatcher("/WEB-INF/views/admin/items.jsp").forward(req, res);
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
        String idStr  = req.getParameter("itemId");

        try {
            int id = Integer.parseInt(idStr);

            String act = action == null ? "" : action;
            if ("delete".equals(act)) {
                dao.delete(id);
            } else if ("devolver".equals(act)) {
                dao.updateStatus(id, "DEVOLVIDO");
            } else if ("edit".equals(act)) {
                Item item = dao.findById(id);
                if (item != null) {
                    String descricao = req.getParameter("descricao");
                    String categoria = req.getParameter("categoria");
                    String local     = req.getParameter("local_encontrado");
                    String dataStr   = req.getParameter("data_encontrado");
                    String statusVal = req.getParameter("status");
                    String obs       = req.getParameter("observacoes");

                    if (descricao != null && !descricao.trim().isEmpty()) item.setDescricao(descricao.trim());
                    if (categoria != null && !categoria.trim().isEmpty()) item.setCategoria(categoria.trim());
                    if (local     != null && !local.trim().isEmpty())     item.setLocalEncontrado(local.trim());
                    if (dataStr   != null && !dataStr.trim().isEmpty())   item.setDataEncontrado(LocalDate.parse(dataStr));
                    if (statusVal != null && !statusVal.trim().isEmpty()) item.setStatus(statusVal.trim());
                    if (obs       != null) item.setObservacoes(obs.trim());
                    dao.update(item);
                }
            }
            res.sendRedirect(req.getContextPath() + "/admin/items?ok=1");
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/admin/items?erro=invalid_id");
        } catch (Exception e) {
            getServletContext().log("Erro ao gerenciar item (admin)", e);
            res.sendRedirect(req.getContextPath() + "/admin/items?erro=server");
        }
    }
}

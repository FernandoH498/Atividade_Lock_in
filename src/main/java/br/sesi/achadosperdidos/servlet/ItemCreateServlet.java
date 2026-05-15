package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.model.Item;
import br.sesi.achadosperdidos.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@WebServlet("/itens/novo")
@MultipartConfig(
    maxFileSize    = 5  * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class ItemCreateServlet extends HttpServlet {

    private final ItemService service = new ItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Item item = new Item();
        item.setDescricao(req.getParameter("descricao"));
        item.setCategoria(req.getParameter("categoria"));
        item.setLocalEncontrado(req.getParameter("localEncontrado"));

        String obs = req.getParameter("observacoes");
        item.setObservacoes(obs != null && !obs.isBlank() ? obs.trim() : null);

        try {
            String dataStr = req.getParameter("dataEncontrado");
            if (dataStr != null && !dataStr.isBlank()) {
                item.setDataEncontrado(LocalDate.parse(dataStr));
            }

            Part fotoPart = req.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                String nomeOriginal = fotoPart.getSubmittedFileName();
                if (!service.isExtensaoImagemValida(nomeOriginal)) {
                    throw new IllegalArgumentException(
                        "Formato de imagem inválido. Use JPG, PNG, WEBP ou GIF.");
                }
                String ext      = nomeOriginal.substring(nomeOriginal.lastIndexOf('.'));
                String nomeArq  = UUID.randomUUID().toString() + ext;
                String uploadsDir = getServletContext().getRealPath("/uploads");
                new File(uploadsDir).mkdirs();
                fotoPart.write(uploadsDir + File.separator + nomeArq);
                item.setFotoPath(nomeArq);
            }

            service.cadastrarItem(item);
            resp.sendRedirect(req.getContextPath() + "/itens?sucesso=Item+cadastrado+com+sucesso!");

        } catch (DateTimeParseException e) {
            req.setAttribute("erro", "Formato de data inválido. Utilize o seletor de data.");
            forwardToForm(req, resp, item);
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            forwardToForm(req, resp, item);
        } catch (SQLException e) {
            req.setAttribute("erro", "Erro ao salvar o item. Tente novamente.");
            getServletContext().log("Erro ao cadastrar item", e);
            forwardToForm(req, resp, item);
        }
    }

    private void forwardToForm(HttpServletRequest req, HttpServletResponse resp, Item item)
            throws ServletException, IOException {
        req.setAttribute("item", item);
        req.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(req, resp);
    }
}

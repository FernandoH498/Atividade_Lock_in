package br.sesi.achadosperdidos.listener;

import br.sesi.achadosperdidos.service.UserService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private static final String ADMIN_USERNAME = "AdminFIH";
    private static final String ADMIN_PASSWORD = "WZ0X++H](\\8dl%!e5^!az}(~l8>{;lj5J<,ib-";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            UserService svc = new UserService();
            svc.criarAdminSeNaoExistir(ADMIN_USERNAME, ADMIN_PASSWORD);
        } catch (Exception e) {
            sce.getServletContext().log("ERRO ao criar conta admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}

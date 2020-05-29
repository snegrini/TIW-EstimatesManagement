package it.polimi.tiw.estimates.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.estimates.beans.Optional;
import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.ProductDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class HomeCustomer
 */
@WebServlet("/GoToHomeCustomer")
public class GoToHomeCustomer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHomeCustomer() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		User user = null;
		HttpSession s = request.getSession();
		user = (User) s.getAttribute("user");
		String chosenProduct = request.getParameter("productid");  //<----
		
		ProductDAO pDAO = new ProductDAO(connection);
		List<Product> products = null;
		List<Optional> optionals = null;  //<----
		int chosenProductId = 1;//<---
		try {
			products = pDAO.findProducts();
			
			if (chosenProduct == null) {
				//chosenProductId = pDAO.findDefaultProduct();
			} else {
				chosenProductId = Integer.parseInt(chosenProduct);
			}
			optionals = pDAO.findOptionalsByProduct(chosenProductId);	//<---
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in product's database extraction");
		}
		
		System.out.println("Optionals found: " + optionals.get(0).getName());
		String path = "/WEB-INF/HomeCustomer.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("products", products);
		ctx.setVariable("productid", chosenProductId); //<---
		ctx.setVariable("optionals", optionals);	//<---
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

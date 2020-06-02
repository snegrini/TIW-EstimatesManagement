package it.polimi.tiw.estimates.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.EstimateDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class AddEstimatePrice
 */
@WebServlet("/AddEstimatePrice")
public class AddEstimatePrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Connection connection;
	private TemplateEngine templateEngine;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddEstimatePrice() {
        super();
        // TODO Auto-generated constructor stub
    }
    
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = null;
		HttpSession s = request.getSession();
		u = (User) s.getAttribute("user");
		int userid = u.getId();
		
		String estimateidStr = request.getParameter("estimateid");
		String priceStr = request.getParameter("price");
		
		EstimateDAO eDAO = new EstimateDAO(connection, userid);
		
		String path;
		
		if (estimateidStr != null && priceStr != null) {
			
			try {
				int estimateid = Integer.parseInt(estimateidStr);
				float price = Float.parseFloat(priceStr);
				
				if (price < 0.f) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Price cannot be negative!");				
				} else {
					eDAO.addEstimatePrice(estimateid, price);
					path = "/HomeEmployee";
					response.sendRedirect(request.getContextPath() + path);
				}
			} catch (NumberFormatException | SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve estimate details");
			}
		}
		
	}

}

package it.polimi.tiw.estimates.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = null;
		HttpSession s = request.getSession();
		u = (User) s.getAttribute("user");
		int userid = u.getId();
		
		String estimateidStr = request.getParameter("estimateid");
		String priceStr = request.getParameter("price");
		
		EstimateDAO eDAO = new EstimateDAO(connection);
		
		String path;
		
		if (estimateidStr != null && priceStr != null) {
			
			try {
				int estimateid = Integer.parseInt(estimateidStr);
				float price = Float.parseFloat(priceStr);
				
				if (price < 0.f) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Price cannot be negative!");				
				} else {
					eDAO.addEstimatePrice(userid, estimateid, price);
					path = "/HomeEmployee?estimateid=" + estimateid;
					response.sendRedirect(request.getContextPath() + path);
				}
			} catch (NumberFormatException | SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve estimate details");
			}
		}	
	}
	
	@Override
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

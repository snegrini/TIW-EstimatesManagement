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
 * Servlet implementation class CreatePriceQuotation
 */
@WebServlet("/CreateEstimate")
public class CreateEstimate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private Connection connection;
	private TemplateEngine templateEngine;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateEstimate() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = null;
		HttpSession s = request.getSession();
		u = (User) s.getAttribute("user");
		int userid = u.getId();
		int lastId;
		
		String productName = request.getParameter("prdct");
		String[] options = request.getParameterValues("option[]");
		
		if (options == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No optionals have been selected.");
			return;
		} else {
			if (productName == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing product name");
				return;
			}

			EstimateDAO eDAO = new EstimateDAO(connection);
			
			try {
				lastId = eDAO.createEstimate(userid, Integer.parseInt(productName), options);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure of price quotation creation in database");
				return;
			}
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/HomeCustomer?estimateid=" + lastId;
		response.sendRedirect(path);
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

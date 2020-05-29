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
import it.polimi.tiw.estimates.daos.UserDAO;
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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User u = null;
		HttpSession s = request.getSession();
		u = (User) s.getAttribute("user");
		String productName = request.getParameter("prdct");
		String optionalName = request.getParameter("option");
		System.out.println("prdct:"+ productName + "option:"+optionalName);
		//TODO: Leggere gli optional facoltativi
		int optionals[] = new int[5];
		
		if (productName == null || optionalName == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing project name");
		}
		int userid = u.getId();
		
		EstimateDAO eDAO = new EstimateDAO(connection, userid);
		optionals[0] = Integer.parseInt(optionalName);
		
		try {
			eDAO.createEstimate(Integer.parseInt(productName), optionals);
		} catch (SQLException e) {
		// throw new ServletException(e);
		response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure of price quotation creation in database");
		}
		// debugged on April 20, 2020
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToHomeUser";
		response.sendRedirect(path);
		doGet(request, response);
	}

}

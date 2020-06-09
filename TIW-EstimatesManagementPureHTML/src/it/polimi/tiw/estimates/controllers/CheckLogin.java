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

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.UserDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
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

		String usr = null;
		String pwd = null;
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());

		try {
			usr = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pwd = StringEscapeUtils.escapeJava(request.getParameter("password"));
		} catch (NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing param values");
			return;
		}
		
		if (usr == null || pwd == null) {
			webContext.setVariable("errorMsg", "Username and password cannot be null");
		} 
		else if (usr.isEmpty() || pwd.isEmpty()) {
			webContext.setVariable("errorMsg", "Username and password cannot be empty");
		} 

		// Query the database to authenticate the user
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.checkCredentials(usr, pwd);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not check credentials");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		String path;
		if (user == null) {
			webContext.setVariable("errorMsg", "Incorrect username or password");
				
			path = "/index.html";
			templateEngine.process(path, webContext, response.getWriter());
		} else {
			path = getServletContext().getContextPath();
			request.getSession().setAttribute("user", user);
			String target = (user.getRole().equals("employee")) ? "/HomeEmployee" : "/HomeCustomer";
			path += target;

			response.sendRedirect(path);
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

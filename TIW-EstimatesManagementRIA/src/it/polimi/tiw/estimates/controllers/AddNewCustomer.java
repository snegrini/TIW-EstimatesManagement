package it.polimi.tiw.estimates.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.estimates.daos.UserDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class AddNewCustomer
 */
@WebServlet("/AddNewCustomer")
@MultipartConfig
public class AddNewCustomer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewCustomer() {
        super();
    }
    
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		
		UserDAO uDAO = new UserDAO(connection);
		
		if (username != null && email != null && password != null && name != null && surname != null) {
			
			try {
				if (uDAO.addUser(username, email, password, name, surname)) {
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().println("Customer successfully registered!");
				}
				else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Username or email already exists!");
				}

			} catch (NumberFormatException | SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Failed to insert new user");
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("You must fill all the fields!");
		}
	}

}

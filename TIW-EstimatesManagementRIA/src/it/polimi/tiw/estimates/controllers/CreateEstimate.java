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

			EstimateDAO eDAO = new EstimateDAO(connection, userid);
			
			try {
				eDAO.createEstimate(Integer.parseInt(productName), options);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure of price quotation creation in database");
				return;
			}
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/HomeCustomer";
		response.sendRedirect(path);
	}

}

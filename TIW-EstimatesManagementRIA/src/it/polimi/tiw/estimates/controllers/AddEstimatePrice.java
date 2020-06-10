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
import javax.servlet.http.HttpSession;

import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.EstimateDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class AddEstimatePrice
 */
@WebServlet("/AddEstimatePrice")
@MultipartConfig
public class AddEstimatePrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Connection connection;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddEstimatePrice() {
        super();
    }
    
    @Override
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
				
				if (price <= 0f) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Price cannot be negative!");
					return;
				} else {
					eDAO.addEstimatePrice(userid, estimateid, price);
					path = "/GetPricedEstimates"; // dopo aver aggiunto il prezzo, reindirizza a GetPricedEstimates che ritorna la nuova tabella (VA TESTATO!!)
					response.sendRedirect(request.getContextPath() + path);
				}
			} catch (NumberFormatException | SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Failed to retrieve estimate details");
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Estimate id or price cannot be null");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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

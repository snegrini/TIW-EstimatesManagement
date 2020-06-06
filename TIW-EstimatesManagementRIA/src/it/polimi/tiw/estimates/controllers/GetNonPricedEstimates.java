package it.polimi.tiw.estimates.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.estimates.beans.Estimate;
import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.EstimateDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class GetNonPricedEstimates
 */
@WebServlet("/GetNonPricedEstimates")
@MultipartConfig
public class GetNonPricedEstimates extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetNonPricedEstimates() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		EstimateDAO eDAO = new EstimateDAO(connection);
		List<Estimate> estimates = new ArrayList<Estimate>();
		
		try {
			estimates = eDAO.findNonPricedEstimates();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover non priced estimates");
		}
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(estimates);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
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

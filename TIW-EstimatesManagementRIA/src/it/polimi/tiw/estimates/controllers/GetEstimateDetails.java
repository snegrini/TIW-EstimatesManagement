package it.polimi.tiw.estimates.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import it.polimi.tiw.estimates.beans.Optional;
import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.EstimateDAO;
import it.polimi.tiw.estimates.daos.OptionalDAO;
import it.polimi.tiw.estimates.daos.ProductDAO;
import it.polimi.tiw.estimates.daos.UserDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class GetProductDetails
 */
@WebServlet("/GetEstimateDetails")
@MultipartConfig
public class GetEstimateDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetEstimateDetails() {
        super();
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
		
		String estimateId = request.getParameter("estimateid");
		
		EstimateDAO eDAO = new EstimateDAO(connection);
		ProductDAO pDAO = new ProductDAO(connection);
		OptionalDAO oDAO = new OptionalDAO(connection);
		UserDAO uDAO = new UserDAO(connection);

		Estimate estimate = null;
		Product product = null;
		List<Optional> optionals = null;
		User employee = null;
		
		try {
			estimate = eDAO.findEstimateByIdAndCustomer(Integer.parseInt(estimateId), user.getId());
			product = pDAO.findProductByEstimate(estimate.getId());
			optionals = oDAO.findChosenOptionalsByEstimate(estimate.getId());
			employee = uDAO.findEmployeeByEstimate(estimate.getId());
			
			product.setOptionals(optionals);
			estimate.setProduct(product);
			estimate.setEmployee(employee);
			
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad estimate id!");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover estimate details!");
			return;
		}
		

		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(estimate);
		
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

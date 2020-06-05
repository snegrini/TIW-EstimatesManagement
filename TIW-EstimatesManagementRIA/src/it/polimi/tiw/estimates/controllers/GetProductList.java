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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.daos.OptionalDAO;
import it.polimi.tiw.estimates.daos.ProductDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class GetProductsList
 */
@WebServlet("/GetProductList")
@MultipartConfig
public class GetProductList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProductList() {
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
		ProductDAO pDAO = new ProductDAO(connection);
		OptionalDAO oDAO = new OptionalDAO(connection);
		List<Product> products = new ArrayList<Product>();
		
		try {
			products = pDAO.findProducts();
			
			for(Product product : products) {
				product.setOptionals(oDAO.findAvailableOptionalsByProduct(product.getId()));
			}
						
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover products list");
			return;
		}
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(products);
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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

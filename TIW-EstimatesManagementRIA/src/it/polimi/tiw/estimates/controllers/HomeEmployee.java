package it.polimi.tiw.estimates.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.estimates.beans.Estimate;
import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.EstimateDAO;
import it.polimi.tiw.estimates.daos.ProductDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class HomeCustomer
 */
@WebServlet("/HomeEmployee")
public class HomeEmployee extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeEmployee() {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		
		EstimateDAO estimateDao = new EstimateDAO(connection, user.getId());
		List<Estimate> pricedEstimates = null;
		List<Estimate> nonPricedEstimates = null;
		
		ProductDAO productDao = new ProductDAO(connection);
		List<Product> pricedProducts = null;
		List<Product> nonPricedProducts = null;
		
		try {
			pricedEstimates = estimateDao.findPricedEstimatesByEmployee(user.getId());
			pricedProducts = productDao.findPricedProductsByEmployee(user.getId());
			
			nonPricedEstimates = estimateDao.findNonPricedEstimates();
			nonPricedProducts = productDao.findNonPricedProducts();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in estimates database extraction");
		}
		
		
		String path = "/WEB-INF/HomeEmployee.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		ctx.setVariable("pricedEstimates", pricedEstimates);
		ctx.setVariable("pricedProducts", pricedProducts);
		ctx.setVariable("nonPricedEstimates", nonPricedEstimates);
		ctx.setVariable("nonPricedProducts", nonPricedProducts);
		// TODO set default estimate to be selected
		
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

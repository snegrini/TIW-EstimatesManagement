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
import it.polimi.tiw.estimates.beans.Optional;
import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.beans.User;
import it.polimi.tiw.estimates.daos.EstimateDAO;
import it.polimi.tiw.estimates.daos.OptionalDAO;
import it.polimi.tiw.estimates.daos.ProductDAO;
import it.polimi.tiw.estimates.daos.UserDAO;
import it.polimi.tiw.estimates.utils.ConnectionHandler;

/**
 * Servlet implementation class CreatePriceEstimate
 */
@WebServlet("/CreateEstimatePrice")
public class CreateEstimatePrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Connection connection;
	private TemplateEngine templateEngine;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateEstimatePrice() {
        super();
    }  
    
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		
		String chosenEstimate = request.getParameter("estimateid");

		EstimateDAO estimateDao = new EstimateDAO(connection, user.getId());
		ProductDAO productDao = new ProductDAO(connection);
		OptionalDAO optionalDao = new OptionalDAO(connection);
		UserDAO userDao = new UserDAO(connection);
		
		Estimate estimate = null;
		Product product = null;
		List<Optional> optionals = null;
		User customer = null;
		
		int chosenEstimateId = 0;
		
		try {
			if (chosenEstimate == null) {
				// TODO chosenEstimateId = estimateDao.findDefaultEstimate();
			} else {
				chosenEstimateId = Integer.parseInt(chosenEstimate);
			}
			
			estimate = estimateDao.findEstimateById(chosenEstimateId);
			product = productDao.findProductById(estimate.getProductId());
			optionals = optionalDao.findChosenOptionalsByEstimate(estimate.getId());
			customer = userDao.findUserById(estimate.getClientId());
			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve estimate details");
		}
		
		
		String path = "/WEB-INF/EstimatePrice.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		ctx.setVariable("estimate", estimate);
		ctx.setVariable("product", product);
		ctx.setVariable("optionals", optionals);
		ctx.setVariable("customer", customer);
		
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*User u = null;
		HttpSession s = request.getSession();
		u = (User) s.getAttribute("user");
		String orderID = request.getParameter("orderID");
		
		//TODO: Leggere gli optional facoltativi
		
		if (orderID == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing project name");
			return;
		}
		int userid = u.getId();
		
		
		try {
		// do SQL
		} catch (SQLException e) {
		// throw new ServletException(e);
		response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure of price quotation creation in database");
		}
		
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/HomeEmployee";
		response.sendRedirect(path);
		doGet(request, response);*/
	}

}

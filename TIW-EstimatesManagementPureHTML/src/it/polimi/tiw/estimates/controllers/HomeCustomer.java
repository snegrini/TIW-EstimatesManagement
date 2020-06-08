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
 * Servlet implementation class HomeCustomer
 */
@WebServlet("/HomeCustomer")
public class HomeCustomer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeCustomer() {
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
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		String chosenEstimateId = request.getParameter("estimateid");
		String chosenProductId = request.getParameter("productid");
		
		EstimateDAO eDAO = new EstimateDAO(connection);
		List<Estimate> estimates = null;
		
		ProductDAO pDAO = new ProductDAO(connection);
		List<Product> products = null;
		
		OptionalDAO oDAO = new OptionalDAO(connection);
		List<Optional> optionals = null;
		
		UserDAO uDAO = new UserDAO(connection);
		User detailsEmployee = null;
		
		Product detailsProduct = null;
		List<Optional> detailsOptionals = null;

		Estimate chosenEstimate = null;
		Product chosenProduct = null;
		
		try {
			estimates = eDAO.findEstimatesByCustomer(user.getId());
			
			if (chosenEstimateId == null) {
				chosenEstimate = eDAO.findDefaultEstimateByCustomer(user.getId());
			} else {
				chosenEstimate = eDAO.findEstimateById(Integer.parseInt(chosenEstimateId));
			}
			
			detailsEmployee  = uDAO.findUserById(chosenEstimate.getEmployeeId());
			detailsProduct   = pDAO.findProductById(chosenEstimate.getProductId());
			detailsOptionals = oDAO.findChosenOptionalsByEstimate(chosenEstimate.getId());
			
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Bad estimate number");
			return;
		} catch (NullPointerException | SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in estimate's database extraction");
			return;
		}
		
		try {
			products = pDAO.findProducts();
			
			if (chosenProductId == null) {
				chosenProduct = pDAO.findDefaultProduct();
			} else {
				chosenProduct = pDAO.findProductById(Integer.parseInt(chosenProductId));
			}
			
			optionals = oDAO.findAvailableOptionalsByProduct(chosenProduct.getId());
			
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Bad product number");
			return;
		} catch (NullPointerException | SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in product's database extraction");
			return;
		}
		
		String path = "/WEB-INF/HomeCustomer.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		ctx.setVariable("estimates", estimates);
		ctx.setVariable("selectedEstimate", chosenEstimate);
		ctx.setVariable("detailsEmployee", detailsEmployee);
		ctx.setVariable("detailsProduct", detailsProduct);
		ctx.setVariable("detailsOptionals", detailsOptionals);

		ctx.setVariable("products", products);
		ctx.setVariable("selectedProduct", chosenProduct);
		ctx.setVariable("optionals", optionals);
		
		templateEngine.process(path, ctx, response.getWriter());
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

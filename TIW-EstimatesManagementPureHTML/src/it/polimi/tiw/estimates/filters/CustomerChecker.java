package it.polimi.tiw.estimates.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.estimates.beans.User;

/**
 * Servlet Filter implementation class CustomerChecker
 */
@WebFilter("/CustomerChecker")
public class CustomerChecker implements Filter {

    /**
     * Default constructor. 
     */
    public CustomerChecker() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String indexPath = req.getServletContext().getContextPath() + "/index.html";
		// check if the client is an admin
		HttpSession s = req.getSession();
		User u = null;
		u = (User) s.getAttribute("user");
		if (u == null || !u.getRole().equals("customer")) {
			res.sendRedirect(indexPath);
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

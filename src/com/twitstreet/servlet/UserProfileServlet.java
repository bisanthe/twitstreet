package com.twitstreet.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twitstreet.db.data.User;
import com.twitstreet.session.UserMgr;

@SuppressWarnings("serial")
@Singleton
public class UserProfileServlet extends HttpServlet {
	@Inject UserMgr userMgr;

	@Inject
	private final Gson gson = null;
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		response.setContentType("application/json;charset=utf-8");

		User user = (User) request.getSession().getAttribute(User.USER);
		
		String userIdStr = request.getParameter("user");
		boolean isAjaxRequest = false;
		try{
			isAjaxRequest = "true".equalsIgnoreCase(request.getParameter("isAjaxRequest"));
		}catch(Exception ex){
			
		}
		
				
		if(userIdStr == null || userIdStr.length() == 0){
			response.sendRedirect(response.encodeRedirectURL("/"));
			return;
		}
		
		
		
		User userObj = userMgr.getUserById(Long.parseLong(userIdStr));
		request.setAttribute("user", userObj);
		
		request.setAttribute("title", "User profile of " + userObj.getUserName());
		request.setAttribute("meta-desc", "This page shows profile of a "+userObj.getUserName()+". You can find details of "+userObj.getUserName()+" like rank, portfolio, cash and portfolio details.");
		
		if (user != null) {
			getServletContext().getRequestDispatcher("/WEB-INF/jsp/userProfile.jsp").forward(request, response);
		} else {
			getServletContext().getRequestDispatcher("/WEB-INF/jsp/userProfile.jsp").forward(request, response);
		}

	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}
}

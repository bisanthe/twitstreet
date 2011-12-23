package com.twitstreet.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twitstreet.db.data.Portfolio;
import com.twitstreet.db.data.User;
import com.twitstreet.market.PortfolioMgr;

@Singleton
public class PortfolioServlet extends HttpServlet {
	@Inject private final Gson gson = null;
	@Inject private final PortfolioMgr portfolioMgr = null;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		response.setContentType("application/json;charset=utf-8");
		String userIdStr = (String) request.getAttribute("user");
		if(userIdStr == null){
			userIdStr = (String) request.getSession(false).getAttribute(User.USER);
		}
		
		if(userIdStr != null){
			long userId = Long.parseLong(userIdStr);
			Portfolio portfolio = portfolioMgr.getUserPortfolio(userId);
			response.getWriter().write(gson.toJson(portfolio));
		}
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doPost(request, response);
	}
	
}
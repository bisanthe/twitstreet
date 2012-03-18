<%@page import="com.twitstreet.session.UserMgr"%>
<%@page import="com.google.inject.Injector"%>
<%@page import="com.twitstreet.servlet.GetUserServlet"%>
<%@page import="com.twitstreet.servlet.GetQuoteServlet"%>
<%@page import="com.twitstreet.servlet.HomePageServlet"%>
<%@ page import="com.twitstreet.localization.LocalizationUtil"%>
<%@ page import="com.twitstreet.db.data.User"%>
<%

	LocalizationUtil lutil = LocalizationUtil.getInstance();
	String lang = (String)request.getSession().getAttribute(LocalizationUtil.LANGUAGE);

	Injector inj = (Injector) pageContext.getServletContext().getAttribute(Injector.class.getName());
	UserMgr userMgr = inj.getInstance(UserMgr.class);
	User user = null;
	user = (user == null) ? (User) request.getAttribute(GetUserServlet.GET_USER) : user;

	String parameterUser = request.getParameter(User.USER);
	user = (user == null && parameterUser != null) ? userMgr.getUserById(Long.valueOf(parameterUser)) : user;
	
%>
<div class="main-tabs" style="width: 100%;">
	<div class="title-bar">
		<a class="top-grossing-users-tab <%=(user!=null)?"": "youarehere"%> "
			onclick="showTopGrossingUsersContent(); loadTopGrossingUsers();">
			<%=lutil.get("topgrossingusers.header", lang)%> </a>
		<a class="user-details-tab <%=(user==null)?"": "youarehere"%>"
			onclick="showUserDetailsContent(); reloadUserProfile();">
			 <%=(user==null)?"": "\""+user.getUserName()+"\""%> 
			<%=lutil.get("userdetails", lang)%> </a>
	</div>
</div>
<%@page import="com.twitstreet.servlet.SeasonServlet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.twitstreet.localization.LocalizationUtil"%>
<%@ page import="com.google.inject.Injector"%>
<%@ page import="com.twitstreet.db.data.User"%>
<%@page import="com.twitstreet.session.UserMgr"%>
<%@ page import="com.twitstreet.servlet.UserProfileServlet"%>
<%@ page import="com.twitstreet.season.SeasonInfo"%>
<%@page import="java.util.Date"%>
<%@page import="com.twitstreet.db.data.RankingHistoryData"%>
<%@page import="com.twitstreet.db.data.RankingData"%>
<%@ page import="com.google.inject.Injector"%>
<%@ page import="com.twitstreet.db.data.User"%>
<%@page import="com.twitstreet.session.UserMgr"%>
<%@ page import="java.util.List"%>
<%@ page import="com.twitstreet.season.SeasonMgr" %>
<%@page import="org.apache.log4j.Logger" %>
<%
	long start = 0;
	long end = 0;
	start = System.currentTimeMillis();
	Logger logger = Logger.getLogger(this.getClass());
	
	LocalizationUtil lutil = LocalizationUtil.getInstance();
	String lang = (String)request.getSession().getAttribute(LocalizationUtil.LANGUAGE);

	Injector inj = (Injector) pageContext.getServletContext().getAttribute(Injector.class.getName());
	UserMgr userMgr = inj.getInstance(UserMgr.class);

	SeasonMgr seasonMgr = inj.getInstance(SeasonMgr.class);
	String width = request.getParameter("width");

	String chartName = (String)request.getAttribute("chartName"); 
	String parameterUser = request.getParameter(User.USER);
	User user = null;
	
	

	user = (user == null) ? (User) request.getAttribute(SeasonServlet.SEASON_HISTORY_USER) : user;
	user = (user == null) ? (User) request.getAttribute(UserProfileServlet.USER_PROFILE_USER) : user;
	user = (user == null && parameterUser != null) ? userMgr.getUserById(Long.valueOf(parameterUser)) : user;
	request.setAttribute(UserProfileServlet.USER_PROFILE_USER, user);
	RankingHistoryData rhd = null;
	
	ArrayList<SeasonInfo> siList = seasonMgr.getAllSeasons();
	SeasonInfo selectedSeason = (SeasonInfo) request.getAttribute("selectedSeason");
	SeasonInfo currentSeason = seasonMgr.getCurrentSeason();
	selectedSeason = (selectedSeason!=null)? selectedSeason:seasonMgr.getCurrentSeason();
	int id = -1;
	if(selectedSeason!=null){
		id = selectedSeason.getId();
	}
	
	
	
	if(user!=null && selectedSeason!=null){
		rhd = userMgr.getRankingHistoryForUser(user.getId(),selectedSeason.getId());
	}
	
	if (rhd != null && rhd.getRankingHistory().size() > 0) {
		if(selectedSeason.getId() == currentSeason.getId()){
			RankingData rd = new RankingData();
	
			double totalNow = user.getTotal();
			Date date = new Date();
			
			rd.setCash(user.getCash());
			rd.setPortfolio(user.getPortfolio());
			rd.setLoan(user.getLoan());
			rd.setLastUpdate(date);
			rd.setRank(user.getRank());
			rd.setTotal(totalNow);

			rhd.getRankingHistory().add(rd);
		}
		%>
		
	
		
		<div id="<%=chartName%>" style="height: 200px; width: <%=width%>px;"></div>
		<br>
		<script type="text/javascript">
			var dateArray = new Array();
			var valueArray = new Array();
			var rankArray = new Array();
			var userName = '<%=user.getUserName()%>';
			
			//Global variable
			rankTitle = '<%=lutil.get("balance.rank", lang)%>';
			<%
			for(RankingData rd : rhd.getRankingHistory()){
						out.write("dateArray.push(new Date(" + rd.getLastUpdate().getTime()+ "));\n");
						out.write("rankArray.push(" + rd.getRank() + ");\n");
						out.write("valueArray.push(" + rd.getTotal() + ");\n");
			}		
			%>
			drawUserValueHistory('#<%=chartName%>', dateArray, valueArray,rankArray, userName);
		</script>
	<%
	}else if(user!=null){%>
		<div align="center">
		<p>
		<%=lutil.get("user.noHistory", lang,user.getUserName()) %>
		</p>
		</div>
	<%			
	}
	%>
<%
end = System.currentTimeMillis();
logger.debug("userRankingHistory.jsp execution time: " + (end - start));
%>


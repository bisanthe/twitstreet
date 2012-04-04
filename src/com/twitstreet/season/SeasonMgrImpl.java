package com.twitstreet.season;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twitstreet.config.ConfigMgr;
import com.twitstreet.db.base.DBConstants;
import com.twitstreet.db.base.DBMgr;

@Singleton
public class SeasonMgrImpl implements SeasonMgr{
	private static Logger logger = Logger.getLogger(SeasonMgrImpl.class);
	private static String INSERT_UPDATE_SEASON_INFO = "insert into season_info (id, startTime, endTime, active, updateInProgress)  values (?,?,?,?,?) on duplicate key update startTime=?, endTime=?, active=?, updateInProgress=? ";
	private static String SELECT_FROM_SEASON_INFO = " select id, startTime, endTime, active, updateInProgress from season_info ";
	

	@Inject DBMgr dbMgr;

	private ArrayList<SeasonInfo> allSeasons = new ArrayList<SeasonInfo>();

	@Inject
	ConfigMgr configMgr;
	private SeasonInfo currentSeason;
	
	@Override
	public SeasonInfo getSeasonInfo(int id) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SeasonInfo siDO = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SELECT_FROM_SEASON_INFO
					+ " where id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				siDO = new SeasonInfo();
				siDO.getDataFromResultSet(rs);
			}

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return siDO;
	}

	@Override
	public ArrayList<SeasonInfo> getAllSeasons() {
		return allSeasons ;
	}

	@Override
	public ArrayList<SeasonInfo> loadAllSeasons() {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SeasonInfo siDO = null;
		ArrayList<SeasonInfo> siList = new ArrayList<SeasonInfo>();
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SELECT_FROM_SEASON_INFO
					+ " order by id desc");
			rs = ps.executeQuery();

			while (rs.next()) {
				siDO = new SeasonInfo();
				siDO.getDataFromResultSet(rs);
				siList.add(siDO);
			}

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return allSeasons = siList;
	}

	public void loadSeasonInfo() {
		try {
			loadAllSeasons();
			loadCurrentSeason();
		} catch (Exception ex) {
			logger.error("Error in getting season info", ex);
		}

	}

	@Override
	public SeasonInfo getCurrentSeason() {
		return currentSeason;

	}
	@Override
	public SeasonInfo loadCurrentSeason() {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SeasonInfo siDO = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SELECT_FROM_SEASON_INFO
					+ " where active = true");

			rs = ps.executeQuery();
			if (rs.next()) {
				siDO = new SeasonInfo();
				siDO.getDataFromResultSet(rs);
			}

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return currentSeason = siDO;

	}
	@Override
	public SeasonInfo setSeasonInfo(SeasonInfo si) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SeasonInfo siDO = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(INSERT_UPDATE_SEASON_INFO);
			ps.setInt(1, si.getId());
			ps.setTimestamp(2, si.getStartTime());
			ps.setTimestamp(3, si.getEndTime());
			ps.setBoolean(4, si.isActive());
			ps.setBoolean(5, si.isUpdateInProgress());
			ps.setTimestamp(6, si.getStartTime());
			ps.setTimestamp(7, si.getEndTime());
			ps.setBoolean(8, si.isActive());
			ps.setBoolean(9, si.isUpdateInProgress());
			ps.executeUpdate();
			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return siDO;
	}
	@Override
	public void newSeason() {
		Connection connection = null;
		CallableStatement cs = null;
		
		loadSeasonInfo();
		
		Date nowDate = new Date();
		long now = nowDate.getTime();
		long endTime = getCurrentSeason().getEndTime().getTime();
		if(endTime-now<0 || getCurrentSeason().isUpdateInProgress() || endTime-now > bufferToStart){
			return;
		}
		SeasonInfo current = getCurrentSeason();
		current.setUpdateInProgress(true);
		setSeasonInfo(current);
		
		try {
			connection = dbMgr.getConnection();
			cs = connection.prepareCall("{call new_season(?)}");
			cs.setDouble(1, configMgr.getInitialMoney());
			cs.execute();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + cs.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + cs.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, cs, null);
		}
		
		current.setActive(false);
		current.setUpdateInProgress(false);
		setSeasonInfo(current);
		
		loadSeasonInfo();
		
	}
}
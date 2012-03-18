package com.twitstreet.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.twitstreet.localization.LocalizationUtil;

public class Stock implements DataObjectIF {
	
	//If stock is not updated more than 10 minutes, update is required
	private static final int UPDATE_REQUIRED = 10 * 60 * 1000;

	public static int STOCK_OLDER_THAN_DAYS_AVAILABLE =  7;
	long id;
	String name;
	String longName;
	private String description;
	int total;
	double sold;
	String pictureUrl;
	String language;
	Date lastUpdate;
	int changePerHour;
	boolean changePerHourCalculated;
	boolean verified;
	boolean updateRequired = false;
	public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private Date createdAt;
    
    private boolean suspended;
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public double getSold() {
		return sold;
	}
	public void setSold(double sold) {
		this.sold = sold;
	}
	public int getAvailable(){
		return (int)(total * ( 1 - sold));
	}
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public int getChangePerHour() {
		return changePerHour;
	}
	public void setChangePerHour(int changePerHour) {
		this.changePerHour = changePerHour;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
		this.setName(rs.getString("name"));
		this.setLongName(rs.getString("longName"));
		this.setDescription(rs.getString("description"));
		this.setCreatedAt(rs.getDate("createdAt"));
		this.setLanguage(rs.getString("language"));
		this.setTotal(rs.getInt("total"));
		this.setSold(rs.getDouble("sold"));
		this.setPictureUrl(rs.getString("pictureUrl"));
		this.setLastUpdate(rs.getTimestamp("lastUpdate"));
		
		Integer changePerHour =	rs.getInt("changePerHour");
		
		if(rs.wasNull()){
			
			changePerHour = 0;
			changePerHourCalculated = false;
		}
		else{
			
			changePerHourCalculated = true;
		}
		this.setChangePerHour(changePerHour);
		this.setVerified(rs.getBoolean("verified"));
		
	}
	public boolean isChangePerHourCalculated() {
		return changePerHourCalculated;
	}
	public void setChangePerHourCalculated(boolean changePerHourCalculated) {
		this.changePerHourCalculated = changePerHourCalculated;
	}
	
	@Override
	public boolean equals(Object obj) {

		try {

			return id == ((Stock) obj).getId();
		} catch (Exception ex) {

		}

		return false;

	}
	public boolean isUpdateRequired() {
		return Calendar.getInstance().getTimeInMillis() - lastUpdate.getTime() > UPDATE_REQUIRED ? true : false;
	}
	public String getLongName() {
		return longName;
	}
	public void setLongName(String longName) {
		this.longName = longName;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		
		if(LocalizationUtil.getInstance().getLanguages().contains(language)){
		
			this.language = language;
		}
		else{
			this.language = LocalizationUtil.DEFAULT_LANGUAGE;
			
		}
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public String toString(){

		String str = "";

		str = "Id: " + str + getId() + "\n";
		str = "Name: " + str + getName() + "\n";
		str = "Long Name: " + str + getLongName() + "\n";
		str = "Follower Count: " + str + getTotal() + "\n";
		str = "Speed: " + str + getChangePerHour() + "\n";
		return str;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
	public String getCreatedAtStr() {
		return sdf.format(createdAt.getTime());
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public boolean isOldEnough() {
		return getCreatedAt().getTime()< (new Date()).getTime()- 1000 * 60 * 60 * 24 * STOCK_OLDER_THAN_DAYS_AVAILABLE;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	
}

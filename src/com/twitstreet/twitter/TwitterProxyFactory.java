package com.twitstreet.twitter;

import com.google.inject.assistedinject.Assisted;

public interface TwitterProxyFactory {
	public TwitterProxy create(@Assisted("oauthToken") String oauthToken,
			@Assisted("oauthTokenSecret") String oauthTokenSecret);
	
}

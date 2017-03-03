package com.vikas.NaiveBayes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterData {
	private final static String CONSUMER_KEY = "YTQwATRZZJ12M6lYEeEQw";
    private final static String CONSUMER_KEY_SECRET = "rNdnInD9Vmw83rM5iL2TbnUFGLRdg2pgMFAnb0f4E";
    String consumerKey = "UHdVkjwLGBQ9EU97QjekYPosI";
    String consumerSecret = "dR6gg7ksjPjc6rWYsktICZuLmhmuTXwaMabwYYft49F7h0ZuS7";
    String accessToken = "53817896-0ZRcHgAHK3uKg3lpcoK2A8DMiMN3nMsdsevuMEbvE";
    String accessTokenSecret = "KKlVgoINyWwcRNbNjc2NEn3Io4JpzI5Gts65fbbW7Pmdy";
    
    public ArrayList<String> search(String searchTerm) throws TwitterException, IOException{
		//Get tweets and return an array of them


    	String consumerKey = "UHdVkjwLGBQ9EU97QjekYPosI";
        String consumerSecret = "dR6gg7ksjPjc6rWYsktICZuLmhmuTXwaMabwYYft49F7h0ZuS7";
        String accessToken = "53817896-0ZRcHgAHK3uKg3lpcoK2A8DMiMN3nMsdsevuMEbvE";
        String accessTokenSecret = "KKlVgoINyWwcRNbNjc2NEn3Io4JpzI5Gts65fbbW7Pmdy";
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthAccessToken(accessToken);
        cb.setOAuthAccessTokenSecret(accessTokenSecret);
        cb.setHttpProxyHost("192.168.1.107");
       cb.setHttpProxyPort(3128);
       cb.setHttpProxyUser("mtan_201422");
       cb.setHttpProxyPassword("nhz5D3Gh");
       TwitterFactory twitterFactory = new TwitterFactory(cb.build());
       Twitter twitter = twitterFactory.getInstance();
       	//Search twitter        
    	Paging paging = new Paging(1, 200);
    	
        Query query = new Query(searchTerm);
        QueryResult result = twitter.search(query);
        ArrayList <String>text = new ArrayList<String>();
        for (Status status : result.getTweets()) {
        	text.add(status.getText());
            System.out.println(" @" + status.getUser().getScreenName() + ":" + status.getText());
        }//end for
        return text;
    }//end method
    
	public String[] retrieveTweets() throws TwitterException, IOException{
		//Get tweets and return an array of them

    	String consumerKey = "UHdVkjwLGBQ9EU97QjekYPosI";
        String consumerSecret = "dR6gg7ksjPjc6rWYsktICZuLmhmuTXwaMabwYYft49F7h0ZuS7";
        String accessToken = "53817896-0ZRcHgAHK3uKg3lpcoK2A8DMiMN3nMsdsevuMEbvE";
        String accessTokenSecret = "KKlVgoINyWwcRNbNjc2NEn3Io4JpzI5Gts65fbbW7Pmdy";
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthAccessToken(accessToken);
        cb.setOAuthAccessTokenSecret(accessTokenSecret);
     //   cb.setHttpProxyHost("192.168.1.107");
      // cb.setHttpProxyPort(3128);
     //  cb.setHttpProxyUser("mtan_201422");
       //cb.setHttpProxyPassword("nhz5D3Gh");
       TwitterFactory twitterFactory = new TwitterFactory(cb.build());
       Twitter twitter = twitterFactory.getInstance();
    	//Post to twitter
    	System.out.println("\nAnalyzing...");
    	
    	// Paging, The factory instance is re-useable and thread safe.
    	List <String> tweets = new ArrayList<String>();
        // requesting page 2, number of elements per page is 40
        Paging paging = new Paging(1, 200);
        ResponseList<twitter4j.Status> statuses = twitter.getHomeTimeline(paging);
        for (twitter4j.Status status : statuses) {
            //System.out.println(status.getUser().getScreenName() + ":" + status.getText());
            tweets.add(status.getText());
        }//end for
        
        String[] stockArr = new String[tweets.size()];
        stockArr = tweets.toArray(stockArr);
        return stockArr;
	}//end method
	
}

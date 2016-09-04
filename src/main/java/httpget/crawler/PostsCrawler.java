package httpget.crawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import facebook4j.FacebookException;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;

public class PostsCrawler {

	static String access_token = "EAARu8c3E95oBAHOK3zjdW9VF7ZB6yt51EHmgwmVhHvYYxkX1ZAhDvXVxFzfYLgFiJbFWXcDex3HD29WKyrK8rtdOdaq3jgNZCSUlorImWIBQFYWwxPZB2b03vjwo0vcxeabHgRBe2Ck1Gv3TQKQMP9axVF22YpIZD";
	static int limit = 100;
	
	@SuppressWarnings("deprecation")
	public static void crawlAllPosts(String page_name) throws FacebookException, IOException, JSONException
	{
		// Override system DNS setting with Google free DNS server
		System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		
		HttpClient httpclient = HttpClientBuilder.create().build();

		String url = "https://graph.facebook.com/"
				+ page_name
				+ "/posts?fields=id,created_time"
				+ "&limit=" + limit
				+ "&access_token=" + access_token;

		String res = "";
		do{
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			res = httpclient.execute(httpget, responseHandler);
			
			JSONObject jsonObject = new JSONObject(res);
			JSONArray data = jsonObject.getJSONArray("data");
			int index = 0;
			while(index<data.length())
			{
				JSONObject post = data.getJSONObject(index);
				String post_id = post.getString("id");
				System.out.println(post_id);
				CommentsCrawler.crawlComments(httpclient, post_id);
				System.out.println();
				index++;
			}
			
			if(!jsonObject.has("paging"))
			{
				System.out.println("Done get posts!");
				break;
			}
			
			JSONObject paging = jsonObject.getJSONObject("paging");
			if(!paging.has("next"))
			{
				System.out.println("Done get posts!");
				break;
			}
			
			url = paging.get("next").toString();
		} while(true);
		
		httpclient.getConnectionManager().shutdown();
	}
	
	@SuppressWarnings("deprecation")
	public static void crawlOnePost(String page_name, String post_id) throws FacebookException, IOException, JSONException
	{
		// Override system DNS setting with Google free DNS server
		System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		
		HttpClient httpclient = HttpClientBuilder.create().build();

		String url = "https://graph.facebook.com/"
				+ page_name
				+ "/posts?fields=id,created_time"
				+ "&limit=" + limit
				+ "&access_token=" + access_token;

		String res = "";
		boolean done = false;
		do{
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			res = httpclient.execute(httpget, responseHandler);
			
			JSONObject jsonObject = new JSONObject(res);
			JSONArray data = jsonObject.getJSONArray("data");
			int index = 0;
			while(index<data.length())
			{
				JSONObject post = data.getJSONObject(index);
				String complete_post_id = post.getString("id");
				System.out.println(complete_post_id);
				if(complete_post_id.contains(post_id))
				{
					CommentsCrawler.crawlComments(httpclient, complete_post_id);
					done = true;
					break;
				}
				index++;
			}
			
			if(!jsonObject.has("paging"))
			{
				System.out.println("Done get posts!");
				break;
			}
			
			JSONObject paging = jsonObject.getJSONObject("paging");
			if(!paging.has("next"))
			{
				System.out.println("Done get posts!");
				break;
			}
			
			url = paging.get("next").toString();
		} while(done);
		
		httpclient.getConnectionManager().shutdown();
	}
	
	private static void savePhones(String page_name, String post_id) throws IOException
	{
		FileWriter fw = new FileWriter("comments.txt");
		fw.write("=== " + new Date() + " ===\n");
		fw.write("page_name: " + page_name + "\n");
		fw.write("post_id: " + post_id + "\n");
		fw.write("*** phones ***\n");
		for(String phone : MyHashMap.mapPhones.keySet())
		{
			fw.write(phone + "\n");
		}
		fw.close();
	}
	
	private static void savePhones(String page_name) throws IOException
	{
		FileWriter fw = new FileWriter("comments.txt");
		fw.write("=== " + new Date() + " ===\n");
		fw.write("page_name: " + page_name + "\n");
		fw.write("*** phones ***\n");
		for(String phone : MyHashMap.mapPhones.keySet())
		{
			fw.write(phone + "\n");
		}
		fw.close();
	}

	public static void main(String[] args) throws FacebookException, IOException, JSONException 
	{
		// init
		MyHashMap.mapPhones = new HashMap<>();
		
		if (args.length==2 && args[1].length()>0)
		{
			System.out.println("Start crawling one post ....");
			String page_name = args[0];
			String post_id = args[1];
			crawlOnePost(page_name, post_id);
			savePhones(page_name, post_id);
			System.out.println("All done");
		} else if((args.length==1) || (args.length==2 && args[1].length()==0))
		{
			System.out.println("Start crawling all page ....");
			String page_name = args[0];
			crawlAllPosts(page_name);
			savePhones(page_name);
			System.out.println("All done");
		} else {
			System.out.println("Please add only 1 or 2 parameter into run.bat!");
			System.out.println("1: page_name");
			System.out.println("2: post_id");
		}
		
//		System.out.println("Start get data on page ....");
//		String page_name = "xadontreotuongverygood";
//		crawlOnePost(page_name, "962423387213952");
//		System.out.println("All done!");
	}
}
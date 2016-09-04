package httpget.crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import facebook4j.FacebookException;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;
import processor.PhoneNumber;

public class CommentsCrawler {

	static String access_token = "EAARu8c3E95oBAHOK3zjdW9VF7ZB6yt51EHmgwmVhHvYYxkX1ZAhDvXVxFzfYLgFiJbFWXcDex3HD29WKyrK8rtdOdaq3jgNZCSUlorImWIBQFYWwxPZB2b03vjwo0vcxeabHgRBe2Ck1Gv3TQKQMP9axVF22YpIZD";
	static int limit = 1000;
	
	public static void crawlComments(HttpClient httpclient, String postId) throws FacebookException, JSONException, ClientProtocolException, IOException
	{
		// Override system DNS setting with Google free DNS server
		System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		
		String url = "https://graph.facebook.com/"
				+ postId
				+ "/comments"
				+ "?access_token=" + access_token
				+ "&summary=" + "1"
				+ "&filter=" + "stream"
				+ "&limit=" + limit;
		
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
				JSONObject comment = data.getJSONObject(index);
				System.out.println(comment);
				ArrayList<String> phoneList = PhoneNumber.getListPhoneNumbers(comment.getString("message"));
				for(int i=0; i<phoneList.size(); i++)
				{
					MyHashMap.mapPhones.put(phoneList.get(i), 0);
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
				System.out.println("Done get comments!");
				break;
			}
			
			url = paging.get("next").toString();
		} while(true);
	}

	public static void main(String[] args) throws FacebookException, JSONException, ClientProtocolException, IOException {
		
		System.out.println("Start getting data in post ...");
		String postId = "775765715879721_962423387213952";
		HttpClient httpclient = HttpClientBuilder.create().build();
		crawlComments(httpclient, postId);
		System.out.println("All done!");
	}
}

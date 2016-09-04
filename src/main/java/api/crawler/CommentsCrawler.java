package api.crawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.RawAPIResponse;
import facebook4j.conf.ConfigurationBuilder;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;
import processor.PhoneNumber;

public class CommentsCrawler {

	static facebook4j.Facebook facebook;
	static String appId = "1247884725254042";
	static String appSecret = "aabb56864c14c6bf4efd79eb6abad5b7";
	static String accessToken = "EAARu8c3E95oBANe6j8ZATKb4kOsNETIlyXudsV1QZBc83TnzBL2Me0qgKNbGgBVfuPHZBJF9JHDbVIiY4Jb8UnyFJr94g2tadDZBZAmMGCXTJV4IuoHIhwplZA30QIlkETi79yCCx3gKrmZABZA6YyrrBTI1z2htCk0ZD";
	
	public static void crawlComments(String post_id) throws FacebookException, IOException, JSONException
	{
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();
		confBuilder.setDebugEnabled(true);

		confBuilder.setOAuthAppId(appId);
		confBuilder.setOAuthAppSecret(appSecret);
		confBuilder.setOAuthAccessToken(accessToken);
		
		confBuilder.setUseSSL(true);
		confBuilder.setJSONStoreEnabled(true);

		facebook4j.conf.Configuration configuration = confBuilder.build();

		FacebookFactory ff = new FacebookFactory(configuration);
		facebook = ff.getInstance();

		FileWriter fw = new FileWriter("comments.txt", true);
		int limit = 200;
		
		RawAPIResponse res = facebook.callGetAPI(
				post_id
				+ "/comments?fields=from,id,message,created_time"
				+ "&filter=stream"
				+ "&limit=" + limit
				+ "&access_token=" + accessToken);

		do{
			JSONObject jsonObject = res.asJSONObject();
			JSONArray data = jsonObject.getJSONArray("data");
			int index = 0;
			while(index<data.length())
			{
				JSONObject comment = data.getJSONObject(index);
				System.out.println(comment);
				ArrayList<String> phoneList = PhoneNumber.getListPhoneNumbers(comment.getString("message"));
				for(int i=0; i<phoneList.size(); i++)
				{
					fw.write("[" + phoneList.get(i) + "] " + comment + "\n");
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
			
			String next = paging.get("next").toString();
			String urlNext = next.replace("https://graph.facebook.com/v2.6/", "");
			res = facebook.callGetAPI(urlNext);
		} while(res!=null);
		
		fw.close();
	}

	public static void main(String[] args) throws FacebookException, IOException, JSONException {
		String post_id = "331230823580420_1063095360393959";
		crawlComments(post_id);
	}
}

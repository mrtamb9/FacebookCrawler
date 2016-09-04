package api.crawler;

import java.io.IOException;

import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.RawAPIResponse;
import facebook4j.conf.ConfigurationBuilder;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;

public class PostsCrawler {

	static facebook4j.Facebook facebook;
	static String appId = "1247884725254042";
	static String appSecret = "aabb56864c14c6bf4efd79eb6abad5b7";
//	static String accessToken = "EAARu8c3E95oBAEpHqrbfxL2bSTQXyDUFsW45kN5lZCY8CGdaavv1e4Ct2XePr0V6n5XENxdfHhHVmwCj8NxZBc2rBZBL39Bp1zpn3hdYmWZBQv20ioby3ruZAvNZCBHeFcCokn3Mr1Ei5GSqbbDA2pWsOriZBUBKx0ZD";
	static String accessToken = "EAARu8c3E95oBANZCUPpleBRZA28KycEYu0dKaAul5HLPYUX4iJ0AHnHvjPKbZC4DhJFga64logi24BuRCWZCglShhC8slpska2Vjse42TfFIA35YXrNa1J5hX3oZA8XPfiXRjuGd7RUfPvyKiXgPGTOBd2oQzZAKUZD";
	
	public static void crawlPosts(String page_name) throws FacebookException, IOException, JSONException
	{
		System.out.println("Start...");
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

		int limit = 100;
		
		RawAPIResponse res = facebook.callGetAPI(
				page_name
				+ "/posts?fields=id,created_time"
				+ "&limit=" + limit
				+ "&access_token=" + accessToken);

		do{
			JSONObject jsonObject = res.asJSONObject();
			JSONArray data = jsonObject.getJSONArray("data");
			int index = 0;
			while(index<data.length())
			{
				JSONObject post = data.getJSONObject(index);
				String post_id = post.getString("id");
				System.out.println(post_id);
				CommentsCrawler.crawlComments(post_id);
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
			
			String next = paging.get("next").toString();
			String urlNext = next.replace("https://graph.facebook.com/v2.6/", "");
			res = facebook.callGetAPI(urlNext);
		} while(res!=null);
	}

	public static void main(String[] args) throws FacebookException, IOException, JSONException {
		String page_name = "stingworld";
		crawlPosts(page_name);
	}
}
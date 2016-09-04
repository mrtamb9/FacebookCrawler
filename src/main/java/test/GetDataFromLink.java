package test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;

public class GetDataFromLink {

	static String access_token = "EAARu8c3E95oBAHOK3zjdW9VF7ZB6yt51EHmgwmVhHvYYxkX1ZAhDvXVxFzfYLgFiJbFWXcDex3HD29WKyrK8rtdOdaq3jgNZCSUlorImWIBQFYWwxPZB2b03vjwo0vcxeabHgRBe2Ck1Gv3TQKQMP9axVF22YpIZD";
	static int limit = 10;
	
	@SuppressWarnings("deprecation")
	private static void getData(String postId) throws ClientProtocolException, IOException, JSONException {
		// Override system DNS setting with Google free DNS server
		System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		HttpClient httpclient = HttpClientBuilder.create().build();

		String url = "https://graph.facebook.com/"
				+ postId
				+ "/comments"
				+ "?access_token=" + access_token
				+ "&summary=" + "1"
				+ "&filter=" + "stream"
				+ "&limit=" + limit;
		
		String res = "";
		do {
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			res = httpclient.execute(httpget, responseHandler);
			
			JSONObject jsonObject = new JSONObject(res);
			JSONArray data = jsonObject.getJSONArray("data");
			int index = 0;
			while (index < data.length()) {
				JSONObject comment = data.getJSONObject(index);
				System.out.println(comment);
				index++;
			}

			if (!jsonObject.has("paging")) {
				System.out.println("Done get posts!");
				break;
			}

			JSONObject paging = jsonObject.getJSONObject("paging");
			if (!paging.has("next")) {
				System.out.println("Done get comments!");
				break;
			}

			url = paging.get("next").toString();
		} while (true);

		httpclient.getConnectionManager().shutdown();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Getting data ....");
		String postId = "775765715879721_962423387213952";
		getData(postId);
	}
}

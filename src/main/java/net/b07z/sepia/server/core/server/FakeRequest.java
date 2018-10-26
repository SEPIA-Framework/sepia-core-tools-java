package net.b07z.sepia.server.core.server;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import net.b07z.sepia.server.core.data.Defaults;
import net.b07z.sepia.server.core.tools.Converters;
import net.b07z.sepia.server.core.users.Account;
import net.b07z.sepia.server.core.users.LocalTestAccount;

import spark.Request;

/**
 * Spark fake 'Request' to construct your own request for testing or anything else.
 * 
 * @author Florian Quirin, Daniel Naber
 *
 */
public class FakeRequest extends Request {
	private final Map<String, String> params;
	private Account fakeAccount;

	public FakeRequest(String... params) {
		this.params = new HashMap<>();
		for (String param : params) {
			String[] parts = param.split("=",2);
			this.params.put(parts[0], parts[1]);
		}
		fakeAccount = new LocalTestAccount();
	}
	public FakeRequest(Map<String, String> params) {
		this.params = params;
		fakeAccount = new LocalTestAccount();
	}
	public FakeRequest(JSONObject params) {
		this.params = Converters.json2HashMapStrStr(params);
		fakeAccount = new LocalTestAccount();
	}
	public FakeRequest(Account fakeAccount, String... params) {
		this(params);
		this.fakeAccount = fakeAccount;
	}
	
	/**
	 * Overwrite a certain parameter
	 * @param key
	 * @param value
	 */
	public void overwriteParameter(String key, String value){
		this.params.put(key, value);
	}
	
	/**
	 * Set a new account (e.g. LocalTestAccount) as this request's account.<br>
	 * Note: use attribute(Defaults.ACCOUNT_ATTR) to get account back.
	 */
	public void setAccount(Account newAccount){
		fakeAccount = newAccount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T attribute(String attribute) {
		if (attribute.equals(Defaults.ACCOUNT_ATTR)) {
			return (T) fakeAccount;
		}
		return super.attribute(attribute);
	}

	@Override
	public String headers(String header) {
		if (header.equalsIgnoreCase("Content-type")) {
			return "application/json";
		}
		return super.headers(header);
	}
	
	@Override
	public String contentType() {
		return "application/json";
	}

	@Override
	public String queryParams(String queryParam) {
		return params.get(queryParam);
	}

	@Override
	public String[] queryParamsValues(String queryParam) {
		return new String[]{params.get(queryParam)};
	}
}

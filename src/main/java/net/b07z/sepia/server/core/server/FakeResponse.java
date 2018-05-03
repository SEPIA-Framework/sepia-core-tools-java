package net.b07z.sepia.server.core.server;

import spark.Response;

/**
 * Fake spark 'Response' to construct your own response for testing or anything else.
 *  
 * @author Florian Quirin, Daniel Naber
 *
 */
public class FakeResponse extends Response {
	@Override
	public void status(int statusCode) {}

	@Override
	public void type(String contentType) {}
}

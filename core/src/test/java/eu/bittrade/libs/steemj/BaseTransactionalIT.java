/*
 *     This file is part of SteemJ (formerly known as 'Steem-Java-Api-Wrapper')
 * 
 *     SteemJ is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SteemJ is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SteemJ.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.bittrade.libs.steemj;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;

import eu.bittrade.libs.steemj.chain.SignedTransaction;
import eu.bittrade.libs.steemj.communication.HttpClientRequestInitializer;
import eu.bittrade.libs.steemj.protocol.AccountName;

/**
 * This class defines which tests should at least be performed for an operation
 * and prepares a transaction object so that it does not need to be created in
 * each sub test case.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public abstract class BaseTransactionalIT extends BaseIT {
    // Test settings can be set as -D Parameters in the argline of the failsave
    // plugin.
    protected static final String MODE_FIELD_NAME = "steemj.test.mode";
    protected static final String ENDPOINT_FIELD_NAME = "steemj.test.endpoint";
    protected static final String HTTP_MODE_IDENTIFIER = "http";
    protected static final String WEBSOCKET_MODE_IDENTIFIER = "websocket";
    protected static final String TESTNET_ENDPOINT_IDENTIFIER = "testnet";
    protected static final String STEEMNET_ENDPOINT_IDENTIFIER = "steem";
    // TestNet related constants:
    protected static final AccountName STEEMJ_ACCOUNT_NAME = new AccountName("steemj");
    protected static final AccountName DEZ_ACCOUNT_NAME = new AccountName("dez1337");
    protected static final String STEEMJ_PASSWORD = "P8N6sHEJu438dj4dwY9jx9c8deeKpPA6XWCr9aTC5SQ7MiCjMUm";
    protected static final String DEZ_PASSWORD = "P63u9CtiWtWMRU8k4m63Ahek7qRwPJpvwqAjNiQw7ZmDN1AQEen";
    // Allow to configure the mode and the endpoint as -D parameters during test
    // execution.
    protected static String TEST_MODE = System.getProperty(MODE_FIELD_NAME, null);
    protected static String TEST_ENDPOINT = System.getProperty(ENDPOINT_FIELD_NAME, null);

    protected static SignedTransaction signedTransaction;

    /**
     * Setup the test environment for transaction related tests.
     */
 

    /**
     * Create a new TestNet account as described in the TestNet main page
     * (https://testnet.steem.vc).
     * 
     * @param username
     *            The account to create.
     * @param password
     *            The password to set for the <code>username</code>.
     * @throws IOException
     *             In case something went wrong.
     * @throws GeneralSecurityException
     *             In case something went wrong.
     */
    private static void createTestNetAccount(String username, String password)
            throws IOException, GeneralSecurityException {
        NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
        // Disable SSL verification:
        builder.doNotValidateCertificate();
        HttpRequest httpRequest = builder.build().createRequestFactory(new HttpClientRequestInitializer())
                .buildPostRequest(new GenericUrl("https://testnet.steem.vc/create"), ByteArrayContent.fromString(
                        "application/x-www-form-urlencoded", "username=" + username + "&password=" + password));
        try {
            httpRequest.execute();
        } catch (HttpResponseException e) {
            if (e.getStatusCode() != 409) {
                LOGGER.info("Account already existed.");
            }
        }
    }
}

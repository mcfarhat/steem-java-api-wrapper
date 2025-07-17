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
package eu.bittrade.libs.steemj.plugins.apis.account.history.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.Test;

/**
 * This class contains all test connected to the
 * {@link eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockArgs
 * GetOpsInBlockArgs} object.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class GetOpsInBlockArgsTest {
    /**
     * Test if a {@link GetOpsInBlockArgs} object can be created successfully with a
     * valid, positive block number.
     */
    @Test
    public void testConstructorWithPositiveBlockNumber() {
        final long blockNum = 12345678L;
        final boolean onlyVirtual = true;

        GetOpsInBlockArgs getOpsInBlockArgs = new GetOpsInBlockArgs(blockNum, onlyVirtual);

        assertThat("The object should be successfully created.", getOpsInBlockArgs, notNullValue());
        assertThat("Expect that the block number has been set correctly.", getOpsInBlockArgs.getBlockNum(),
                equalTo(blockNum));
        assertThat("Expect that the onlyVirtual flag has been set correctly.", getOpsInBlockArgs.getOnlyVirtual(),
                equalTo(onlyVirtual));
    }

    /**
     * Test that the constructor, as implemented in the provided core file, accepts
     * zero and negative block numbers without throwing an exception. This test
     * verifies the actual behavior of the existing code.
     */
    @Test
    public void testConstructorAcceptsTechnicallyInvalidBlockNumbers() {
        // Test with block number 0
        final long blockNumZero = 0L;
        GetOpsInBlockArgs argsZero = new GetOpsInBlockArgs(blockNumZero, false);
        assertThat("The object should be created successfully with block number 0.", argsZero, notNullValue());
        assertThat("The block number should be set to 0.", argsZero.getBlockNum(), equalTo(blockNumZero));

        // Test with a negative block number
        final long blockNumNegative = -100L;
        GetOpsInBlockArgs argsNegative = new GetOpsInBlockArgs(blockNumNegative, true);
        assertThat("The object should be created successfully with a negative block number.", argsNegative,
                notNullValue());
        assertThat("The block number should be set to -100.", argsNegative.getBlockNum(), equalTo(blockNumNegative));
    }
}
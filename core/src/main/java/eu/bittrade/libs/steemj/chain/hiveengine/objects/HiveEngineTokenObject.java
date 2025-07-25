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
package eu.bittrade.libs.steemj.chain.hiveengine.objects; // Note the new package

/**
 * Represents a Token object from the Hive-Engine sidechain. This is the Hive
 * ecosystem's replacement for Steem's SMTs.
 *
 * Data is retrieved from the 'tokens' contract, 'tokens' table on a
 * Hive-Engine node. The JSON parser will need to map the API response fields
 * to these Java fields.
 */
public class HiveEngineTokenObject {

    public String issuer;
    public String symbol;
    public String name;
    public String metadata; // This is a JSON string containing url, icon, desc etc.
    public int precision;

    // These are often very large numbers, so String is the safest type.
    public String maxSupply;
    public String supply;
    public String circulatingSupply;

    public boolean stakingEnabled;
    public int unstakingCooldown; // in days
    public boolean delegationEnabled;
    public int undelegationCooldown; // in days

    /**
     * Default constructor. Required for some deserialization libraries.
     */
    public HiveEngineTokenObject() { }
}
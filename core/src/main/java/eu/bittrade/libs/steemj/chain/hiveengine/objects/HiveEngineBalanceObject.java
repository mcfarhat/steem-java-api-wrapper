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
 * Represents an account's balance for a specific Hive-Engine token. This
 * replaces the SMT AccountBalanceObject.
 *
 * Data is retrieved from the 'tokens' contract, 'balances' table on a
 * Hive-Engine node. The JSON parser will need to map the API response fields
 * to these Java fields.
 */
public class HiveEngineBalanceObject {

    public String account;
    public String symbol;

    // Using String for balances is safest to avoid precision issues with large
    // numbers. Can be converted to BigDecimal for calculations.
    public String balance;
    public String stake;
    public String pendingUnstake;
    public String delegationsIn;
    public String delegationsOut;
    
    /**
     * Default constructor. Required for some deserialization libraries.
     */
    public HiveEngineBalanceObject() { }
}
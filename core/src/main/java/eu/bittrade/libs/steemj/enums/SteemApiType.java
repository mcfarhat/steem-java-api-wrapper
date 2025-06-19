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
package eu.bittrade.libs.steemj.enums;

/**
 * An enumeration for all existing APIs.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public enum SteemApiType {
     /** The "account_by_key_api" */
    ACCOUNT_BY_KEY_API,
    /** The "account_history_api" */
    ACCOUNT_HISTORY_API,
    /** The "block_api" for retrieving full blocks. */
    BLOCK_API,
    /** The "condenser_api" provides many legacy calls. */
    CONDENSER_API,
    /** The low-level "database_api". */
    DATABASE_API,
    /** The legacy "follow_api". */
    FOLLOW_API,
    /** The "market_history_api" for internal market data. */
    MARKET_HISTORY_API,
    /** The "network_broadcast_api" for sending transactions. */
    NETWORK_BROADCAST_API,
    /** The legacy "tags_api". */
    TAGS_API,
    /** The "witness_api" */
    WITNESS_API,

    // ###########################################################
    // # NEW APIS ADDED IN HIVE, NOT PRESENT IN STEEM              #
    // ###########################################################

    /** The modern API for all social data (posts, profiles, communities). */
    BRIDGE_API,
    /** The API for the Resource Credits (RC) system. */
    RC_API,
    /** The API to check the status of a broadcasted transaction. */
    TRANSACTION_STATUS_API,
    /** The API to simulate pending reward claims. */
    REWARDS_API

    /*
     * Note: DEBUG_NODE_API and CHAIN_API have been removed as they are
     * not enabled on public-facing RPC nodes and are not intended for
     * client-side application use.
     */
}


// done
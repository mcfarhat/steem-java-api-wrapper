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
package eu.bittrade.libs.steemj.protocol.enums;

/**
 * This enum stores all available asset symbols.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public enum LegacyAssetSymbolType {
    /** Steem Power (SP) Symbol */
    VESTS,
    /** Steem Sybol */
    STEEM,
    /** Steem Backed Dollar (SBD) Symbol */
    SBD,
    /** Steem Dollar Symbol */
    STMD,
    /** Steem Sybol for the test network */
    TESTS,
    /** Steem Backed Dollar Symbol for the test network */
    TBD,
    /** Steem Dollar Symbol for the test network */
    TSTD,
    // ---------- ADDED FOR HIVE SUPPORT ----------
    /** Hive Power (HP) Symbol - equivalent to VESTS conceptually */
    // VESTS is already present and typically used for Hive Power as well.
    // No separate HP symbol usually needed here if VESTS is used for HP.

    /** Hive Symbol */
    HIVE,
    /** Hive Backed Dollar (HBD) Symbol */
    HBD;
}

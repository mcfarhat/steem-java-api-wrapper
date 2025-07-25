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
 * @author Your Name Here (for the modifications)
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
    /** Hive Symbol */
    HIVE,
    /** Hive Backed Dollar (HBD) Symbol */
    HBD;

    // ### START OF HIVE FIX ###
    // These are the well-known NAI values for Hive's core assets.
    private static final String HIVE_NAI = "@@000000021";
    private static final String HBD_NAI = "@@000000013";
    private static final String VESTS_NAI = "@@000000037";

    /**
     * A utility method to determine the asset symbol from the NAI string and precision
     * provided by modern Hive APIs.
     * 
     * @param nai The NAI string (e.g., "@@000000021").
     * @param precision The precision of the asset.
     * @return The matching {@link LegacyAssetSymbolType}.
     * @throws IllegalArgumentException if the NAI is not recognized.
     */
    public static LegacyAssetSymbolType fromNai(String nai, int precision) {
        if (nai == null) {
            throw new IllegalArgumentException("NAI cannot be null.");
        }

        switch (nai) {
            case HIVE_NAI:
                // HIVE has a precision of 3
                if (precision == 3) return HIVE;
                break;
            case HBD_NAI:
                // HBD has a precision of 3
                if (precision == 3) return HBD;
                break;
            case VESTS_NAI:
                // VESTS (Hive Power) has a precision of 6
                if (precision == 6) return VESTS;
                break;
            default:
                // If we don't recognize the NAI, we fall through to the exception.
                break;
        }

        // If no match was found, throw an error.
        throw new IllegalArgumentException("Unknown NAI '" + nai + "' with precision " + precision);
    }
    // ### END OF HIVE FIX ###
}
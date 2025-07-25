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
 * An enumeration for all existing witness schedule types.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public enum WitnessScheduleType {
     /**
     * A witness in the top 20 who has a guaranteed block production slot each round.
     */
    TOP20,
    /**
     * A backup witness who shares a slot with other backups.
     */
    TIMESHARE,
    /**
     * A placeholder for an account that is not in the witness schedule.
     */
    NONE
    /*
     * Note: The MINER type has been removed as Hive is a pure DPoS
     * blockchain and does not have a proof-of-work miner queue. The
     * TOP19 type has been updated to TOP20 to reflect Hive's consensus.
     */
}
//done
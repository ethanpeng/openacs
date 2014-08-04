/*
 * 
 * Copyright 2007-2012 Audrius Valunas
 * 
 * This file is part of OpenACS.

 * OpenACS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenACS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenACS.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.openacs;

import java.sql.Timestamp;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface ATMErrorsStatsLocalHome extends EJBLocalHome {

    org.openacs.ATMErrorsStatsLocal findByPrimaryKey(org.openacs.ATMErrorsStatsPK key) throws FinderException;

    org.openacs.ATMErrorsStatsLocal create(Integer hostid, Timestamp time, int type) throws CreateException;

    org.openacs.ATMErrorsStatsLocal create(Integer hostid, Timestamp time, int type,
            Timestamp intervalStart,
            Long ATUCCRCErrors, Long ATUCFECErrors, Long ATUCHECErrors, Long CellDelin,
            Long CRCErrors, Long FECErrors, Long HECErrors, Long ErroredSecs,
            Long InitErrors, Long InitTimeouts, Long LinkRetrain, Long LossOfFraming,
            Long ReceiveBlocks, Long SeverelyErroredSecs, Long TransmitBlocks,
            Long LossOfPower, Long LossOfSignal) throws CreateException;

    Collection<ATMErrorsStatsLocal> findByTimeBeforeAndHost(Integer hostid, Timestamp tm) throws FinderException;

    ATMErrorsStatsLocal findByHostAndTime(Integer host, Timestamp time) throws FinderException;
}

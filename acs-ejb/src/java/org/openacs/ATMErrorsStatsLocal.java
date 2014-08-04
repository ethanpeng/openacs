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
import javax.ejb.EJBLocalObject;

public interface ATMErrorsStatsLocal extends EJBLocalObject {

    public Timestamp getTime();

    public Integer getHostid();

    public int getType();

    Timestamp getIntervalStart();

    void setIntervalStart(Timestamp intervalStart);
    static final int TYPE_TOTAL = 1;
    static final int TYPE_SHOWTIME = 2;
    static final int TYPE_CURRENTDAY = 3;
    static final int TYPE_QUARTERHOUR = 4;
    static final int TYPE_LASTSHOWTIME = 5;

    Long getATUCCRCErrors();

    void setATUCCRCErrors(Long ATUCCRCErrors);

    Long getATUCFECErrors();

    void setATUCFECErrors(Long ATUCFECErrors);

    Long getATUCHECErrors();

    void setATUCHECErrors(Long ATUCHECErrors);

    Long getCellDelin();

    void setCellDelin(Long CellDelin);

    Long getCRCErrors();

    void setCRCErrors(Long CRCErrors);

    Long getErroredSecs();

    void setErroredSecs(Long ErroredSecs);

    Long getFECErrors();

    void setFECErrors(Long FECErrors);

    Long getHECErrors();

    void setHECErrors(Long HECErrors);

    Long getInitErrors();

    void setInitErrors(Long InitErrors);

    Long getInitTimeouts();

    void setInitTimeouts(Long InitTimeouts);

    Long getLinkRetrain();

    void setLinkRetrain(Long LinkRetrain);

    Long getLossOfFraming();

    void setLossOfFraming(Long LossOfFraming);

    Long getReceiveBlocks();

    void setReceiveBlocks(Long ReceiveBlocks);

    Long getSeverelyErroredSecs();

    void setSeverelyErroredSecs(Long SeverelyErroredSecs);

    Long getTransmitBlocks();

    void setTransmitBlocks(Long TransmitBlocks);

    Long getLossOfPower();

    void setLossOfPower(Long LossOfPower);

    Long getLossOfSignal();

    void setLossOfSignal(Long LossOfSignal);
}

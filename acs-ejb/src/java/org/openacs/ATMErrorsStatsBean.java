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
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public abstract class ATMErrorsStatsBean implements EntityBean {

    private EntityContext context;

    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click on the + sign on the left to edit the code.">
    // TODO Consider creating Transfer Object to encapsulate data
    // TODO Review finder methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
    }

    /**
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() {
    }

    /**
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context = null;
    }

    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
    }

    // </editor-fold>
    public abstract Timestamp getTime();

    public abstract void setTime(Timestamp time);

    public abstract Integer getHostid();

    public abstract void setHostid(Integer hostid);

    public abstract int getType();

    public abstract void setType(int type);

    public org.openacs.ATMErrorsStatsPK ejbCreate(Integer hostid, Timestamp time, int type) throws CreateException {
        setHostid(hostid);
        setTime(time);
        setType(type);

        return null;
    }

    public org.openacs.ATMErrorsStatsPK ejbCreate(Integer hostid, Timestamp time, int type,
            Timestamp intervalStart,
            Long ATUCCRCErrors, Long ATUCFECErrors, Long ATUCHECErrors, Long CellDelin,
            Long CRCErrors, Long FECErrors, Long HECErrors, Long ErroredSecs,
            Long InitErrors, Long InitTimeouts, Long LinkRetrain, Long LossOfFraming,
            Long ReceiveBlocks, Long SeverelyErroredSecs, Long TransmitBlocks,
            Long LossOfPower, Long LossOfSignal) throws CreateException {
        setHostid(hostid);
        setTime(time);
        setType(type);

        setIntervalStart(intervalStart);
        setATUCCRCErrors(ATUCCRCErrors);
        setATUCFECErrors(ATUCFECErrors);
        setATUCHECErrors(ATUCHECErrors);
        setCellDelin(CellDelin);
        setCRCErrors(CRCErrors);
        setFECErrors(FECErrors);
        setHECErrors(HECErrors);
        setErroredSecs(ErroredSecs);
        setInitErrors(InitErrors);
        setInitTimeouts(InitTimeouts);
        setLinkRetrain(LinkRetrain);
        setLossOfFraming(LossOfFraming);
        setReceiveBlocks(ReceiveBlocks);
        setSeverelyErroredSecs(SeverelyErroredSecs);
        setTransmitBlocks(TransmitBlocks);
        setLossOfPower(LossOfPower);
        setLossOfSignal(LossOfSignal);

        return null;
    }

    public void ejbPostCreate(Integer hostid, Timestamp time, int type) {
        // TODO populate relationships here if appropriate
    }

    public void ejbPostCreate(Integer hostid, Timestamp time, int type,
            Timestamp intervalStart,
            Long ATUCCRCErrors, Long ATUCFECErrors, Long ATUCHECErrors, Long CellDelin,
            Long CRCErrors, Long FECErrors, Long HECErrors, Long ErroredSecs,
            Long InitErrors, Long InitTimeouts, Long LinkRetrain, Long LossOfFraming,
            Long ReceiveBlocks, Long SeverelyErroredSecs, Long TransmitBlocks,
            Long LossOfPower, Long LossOfSignal) {
    }

    public abstract Timestamp getIntervalStart();

    public abstract void setIntervalStart(Timestamp intervalStart);

    public abstract Long getATUCCRCErrors();

    public abstract void setATUCCRCErrors(Long ATUCCRCErrors);

    public abstract Long getATUCFECErrors();

    public abstract void setATUCFECErrors(Long ATUCFECErrors);

    public abstract Long getATUCHECErrors();

    public abstract void setATUCHECErrors(Long ATUCHECErrors);

    public abstract Long getCellDelin();

    public abstract void setCellDelin(Long CellDelin);

    public abstract Long getCRCErrors();

    public abstract void setCRCErrors(Long CRCErrors);

    public abstract Long getErroredSecs();

    public abstract void setErroredSecs(Long ErroredSecs);

    public abstract Long getFECErrors();

    public abstract void setFECErrors(Long FECErrors);

    public abstract Long getHECErrors();

    public abstract void setHECErrors(Long HECErrors);

    public abstract Long getInitErrors();

    public abstract void setInitErrors(Long InitErrors);

    public abstract Long getInitTimeouts();

    public abstract void setInitTimeouts(Long InitTimeouts);

    public abstract Long getLinkRetrain();

    public abstract void setLinkRetrain(Long LinkRetrain);

    public abstract Long getLossOfFraming();

    public abstract void setLossOfFraming(Long LossOfFraming);

    public abstract Long getReceiveBlocks();

    public abstract void setReceiveBlocks(Long ReceiveBlocks);

    public abstract Long getSeverelyErroredSecs();

    public abstract void setSeverelyErroredSecs(Long SeverelyErroredSecs);

    public abstract Long getTransmitBlocks();

    public abstract void setTransmitBlocks(Long TransmitBlocks);

    public abstract Long getLossOfPower();

    public abstract void setLossOfPower(Long LossOfPower);

    public abstract Long getLossOfSignal();

    public abstract void setLossOfSignal(Long LossOfSignal);
}

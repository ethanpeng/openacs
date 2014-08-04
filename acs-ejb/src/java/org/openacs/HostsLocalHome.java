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

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

/**
 * This is the local-home interface for Hosts enterprise bean.
 */
public interface HostsLocalHome extends EJBLocalHome {

    HostsLocal findByPrimaryKey(Object key) throws FinderException;

    public HostsLocal create(Integer hwid, String serialno, String url) throws CreateException;

    /**
     * 
     */
    Collection<HostsLocal> findBySerialno(String serialno) throws FinderException;

    /**
     * 
     */
    Collection findByUrl(String url) throws FinderException;

    Collection findAll() throws FinderException;

    Collection findByPartialSN(Integer hwid, String snprefix) throws FinderException;

    org.openacs.HostsLocal findByIp(java.lang.String ip) throws javax.ejb.FinderException;

    HostsLocal findByHwidAndSn(Integer hwid, String sn) throws FinderException;

    Collection findByIpM(String ip) throws FinderException;

    Collection<HostsLocal> findByCustomerId(String customerId) throws FinderException;
}

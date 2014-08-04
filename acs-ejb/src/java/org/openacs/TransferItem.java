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

import java.util.Calendar;
import java.util.Random;
import org.openacs.utils.cvtHex;

public class TransferItem {

    private static final long LIFE_TIME = 3600 * 1000; // lifetime in milis
    private long timeCreated;
    private String user;
    private String password;
    private Integer idHost;
    private int type;
    public final static int UPLOAD_LOG = 1;
    public final static int UPLOAD_CONFIG = 2;
    public final static int DOWNLOAD_CONFIG = 3;

    public TransferItem(int id, int type) {
        byte[] rb = new byte[8];
        Random r = new Random();
        r.nextBytes(rb);
        user = Integer.toHexString(id) + cvtHex.cvtHex(rb);
        r.nextBytes(rb);
        password = cvtHex.cvtHex(rb);
        this.type = type;
        idHost = id;
        timeCreated = Calendar.getInstance().getTimeInMillis();
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Integer getHostId() {
        return idHost;
    }

    public boolean isExpired() {
        return (Calendar.getInstance().getTimeInMillis() - timeCreated > LIFE_TIME);
    }
}

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
package org.openacs.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Subnet {

    private byte baddr[];
    private byte bmask[];

    public Subnet(String s) throws UnknownHostException {
        String a[] = s.split("/");
        if (a.length != 2) {
            throw new IndexOutOfBoundsException("No mask delimiter / found");
        }
        baddr = InetAddress.getByName(a[0]).getAddress();
        try {
            int maskbits = Integer.parseInt(a[1]);
            bmask = new byte[baddr.length];
            for (int i = 0; i < bmask.length; i++, maskbits -= 8) {
                if (maskbits >= 8) {
                    bmask[i] = (byte) 0xFF;
                } else if (maskbits <= 0) {
                    bmask[i] = 0;
                } else {
                    bmask[i] = (byte) (~(0xFF >> maskbits));
                }
            }
        } catch (NumberFormatException e) {
            bmask = InetAddress.getByName(a[1]).getAddress();
        }
        if (bmask.length != baddr.length) {
            throw new UnknownHostException("Bad subnet");
        }
    }

    public boolean isInSubnet(String s) {
        try {
            return isInSubnet(InetAddress.getByName(s));
        } catch (UnknownHostException ex) {
            return false;
        }
    }

    public boolean isInSubnet(InetAddress a) {
        byte ba[] = a.getAddress();
        if (ba.length != baddr.length) {
            return false;
        }
        for (int i = 0; i < ba.length; i++) {
            if ((ba[i] & bmask[i]) != (baddr[i] & bmask[i])) {
                return false;
            }
        }
        return true;
    }
}

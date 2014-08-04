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

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.openacs.web.Form;

public class Context implements ServletContextListener {

    public void contextInitialized(ServletContextEvent ctx) {
        Application.init(ctx);

        Set<String> formpathes = ctx.getServletContext().getResourcePaths("/WEB-INF/forms/");
        Form form = new Form();
        for (String path : formpathes) {
            File f = new File(path);
            String dmName = f.getName();
            System.out.println("PATH: " + path + " NAME: " + dmName);
            InputStream in = ctx.getServletContext().getResourceAsStream(path);
            form.Load(dmName.substring(0, dmName.length() - 4), in);
        }
    }

    public void contextDestroyed(ServletContextEvent ctx) {
    }
}
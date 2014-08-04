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
package org.openacs.web;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpSession;

public class PhaseListener implements javax.faces.event.PhaseListener {
    public static final String PHASE = "PHASE";

    public void afterPhase(PhaseEvent event) {
        reportPhase(event, true);
    }

    public void beforePhase(PhaseEvent event) {
        reportPhase(event, false);
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    private static final String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    private void DumpView (FacesContext ctx, UIComponent p, int lvl) {
        System.out.print (tabs.substring(0, lvl + 1));
        System.out.println (p.getClass().getName()+": "+p.getClientId(ctx)+" "+p.getId());
        for (UIComponent c : p.getChildren()) {
            DumpView(ctx, c, lvl + 1);
        }
    }

    private void DumpView (FacesContext ctx) {
        UIViewRoot r = ctx.getViewRoot();
        System.out.println ("ROOT children "+r.getChildCount());
        DumpView(ctx, r, 0);
    }

    private void reportPhase (PhaseEvent event, boolean after) {
        HttpSession session = (HttpSession) event.getFacesContext().getExternalContext().getSession(false);
        if (session != null) {
            if (after && event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
                session.removeAttribute(PHASE);
            } else {
                if (!after) session.setAttribute(PHASE, event.getPhaseId());
            }

        }

        if (!after && event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            //DumpView(event.getFacesContext());
        }
        //System.out.println ("**********************"+(after ? "AFTER " : "BEFORE ")+event.getPhaseId()+" session="+session);        System.out.flush();
    }
}

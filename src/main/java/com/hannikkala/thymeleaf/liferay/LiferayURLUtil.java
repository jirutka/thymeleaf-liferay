/*
 * The MIT License
 *
 * Copyright 2013 Jakub Jirutka <jakub@jirutka.cz>.
 * Originally developed by Tommi Hannikkala <tommi@hannikkala.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.hannikkala.thymeleaf.liferay;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLFactoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.liferay.portal.kernel.portlet.LiferayPortletMode.*;
import static com.liferay.portal.kernel.portlet.LiferayWindowState.*;
import static javax.portlet.PortletRequest.*;

/**
 * Util class to create Liferay portlet URLs.
 * 
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class LiferayURLUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LiferayURLUtil.class);

    private static final Map<String, PortletMode> PORTLET_MODES = toHashMap(
            VIEW, EDIT, HELP, ABOUT, CONFIG, EDIT_DEFAULTS, EDIT_GUEST, PREVIEW, PRINT);

    private static final Map<String, WindowState> WINDOW_STATES = toHashMap(
            NORMAL, MAXIMIZED, MINIMIZED, EXCLUSIVE, POP_UP);

    private static final Map<String, String> LIFECYCLE_PHASES = toHashMap(
            ACTION_PHASE, EVENT_PHASE, RENDER_PHASE, RESOURCE_PHASE);

    
    public static LiferayPortletURL createUrl(Map<String, Object> params, HttpServletRequest request) {

        long plid = parsePlid(params.remove("plid"), request);
        String portletName = getPortletName((String) params.remove("portletname"), request);
        String lifecycle = parseLifecycle((String) params.remove("lifecycle"));
        
        LiferayPortletURL portletURL = PortletURLFactoryUtil.create(request, portletName, plid, lifecycle);

        try {
            portletURL.setWindowState(parseWindowState((String) params.remove("windowState")));
        } catch (WindowStateException ex) {}

        try {
            portletURL.setPortletMode(parsePortletMode((String) params.remove("portletMode")));
        } catch (PortletModeException ex) {}

        if (params.containsKey("action")) {
            String value = params.get("action") != null ? params.remove("action").toString() : "";
            portletURL.setParameter("javax.portlet.action", value, false);
            portletURL.setLifecycle(ACTION_PHASE);
        }

        for(Entry<String, Object> entry : params.entrySet()) {
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            portletURL.setParameter(entry.getKey(), value, true);
        }
        return portletURL;
    }

    public static Map<String, Object> parseParams(Arguments arguments, String attributeValue) {

        AssignationSequence assignations = StandardExpressionProcessor.parseAssignationSequence(
                arguments, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException(String.format(
                    "Could not parse value as attribute assignations: '%s'", attributeValue));
        }
        Map<String, Object> newLocalVariables = new HashMap<String, Object>(assignations.size() + 1, 1.0f);

        for (Assignation assignation : assignations) {
            String varName = assignation.getLeft().getValue();
            Expression expression = assignation.getRight();
            Object varValue = StandardExpressionProcessor.executeExpression(arguments, expression);

            newLocalVariables.put(varName, varValue);
        }
        return newLocalVariables;
    }

    
    private static WindowState parseWindowState(String windowState) {
        if (windowState != null) {
            windowState = windowState.toLowerCase();

            if (WINDOW_STATES.containsKey(windowState)) {
                return WINDOW_STATES.get(windowState);
            }
            LOG.warn("No such Window State '{}', returning default one", windowState);
        }
        return WindowState.NORMAL;
    }

    private static PortletMode parsePortletMode(String portletMode) {
        if (portletMode != null) {
            portletMode = portletMode.toLowerCase();

            if (PORTLET_MODES.containsKey(portletMode)) {
                return PORTLET_MODES.get(portletMode);
            }
            LOG.warn("No such Portlet Mode '{}', returning default one", portletMode);
        }
        return PortletMode.VIEW;
    }

    private static String parseLifecycle(String lifecycle) {
        if (lifecycle != null) {
            lifecycle = lifecycle.toUpperCase();

            if (LIFECYCLE_PHASES.containsKey(lifecycle)) {
                return LIFECYCLE_PHASES.get(lifecycle);
            }
            LOG.warn("No such Lifecycle Phase '{}', returning default one", lifecycle);
        }
        return PortletRequest.RENDER_PHASE;
    }

    private static String getPortletName(String portletName, HttpServletRequest request) {
        if (portletName != null) {
            return portletName;
        }
        return (String) request.getAttribute(WebKeys.PORTLET_ID);
    }

    private static long parsePlid(Object plid, HttpServletRequest request) {
        if (plid != null) {
            try {
                return Long.parseLong(plid.toString());
            } catch (NumberFormatException ex) {
                LOG.warn("Couldn't parse plid value '{}' to long, returning default", plid);
            }
        }
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        Layout layout = themeDisplay.getLayout();

        return layout.getPlid();
    }

    private static <T> Map<String, T> toHashMap(T... values) {
        Map<String, T> map = new HashMap<String, T>(values.length +1, 1.0f);

        for (T value : values) {
            map.put(value.toString(), value);
        }
        return map;
    }
}

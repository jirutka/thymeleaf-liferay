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
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;
import org.thymeleaf.util.PrefixUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Attribute modifying processor for
 *
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class LiferayAttributeModifierProcessor extends AbstractStandardSingleAttributeModifierAttrProcessor {

    public LiferayAttributeModifierProcessor(String attributeName) {
        super(attributeName);
    }

    @Override
    protected String getTargetAttributeName(Arguments arguments, Element element, String attributeName) {
        return PrefixUtils.getUnprefixed(attributeName);
    }

    @Override
    protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }

    @Override
    protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return false;
    }

    @Override
    public int getPrecedence() {
        return 10005;
    }

    @Override
    protected String getTargetAttributeValue(Arguments arguments, Element element, String attributeName) {
        IWebContext context = (IWebContext) arguments.getContext();
        HttpServletRequest request = context.getHttpServletRequest();

        String attributeValue = element.getAttributeValue(attributeName);
        Map<String,Object> newLocalVariables = LiferayURLUtil.parseParams(arguments, attributeValue);

        LiferayPortletURL portletURL = LiferayURLUtil.createUrl(newLocalVariables, request);

        return portletURL.toString();
    }
}

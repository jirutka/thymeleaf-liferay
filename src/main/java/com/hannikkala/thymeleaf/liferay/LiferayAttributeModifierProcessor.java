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

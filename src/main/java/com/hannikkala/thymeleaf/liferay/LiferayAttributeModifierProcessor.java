package com.hannikkala.thymeleaf.liferay;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;
import org.thymeleaf.util.PrefixUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Attribute modifying processor for
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

        final String attributeValue = element.getAttributeValue(attributeName);

        final AssignationSequence assignations =
                StandardExpressionProcessor.parseAssignationSequence(
                        arguments, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException(
                    "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }

        final Map<String,Object> newLocalVariables = new HashMap<String,Object>(assignations.size() + 1, 1.0f);
        for (final Assignation assignation : assignations) {

            final String varName = assignation.getLeft().getValue();
            final Expression expression = assignation.getRight();
            final Object varValue = StandardExpressionProcessor.executeExpression(arguments, expression);

            newLocalVariables.put(varName, varValue);

        }

        LiferayURLUtil urlUtil = new LiferayURLUtil("liferay");
        LiferayPortletURL portletURL = urlUtil.createUrl(newLocalVariables, request);

        return portletURL.toString();
    }
}

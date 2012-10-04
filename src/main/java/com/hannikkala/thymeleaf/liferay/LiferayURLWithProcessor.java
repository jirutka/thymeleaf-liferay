/**
 * 
 */
package com.hannikkala.thymeleaf.liferay;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.attr.AbstractLocalVariableDefinitionAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;

/**
 * Processor for portlet url attribute.
 * 
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 *
 */
public class LiferayURLWithProcessor extends
		AbstractLocalVariableDefinitionAttrProcessor {

	public LiferayURLWithProcessor() {
		super("with");
	}
	
	/* (non-Javadoc)
	 * @see org.thymeleaf.processor.attr.AbstractLocalVariableDefinitionAttrProcessor#getNewLocalVariables(org.thymeleaf.Arguments, org.thymeleaf.dom.Element, java.lang.String)
	 */
	@Override
	protected Map<String, Object> getNewLocalVariables(Arguments arguments,
			Element element, String attributeName) {
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

		String varName = null;
		if(newLocalVariables.containsKey("var")) {
			varName = newLocalVariables.get("var").toString();
			newLocalVariables.remove("var");
		}
		
		LiferayURLUtil urlUtil = new LiferayURLUtil("liferay");
		LiferayPortletURL portletURL = urlUtil.createUrl(newLocalVariables, request);
		
		Map<String, Object> newVariables = new HashMap<String, Object>();
		if(varName != null) {
			newVariables.put(varName, portletURL.toString());
		} else {
			throw new TemplateProcessingException("liferay:with must contain 'var' variable for new variable definition.");
		}
		
		return newVariables;
	}

	/* (non-Javadoc)
	 * @see org.thymeleaf.processor.AbstractProcessor#getPrecedence()
	 */
	@Override
	public int getPrecedence() {
		return 13000;
	}

}

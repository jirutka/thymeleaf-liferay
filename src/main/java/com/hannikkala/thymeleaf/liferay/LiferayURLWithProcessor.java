/**
 * 
 */
package com.hannikkala.thymeleaf.liferay;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.attr.AbstractLocalVariableDefinitionAttrProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Processor for portlet url attribute.
 * 
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 *
 */
public class LiferayURLWithProcessor extends AbstractLocalVariableDefinitionAttrProcessor {

    public LiferayURLWithProcessor() {
        super("with");
    }
    
    /* (non-Javadoc)
     * @see org.thymeleaf.processor.attr.AbstractLocalVariableDefinitionAttrProcessor#getNewLocalVariables(org.thymeleaf.Arguments, org.thymeleaf.dom.Element, java.lang.String)
     */
    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments, Element element, String attributeName) {
        IWebContext context = (IWebContext) arguments.getContext();
        HttpServletRequest request = context.getHttpServletRequest();

        String attributeValue = element.getAttributeValue(attributeName);
        Map<String,Object> newLocalVariables = LiferayURLUtil.parseParams(arguments, attributeValue);

        String varName = null;
        if (newLocalVariables.containsKey("var")) {
            varName = newLocalVariables.remove("var").toString();
        }

        LiferayPortletURL portletURL = LiferayURLUtil.createUrl(newLocalVariables, request);
        
        Map<String, Object> newVariables = new HashMap<String, Object>();
        if (varName != null) {
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

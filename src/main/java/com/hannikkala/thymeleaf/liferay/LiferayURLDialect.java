/**
 * 
 */
package com.hannikkala.thymeleaf.liferay;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

/**
 * Dialect to add Liferay URL namespace to Thymeleaf.
 * 
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 *
 */
public class LiferayURLDialect extends AbstractDialect {

	/* (non-Javadoc)
	 * @see org.thymeleaf.dialect.IDialect#getPrefix()
	 */
	public String getPrefix() {
		return "liferay";
	}

	/* (non-Javadoc)
	 * @see org.thymeleaf.dialect.IDialect#isLenient()
	 */
	public boolean isLenient() {
		return false;
	}

	@Override
	public Set<IProcessor> getProcessors() {
		Set<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new LiferayURLWithProcessor());
        processors.add(new LiferayAttributeModifierProcessor("src"));
        processors.add(new LiferayAttributeModifierProcessor("href"));
        processors.add(new LiferayAttributeModifierProcessor("value"));
        processors.add(new LiferayAttributeModifierProcessor("action"));
		return processors;
	}
}

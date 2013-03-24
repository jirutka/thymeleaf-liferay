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

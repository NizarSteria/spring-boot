/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.condition;

import java.util.Map;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link org.springframework.context.annotation.Condition Condition} that
 * checks if a property has a given value. Can also be configured so that
 * the value to check is the default, hence the absence of property matches
 * as well.
 *
 * @author Stephane Nicoll
 * @since 1.2.0
 * @see ConditionalOnPropertyValue
 */
public class OnPropertyValueCondition extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Map<String, Object> attributes = metadata
				.getAnnotationAttributes(ConditionalOnPropertyValue.class.getName());
		String prefix = ((String) attributes.get("prefix")).trim();
		String property = (String) attributes.get("property");
		Boolean relaxedName = (Boolean) attributes.get("relaxedName");
		String value = (String) attributes.get("value");
		Boolean defaultValue = (Boolean) attributes.get("defaultValue");

		PropertyHelper helper = new PropertyHelper(context.getEnvironment(), prefix, relaxedName);
		String propertyKey = helper.createPropertyKey(property);
		String actualValue = helper.getPropertyResolver().getProperty(propertyKey);

		if (actualValue == null && defaultValue) {
			return ConditionOutcome.match("@ConditionalOnProperty no property '" + property
					+ "' is set, assuming default value '" + value + "'");
		}
		if (!value.equalsIgnoreCase(actualValue)) {
			return ConditionOutcome.noMatch("@ConditionalOnProperty wrong value for property '"
					+ property + "': expected '" + value + "' but got '" + actualValue + "'");
		}
		return ConditionOutcome.match();
	}

}

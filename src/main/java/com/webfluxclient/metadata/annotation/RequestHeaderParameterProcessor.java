/*
 * Copyright 2013-2015 the original author or authors.
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

package com.webfluxclient.metadata.annotation;

import com.webfluxclient.metadata.MethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class RequestHeaderParameterProcessor implements AnnotatedParameterProcessor {

	@Override
	public Class<? extends Annotation> getAnnotationType() {
		return RequestHeader.class;
	}

	@Override
	public void processAnnotation(MethodMetadata.Builder requestTemplateBuilder, Annotation annotation, Integer integer, Type parameterType) {
		RequestHeader requestParam = RequestHeader.class.cast(annotation);
		String name = requestParam.value();
		Assert.isTrue(StringUtils.hasText(name), "");

		requestTemplateBuilder.addHeader(integer, name);
	}

}
package com.auth.testlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

//	 @Bean
//	    public Docket api(){
//	        return new Docket(DocumentationType.SWAGGER_2)
//	            .select()
//	            .apis(RequestHandlerSelectors.any())
//	            .paths(PathSelectors.any())
//	            .build()
//	            .apiInfo(apiInfo());
//	    }
//
//	    private ApiInfo apiInfo() {
//	        return new ApiInfoBuilder()
//	            .title("TITLE")
//	            .description("DESCRIPTION")
//	            .version("VERSION")
//	            .termsOfServiceUrl("http://terms-of-services.url")
//	            .license("LICENSE")
//	            .licenseUrl("http://url-to-license.com")
//	            .build();
//	    }
@Bean
public Docket api() {
	//Adding Header
	ParameterBuilder aParameterBuilder = new ParameterBuilder();
	aParameterBuilder.name("Authorization")
			.modelRef(new ModelRef("string"))
			.parameterType("header")
			.defaultValue("Bearer + token")
			.required(true)
			.build();
	List<Parameter> aParameters = new ArrayList<>();
	aParameters.add(aParameterBuilder.build());
	return new Docket(DocumentationType.SWAGGER_2).select()
			.apis(RequestHandlerSelectors
					.any())
			.paths(PathSelectors.any())
			.build().
					pathMapping("")
			.globalOperationParameters(aParameters);
}
}

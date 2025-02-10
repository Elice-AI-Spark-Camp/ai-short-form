package elice.aishortform.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		/*todo
		* 1. JWT 토큰을 이용한 인증 설정(필요하다면)
		* 2. OpenAPI 정보 설정
		 */

		// SecurityScheme apiKey = new SecurityScheme()
		// 	.type(SecurityScheme.Type.HTTP)  // HTTP 기반 인증 사용
		// 	.in(SecurityScheme.In.HEADER)    // 인증 정보를 HTTP 헤더에서 받음
		// 	.name("Authorization")           // 헤더 이름: "Authorization"
		// 	.scheme("bearer")                // 인증 방식: Bearer (JWT 방식)
		// 	.bearerFormat("JWT");            // 토큰 형식: JWT
		//
		// SecurityRequirement securityRequirement = new SecurityRequirement()
		// 	.addList("Bearer Token");


		return new OpenAPI()
			.components(new Components()/*.addSecuritySchemes("Bearer Token", apiKey)*/)
			// .addSecurityItem(securityRequirement)
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("Ai Short Form API")
			.description("AI 숏폼 제작 API")
			.version("1.0");
	}
}

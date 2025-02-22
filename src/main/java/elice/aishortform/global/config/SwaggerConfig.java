package elice.aishortform.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		/*todo
		* 1. JWT 토큰을 이용한 인증 설정(필요하다면)
		* 2. OpenAPI 정보 설정
		 */


		return new OpenAPI()
			.components(new Components())
			.info(apiInfo())
			.servers(serverList());
	}

	private Info apiInfo() {
		return new Info()
			.title("Ai Short Form API")
			.description("AI 숏폼 제작 API")
			.version("1.0");
	}

	private List<Server> serverList() {
		return List.of(
			new Server().url("http://localhost:8000").description("로컬 개발 서버"),  // ✅ 로컬 서버 URL
			new Server().url("https://ccqapyxttsnqmhxx.tunnel-pt.elice.io").description("Elice Cloud 서버"),  // ✅ 배포된 서버 URL
			new Server().url("https://snapsum.vercel.app").description("Vercel 서버"),  // ✅ 배포된 서버 URL
			new Server().url("localhost:3000").description("로컬 개발 서버")  // ✅ 배포된 서버 URL

		);
	}
}

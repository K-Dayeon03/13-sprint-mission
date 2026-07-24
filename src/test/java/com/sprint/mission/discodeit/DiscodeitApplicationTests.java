package com.sprint.mission.discodeit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:discodeit-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.sql.init.mode=never",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"discodeit.storage.type=local",
		"discodeit.storage.local.root-path=${java.io.tmpdir}/discodeit-test-binary-contents"
})
class DiscodeitApplicationTests {

	@Test
	void contextLoads() {
	}

}

use crate::template;
use openapiv3;

pub const DOCKER_ENTRYPOINT: &str = r#"[ "java", "-jar", "/opt/service/service.jar", "--server.port=80" ]"#;
pub struct Faker;

impl template::Fakeable for Faker {
    fn get_string_fake(&self, string_validation: &openapiv3::StringType) -> template::FakeFunction {
        template::FakeFunction::new(
            "faker.lorem().characters".to_string(),
            format!(
                "{}, {}, true, true",
                string_validation.min_length.unwrap_or(5),
                string_validation.max_length.unwrap_or(20)
            ),
        )
    }

    fn get_number_fake(&self, number_validation: &openapiv3::NumberType) -> template::FakeFunction {
        template::FakeFunction::new(
            "faker.number().randomDouble".to_string(),
            format!(
                "{}, (long){}, (long){}",
                2,
                number_validation.minimum.map_or(0.0, |m| m as f64),
                number_validation.maximum.map_or(5000.0, |m| m as f64)
            ),
        )
    }

    fn get_integer_fake(
        &self,
        integer_validation: &openapiv3::IntegerType,
    ) -> template::FakeFunction {
        template::FakeFunction::new(
            "faker.number().numberBetween".to_string(),
            format!(
                "{}, {}", // min, max
                integer_validation.minimum.map_or(0, |m| m as i64),
                integer_validation.maximum.map_or(9999, |m| m as i64)
            ),
        )
    }

    fn get_object_fake(&self, function_name: &str) -> template::FakeFunction {
        template::FakeFunction::new(
            format!("this.{}", function_name),
            String::new(),
        )
    }
    fn get_array_fake(&self, function_name: &str) -> template::FakeFunction {
        template::FakeFunction::new(
            format!("this.{}", function_name),
            String::new(),
        )
    }

    fn get_boolean_fake(
        &self,
        _boolean_validation: &openapiv3::BooleanType,
    ) -> template::FakeFunction {
        template::FakeFunction::new(
            "faker.bool().bool".to_string(),
            String::new(),
        )
    }
}

pub struct RouterGenerator;
impl template::RouterGenerator for RouterGenerator {
    fn create_router_template(&self) -> template::RouterTemplate {
        template::RouterTemplate {
            template_dir: "java/spring_boot/router",
            root_template_name: "router",
        }
    }
}

pub struct ServiceCallGenerator;
impl template::ServiceCallGenerator for ServiceCallGenerator {
    fn create_service_call_template(&self) -> template::ServiceCallTemplate {
        template::ServiceCallTemplate {
            template_dir: "java/spring_boot/service_calls",
            root_template_name: "service_calls",
        }
    }
}

pub struct MainGenerator;
impl template::MainGenerator for MainGenerator {
    fn create_main_template(&self) -> template::MainTemplate {
        template::MainTemplate {
            template_dir: "java/spring_boot",
            root_template_name: "main",
            auxiliry_template_names: &[template::AuxiliryTemplate {
                template_name: "http_client",
                file_name: "src/HttpClientConfig.java",
            }],
        }
    }
}

pub fn get_framework_dependencies() -> Vec<&'static str> {
    vec![
        r#"<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId><version>3.2.5</version></dependency>"#,
        r#"<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId><version>3.2.5</version></dependency>"#,
        r#"<dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-databind</artifactId><version>2.17.1</version></dependency>"#,
        r#"<dependency><groupId>com.fasterxml.jackson.datatype</groupId><artifactId>jackson-datatype-jsr310</artifactId><version>2.17.1</version></dependency>"#,
        r#"<dependency><groupId>com.fasterxml.jackson.module</groupId><artifactId>jackson-module-parameter-names</artifactId><version>2.17.1</version></dependency>"#,
        r#"<dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-core</artifactId><version>2.17.1</version></dependency>"#,
        r#"<dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-annotations</artifactId><version>2.17.1</version></dependency>"#,
        r#"<dependency><groupId>com.github.javafaker</groupId><artifactId>javafaker</artifactId><version>1.0.2</version></dependency>"#,
        r#"<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><version>3.2.5</version><scope>test</scope></dependency>"#,
    ]
}

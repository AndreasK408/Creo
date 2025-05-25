use rand_derive::Rand;

use crate::{generator::core::FrameworkGenerator, template};

pub mod springboot;

#[derive(Rand)]
pub enum Frameworks {
    SpringBoot,
}
use Frameworks::*;

impl FrameworkGenerator for Frameworks {
    fn to_faker(&self) -> &dyn template::Fakeable {
        match self {
            SpringBoot => &springboot::Faker,
        }
    }

    fn to_router_generator(&self) -> &dyn template::RouterGenerator {
        match self {
            SpringBoot => &springboot::RouterGenerator,
        }
    }

    fn to_service_calls_generator(&self) -> &dyn template::ServiceCallGenerator {
        match self {
            SpringBoot => &springboot::ServiceCallGenerator,
        }
    }

    fn to_main_generator(&self) -> &dyn template::MainGenerator {
        match self {
            SpringBoot => &springboot::MainGenerator,
        }
    }

    fn get_framework_requirements(&self) -> Vec<&'static str> {
        match self {
            SpringBoot => springboot::get_framework_dependencies(),
        }
    }

    fn get_docker_entrypoint(&self) -> &'static str {
        match self {
            SpringBoot => springboot::DOCKER_ENTRYPOINT,
        }
    }
}
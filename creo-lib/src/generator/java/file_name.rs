use crate::generator::core::{self, FileName};

pub struct FileNameGenerator;

impl core::FileNameGenerator for FileNameGenerator {
    fn generate_router_file_name(&self) -> FileName {
        FileName {
            path: "src/ServiceController",
            extension: "java",
        }
    }

    fn generate_service_call_file_name(&self) -> FileName {
        FileName {
            path: "src/ServiceCallsClient",
            extension: "java",
        }
    }

    fn generate_main_file_name(&self) -> FileName {
        FileName {
            path: "src/Application",
            extension: "java",
        }
    }
}

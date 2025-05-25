pub mod data_type;
pub mod file_name;
pub mod local_deps;
pub mod symbol;
pub mod frameworks;

pub use data_type::DataTypeMapper;
pub use file_name::FileNameGenerator;
pub use frameworks::Frameworks;
pub use local_deps::get_local_handler_dependencies;
pub use symbol::SymbolGenerator;

pub const DOCKERFILE_TEMPLATE_PATH: &str = "java/Dockerfile.mgt";
pub const DEPENDENCY_FILE_NAME: &str = "pom.xml";
pub const DEPENDENCY_FILE_TEMPLATE_PATH: &str = "java/pom.mgt";

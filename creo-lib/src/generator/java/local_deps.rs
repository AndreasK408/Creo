use std::fs;
use std::io;
use std::path::Path;

const HANDLER_GROUP_ID_PREFIX: &str = "creo.handlers";
const HANDLER_VERSION: &str = "1.0-SNAPSHOT";

pub fn get_local_handler_dependencies(
    lib_dir: impl AsRef<Path>,
) -> io::Result<Vec<String>> {
    let mut deps = Vec::new();
    for entry in fs::read_dir(lib_dir.as_ref())? {
        let entry = entry?;
        let path = entry.path();

        if path.is_dir() {
            if let Some(handler_name_osstr) = path.file_name() {
                if let Some(handler_name) = handler_name_osstr.to_str() {
                    let artifact_id = format!("{}-handler", handler_name);
                    let jar_file_name = format!("{}-{}.jar", artifact_id, HANDLER_VERSION);
                    let system_path = format!(
                        "${{project.basedir}}/lib/{}/target/{}",
                        handler_name,
                        jar_file_name
                    );

                    let dep_xml = format!(
                        r#"
    <dependency>
        <groupId>{}</groupId>
        <artifactId>{}</artifactId>
        <version>{}</version>
        <scope>system</scope>
        <systemPath>{}</systemPath>
    </dependency>"#,
                        HANDLER_GROUP_ID_PREFIX,
                        artifact_id,
                        HANDLER_VERSION,
                        system_path
                    );
                    deps.push(dep_xml.trim().to_string());
                } else {
                    log::warn!("Handler directory name {:?} is not valid UTF-8, skipping.", path);
                }
            }
        } else {
            log::debug!("Skipping non-directory entry in lib: {:?}", path);
        }
    }

    Ok(deps)
}
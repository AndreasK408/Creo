use crate::{
    application::ServiceCallEdge,
    generator::core,
    graph::EndpointIndex,
};
use heck::{ToLowerCamelCase, ToUpperCamelCase};
pub struct SymbolGenerator;

impl core::SymbolGenerator for SymbolGenerator {
    fn generate_array_item_function_name(&self, name: &str) -> String {
        format!("generate{}Item", name.to_upper_camel_case())
    }
    fn generate_object_property_function_name(&self, name: &str, prop_name: &str) -> String {
        format!(
            "generate{}Property{}",
            name.to_upper_camel_case(),
            prop_name.to_upper_camel_case()
        )
    }

    fn generate_service_calls_function_name(&self, endpoint: EndpointIndex) -> String {
        format!("serviceCallsForEndpoint{}", endpoint.0).to_lower_camel_case()
    }

    fn generate_service_call_function_import(
        &self,
        _file_path: &str,
        _function_name: &str,
    ) -> String {
        format!("import {}.ServiceCallsClient;", "com.creo.generated.service.calls")
    }

    fn generate_handler_function_import(&self, import_path: &str, function_name: &str) -> String {
        let base_import = format!("import static {}.{};", import_path.trim_end_matches(';'), function_name);
        if base_import.ends_with(";;") {
            base_import[..base_import.len()-1].to_string()
        } else if !base_import.ends_with(';') {
            format!("{};", base_import)
        }
        else {
            base_import
        }
    }

    fn generate_individual_service_call_function_name(
        &self,
        call: ServiceCallEdge,
    ) -> String {
        format!(
            "serviceCallFromEndpoint{}ToEndpoint{}",
            call.source.0, call.target.0
        ).to_lower_camel_case()
    }

    fn generate_operation_function_name(&self, endpoint: EndpointIndex) -> String {
        format!("handleEndpoint{}", endpoint.0).to_lower_camel_case()
    }

    fn generate_query_data_function_name(&self, service_call: ServiceCallEdge) -> String {
        format!(
            "queryDataForServiceCallFromEndpoint{}ToEndpoint{}",
            service_call.source.0, service_call.target.0
        ).to_lower_camel_case()
    }

    fn generate_parameter_function_name(
        &self,
        service_call: ServiceCallEdge,
        param_name: &str,
    ) -> String {
        format!(
            "payloadForServiceCallFromEndpoint{}ToEndpoint{}Param{}",
            service_call.source.0,
            service_call.target.0,
            param_name.to_upper_camel_case()
        ).to_lower_camel_case()
    }
}

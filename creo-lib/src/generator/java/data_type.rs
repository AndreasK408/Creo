use crate::{
    generator::core::{self, LanguageDataType},
    template::Import,
};

pub struct DataTypeMapper;

impl core::DataTypeMapper for DataTypeMapper {
    fn get_string_type(&self) -> &'static str { "String" }

    fn get_date_type(&self) -> LanguageDataType {
        LanguageDataType {
            type_name: "LocalDate".into(),
            import: Some(Import::new("import java.time.LocalDate;".into())),
        }
    }

    fn get_date_time_type(&self) -> LanguageDataType {
        LanguageDataType {
            type_name: "LocalDateTime".into(),
            import: Some(Import::new("import java.time.LocalDateTime;".into())),
        }
    }

    fn get_floating_point_number_type(&self) -> &'static str { "float" }

    fn get_double_type(&self) -> &'static str { "double" }

    fn get_signed_32_bit_integer_type(&self) -> &'static str { "int" }

    fn get_signed_64_bit_integer_type(&self) -> &'static str { "long" }

    fn get_boolean_type(&self) -> &'static str { "boolean" }
}

# 1. Define where your Java Entities are
data "external_schema" "hibernate" {
  program = [
    "sh",
    "-c",
    "mvn compile -q hibernate-provider:schema -Dproperties=schema-export.properties -Denable-table-generators=true | grep -Ei '^(create|alter|drop|comment|table)'"
  ]
}

env "hibernate" {
   src = data.external_schema.hibernate.url

   dev = "docker://postgres/15/dev?search_path=public"

   migration {
      dir = "file://src/main/resources/db/migration"

      format = flyway
   }
}
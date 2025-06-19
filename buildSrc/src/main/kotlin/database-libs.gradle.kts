plugins {
    id("regular-libs")
}

val hikariVersion: String by project
val h2Version: String by project
val mysqlVersion: String by project
val postgresVersion: String by project
val mariadbVersion: String by project
val mongodbVersion: String by project

dependencies {
    compileOnly("com.zaxxer:HikariCP:$hikariVersion")
    compileOnly("com.h2database:h2:$h2Version")
    compileOnly("com.mysql:mysql-connector-j:$mysqlVersion")
    compileOnly("org.postgresql:postgresql:$postgresVersion")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")
    compileOnly("org.mongodb:mongodb-driver-sync:$mongodbVersion")
}
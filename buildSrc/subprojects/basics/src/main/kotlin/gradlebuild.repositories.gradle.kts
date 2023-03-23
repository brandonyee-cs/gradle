/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

repositories {
    maven {
        name = "Gradle libs"
        url = uri("https://repo.gradle.org/gradle/libs")
        metadataSources {
            mavenPom()
            artifact()
        }
    }
    mavenCentral()
    google()
    jcenter() {
        content {
            includeVersion("org.asciidoctor", "asciidoctorj-groovy-dsl", "1.0.0.Alpha3")
            includeVersion("org.samba.jcifs", "jcifs", "1.3.17")
            includeVersionByRegex("org\\.sonatype\\.pmaven", "pmaven.*", "0\\.8-20100325")
        }
    }
}
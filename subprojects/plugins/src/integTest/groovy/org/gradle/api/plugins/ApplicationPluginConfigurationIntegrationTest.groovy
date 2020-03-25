/*
 * Copyright 2018 the original author or authors.
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

package org.gradle.api.plugins

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.ToBeFixedForInstantExecution
import org.gradle.integtests.fixtures.ScriptExecuter
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition
import org.gradle.util.TextUtil
import spock.lang.Unroll

class ApplicationPluginConfigurationIntegrationTest extends AbstractIntegrationSpec {
    @ToBeFixedForInstantExecution
    def "can configure using project extension"() {
        settingsFile << """
            rootProject.name = 'test'
        """

        file("src/main/java/test/Main.java") << """
            package test;
            public class Main {
                public static void main(String[] args) {
                    System.out.println("all good");
                }
            }
        """

        buildFile << """
            plugins {
                id("application")
            }
            application {
                mainClassName = "test.Main"
            }
        """

        when:
        run("installDist")

        def out = new ByteArrayOutputStream()
        def executer = new ScriptExecuter()
        executer.workingDir = testDirectory
        executer.standardOutput = out
        executer.commandLine = "build/install/test/bin/test"

        then:
        executer.run().assertNormalExitValue()
        out.toString() == TextUtil.toPlatformLineSeparators("all good\n")
    }

    @ToBeFixedForInstantExecution
    @Requires(TestPrecondition.JDK9_OR_LATER)
    @Unroll
    def "can configure using project extension for main class and main module"() {
        settingsFile << """
            rootProject.name = 'test'
        """

        file("src/main/java/test/Main.java") << """
            package test;
            public class Main {
                public static void main(String[] args) {
                    System.out.println("Module: " + Main.class.getModule().getName());
                }
            }
        """
        file("src/main/java/module-info.java") << "module test.main {}"

        buildFile << """
            plugins {
                id("application")
            }
            application {
                $configClass
                $configModule
            }
            compileJava {
                modularClasspathHandling.inferModulePath.set(true)
            }
            startScripts {
                modularClasspathHandling.inferModulePath.set(true)
            }
        """

        if (configClass == '') {
            // set the main class directly in the compile task
            buildFile << "compileJava { options.javaModuleMainClass.set('test.Main') }"
        }

        when:
        run("installDist")

        def out = new ByteArrayOutputStream()
        def executer = new ScriptExecuter()
        executer.workingDir = testDirectory
        executer.standardOutput = out
        executer.commandLine = "build/install/test/bin/test"

        then:
        executer.run().assertNormalExitValue()
        out.toString() == TextUtil.toPlatformLineSeparators("Module: $expectedModule\n")

        where:
        configClass                   | configModule                  | expectedModule
        "mainClassName = 'test.Main'" | ''                            | 'null'
        "mainClass.set('test.Main')"  | ''                            | 'null'
        "mainClass.set('test.Main')"  | "mainModule.set('test.main')" | 'test.main'
        ''                            | "mainModule.set('test.main')" | 'test.main'
    }
}
